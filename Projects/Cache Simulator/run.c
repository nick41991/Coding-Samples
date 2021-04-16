/*************************************************************************
/
/	filename: 	run.c
/
/	description: 	Creates a cache and uses a trace file to simulate program execution
/
/	authors: 	Smith, Nicholas
/
/	class:		CSE 331
/	instructor: 	Zheng
/	assignment: 	Lab #2
/
/	assigned: 	2/19/2019
/	due:		3/8/2019
/
/************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <limits.h>
#include <time.h> /*Used as seed for random replacement*/
#include "cache.h"

void print_help()
{
	printf("Usage: ./run.c [CONFIG] [TRACE]\n");
	printf("Where:\n[CONFIG] is a .conf file\n[TRACE] is a .trace file\n");
	exit(-1);
}

int main(int argc, char **argv)
{

	int i;
	srand(time(0)); /*Use the time as seed for random, allows for varied output with random replacement*/

	if(argc != 3 || strstr(argv[1], ".conf") == NULL || strstr(argv[2], ".trace") == NULL){
		print_help();
	}
	FILE *conf = fopen(argv[1], "r");
	/*fopen check*/
	if (conf == NULL){
		printf("Error: could not open configuration file %s\n", argv[1]);
		print_help();
	}


	/*Create new cache based on conf*/
	struct cache_t *my_cache = malloc(1 * sizeof(struct cache_t));
	my_cache = new_cache(my_cache, conf);
	FILE *trace = fopen(argv[2], "r");
	/*fopen check*/
	if (trace == NULL){
		printf("Error: could not open configuration file %s", argv[2]);
		print_help();
	}


	char instr;
	char *addr;
	char buff[256];
	int time = 0; /*Keeps track of memory operation occuring for FIFO*/

	/*Store Stats*/
	int stores = 0;
	int store_hits = 0;
	int store_miss = 0;

	/*Load Stats*/
	int loads = 0;
	int load_hits = 0;
	int load_miss = 0;
	int e = 0;

	/*Cycle Counts*/
	int intermediate = 0; /*Keeps track of cycles (1 / ins) between memory accesses*/
	int mem_access = 0; /*Keeps track of cycles taken for memory accesses*/

	while(fgets(buff, 256, trace)){
		/*Parse input from trace file*/
		instr = strtok(buff, " ")[0];
		addr = strtok(NULL, " ");
		intermediate += atoi(strtok(NULL, " "));

		struct address_t *add = new_address(addr, my_cache->line_bits, my_cache->set_bits);

		switch(instr){
			case 's':
				/*Cache Hit or Miss*/
				stores++;
				switch(my_cache->associativity){
				case 1: /*Direct Mapped - block can be in set[0].cache_lines[add->set]*/
					if(my_cache->cache_sets[0].cache_lines[add->set].tag == add->tag){
						/*Hit*/
						store_hits++;
						mem_access++; /*1 cycle / hit*/
					} else {
						/*Since add can only map to the line specified by add-> set, count as miss and replace*/
						if(my_cache->write_alloc == 1){ /*Load data to cache on write miss*/
							my_cache->cache_sets[0].cache_lines[add->set].tag = add->tag;
							my_cache->cache_sets[0].cache_lines[add->set].valid = 1;
							store_miss++;
							mem_access += my_cache->miss_penalty;
						} else { /*No allocate on write*/
							store_miss++;
							mem_access += my_cache->miss_penalty;
						}
					}
					break;
				case 0: /*Fully Associative - block can be anywhere in cache*/
					/*First verify block is in cache*/
					; /*Avoid not a statement error*/
					int flag1 = 0;

					for(i = 0; i < my_cache->num_lines; i++){
						if(my_cache->cache_sets[i].cache_lines[0].tag == add->tag){
							flag1 = 1;
							break;
						}
					}
					if(flag1 == 1){ /*hit*/
						store_hits++;
						mem_access++; /*1 cycle / hit*/

					} else { /*miss bring data to cache*/
						if(my_cache->write_alloc == 1){
							int j;
							int flag2 = 0;
							for(j = 0; j < my_cache->num_lines; j++){
								if(my_cache->cache_sets[j].cache_lines[0].valid != 1){
									flag2 = 1;
									break;
								}
							}
							if(flag2 == 1){ /*found empty line at j*/
								my_cache->cache_sets[j].cache_lines[0].tag = add->tag;
								my_cache->cache_sets[j].cache_lines[0].valid = 1;
								my_cache->cache_sets[j].cache_lines[0].timestamp = time;
								store_miss++;
								mem_access += my_cache->miss_penalty;
							} else { /*cache is full. time to evict*/

								if(my_cache->replacement == 1){ /*FIFO policy*/
									int k;
									int first = INT_MAX; /*Arbitrarily high*/
									int first_place = 0;
									for(k = 0; k < my_cache->num_lines; k++){
										if(my_cache->cache_sets[k].cache_lines[0].timestamp < first){
											first = my_cache->cache_sets[k].cache_lines[0].timestamp;
											first_place = k;
										}
									}
									my_cache->cache_sets[first_place].cache_lines[0].tag = add->tag;
									my_cache->cache_sets[first_place].cache_lines[0].valid = 1;
									my_cache->cache_sets[first_place].cache_lines[0].timestamp = time;
									store_miss++;
									mem_access += my_cache->miss_penalty;

								} else { /*Random replacement*/
									int ran = rand();
									int place = ran % my_cache->num_lines;
									my_cache->cache_sets[place].cache_lines[0].tag = add->tag;
									my_cache->cache_sets[place].cache_lines[0].valid = 1;
									my_cache->cache_sets[place].cache_lines[0].timestamp = time;
									store_miss++;
									mem_access += my_cache->miss_penalty;
								}
							}
						} else {
							store_miss++;
							mem_access += my_cache->miss_penalty;
						}
					}

					break;
				default: /*n way associativity*/
					; /*Avoid not a statement error*/
					int flag3 = 0;

					for(i = 0; i < my_cache->lines_per_set; i++){ /*attempt to find line in set*/
						if(my_cache->cache_sets[add->set].cache_lines[i].tag == add->tag){
							flag3 = 1;
							break;
						}
					}
					if(flag3 == 1){ /*hit*/
						store_hits++;
						mem_access++; /*1 cycle / hit*/

					} else { /*miss, bring data to cache*/
						if(my_cache->write_alloc == 1){
							int j;
							int flag4 = 0;
							for(j = 0; j < my_cache->lines_per_set; j++){/*Attempt to find empty line in set*/
								if(my_cache->cache_sets[add->set].cache_lines[j].valid != 1){
									flag4 = 1;
									break;
								}
							}
							if(flag4 == 1){ /*found empty line at j*/
								my_cache->cache_sets[add->set].cache_lines[j].tag = add->tag;
								my_cache->cache_sets[add->set].cache_lines[j].valid = 1;
								my_cache->cache_sets[add->set].cache_lines[j].timestamp = time;
								store_miss++;
								mem_access += my_cache->miss_penalty;
							} else { /*set is full. time to evict*/

								if(my_cache->replacement == 1){ /*FIFO policy*/
									int k;
									int first = INT_MAX; /*Arbitrarily high*/
									int first_place = 0;
									for(k = 0; k < my_cache->lines_per_set; k++){
										if(my_cache->cache_sets[add->set].cache_lines[k].timestamp < first){
											first = my_cache->cache_sets[add->set].cache_lines[k].timestamp;
											first_place = k;
										}
									}
									my_cache->cache_sets[add->set].cache_lines[first_place].tag = add->tag;
									my_cache->cache_sets[add->set].cache_lines[first_place].valid = 1;
									my_cache->cache_sets[add->set].cache_lines[first_place].timestamp = time;
									store_miss++;
									mem_access += my_cache->miss_penalty;

								} else { /*Random replacement*/
									int ran = rand();
									int place = ran % my_cache->lines_per_set;
									my_cache->cache_sets[add->set].cache_lines[place].tag = add->tag;
									my_cache->cache_sets[add->set].cache_lines[place].valid = 1;
									my_cache->cache_sets[add->set].cache_lines[place].timestamp = time;
									store_miss++;
									mem_access += my_cache->miss_penalty;
								}
							}
						} else {
							store_miss++;
							mem_access += my_cache->miss_penalty;
						}
					}
					break;
				}
				break;
			case 'l':
				loads++;

				switch(my_cache->associativity){
				case 1: /*Direct Mapped - block can be in set[0].cache_lines[add->set]*/
					if(my_cache->cache_sets[0].cache_lines[add->set].tag == add->tag){
						/*Hit*/
						load_hits++;
						mem_access++; /*1 cycle / hit*/
					} else {
						/*Since add can only map to the line specified by add-> set, count as miss and replace*/
						my_cache->cache_sets[0].cache_lines[add->set].tag = add->tag;
						my_cache->cache_sets[0].cache_lines[add->set].valid = 1;
						load_miss++;
						mem_access += my_cache->miss_penalty;
					}
					break;
				case 0: /*Fully Associative - block can be anywhere in cache*/
					/*First verify block is in cache*/
					; /*Avoid not a statement error*/
					int flag1 = 0;

					for(i = 0; i < my_cache->num_lines; i++){
						if(my_cache->cache_sets[i].cache_lines[0].tag == add->tag){
							flag1 = 1;
							break;
						}
					}
					if(flag1 == 1){ /*hit*/
						load_hits++;
						mem_access++; /*1 cycle / hit*/

					} else { /*miss bring data to cache*/
						int j;
						int flag2 = 0;
						for(j = 0; j < my_cache->num_lines; j++){
							if(my_cache->cache_sets[j].cache_lines[0].valid != 1){
								flag2 = 1;
								break;
							}
						}
						if(flag2 == 1){ /*found empty line at j*/
							my_cache->cache_sets[j].cache_lines[0].tag = add->tag;
							my_cache->cache_sets[j].cache_lines[0].valid = 1;
							my_cache->cache_sets[j].cache_lines[0].timestamp = time;
							load_miss++;
							mem_access += my_cache->miss_penalty;
						} else { /*cache is full. time to evict*/

							if(my_cache->replacement == 1){ /*FIFO policy*/
								int k;
								int first = INT_MAX; /*Arbitrarily high*/
								int first_place = 0;
								for(k = 0; k < my_cache->num_lines; k++){
									if(my_cache->cache_sets[k].cache_lines[0].timestamp < first){
										first = my_cache->cache_sets[k].cache_lines[0].timestamp;
										first_place = k;
									}
								}
								my_cache->cache_sets[first_place].cache_lines[0].tag = add->tag;
								my_cache->cache_sets[first_place].cache_lines[0].valid = 1;
								my_cache->cache_sets[first_place].cache_lines[0].timestamp = time;
								load_miss++;
								mem_access += my_cache->miss_penalty;
								e++;

							} else { /*Random replacement*/
								int ran = rand();
								int place = ran % my_cache->num_lines;
								my_cache->cache_sets[place].cache_lines[0].tag = add->tag;
								my_cache->cache_sets[place].cache_lines[0].valid = 1;
								my_cache->cache_sets[place].cache_lines[0].timestamp = time;
								load_miss++;
								mem_access += my_cache->miss_penalty;
							}
						}
					}

					break;
				default: /*n way associativity*/
					; /*Avoid not a statement error*/
					int flag3 = 0;

					for(i = 0; i < my_cache->lines_per_set; i++){ /*attempt to find line in set*/
						if(my_cache->cache_sets[add->set].cache_lines[i].tag == add->tag){
							flag3 = 1;
							break;
						}
					}
					if(flag3 == 1){ /*hit*/
						load_hits++;
						mem_access++; /*1 cycle / hit*/

					} else { /*miss, bring data to cache*/
						int j;
						int flag4 = 0;
						for(j = 0; j < my_cache->lines_per_set; j++){/*Attempt to find empty line in set*/
							if(my_cache->cache_sets[add->set].cache_lines[j].valid != 1){
								flag4 = 1;
								break;
							}
						}
						if(flag4 == 1){ /*found empty line at j*/
							my_cache->cache_sets[add->set].cache_lines[j].tag = add->tag;
							my_cache->cache_sets[add->set].cache_lines[j].valid = 1;
							my_cache->cache_sets[add->set].cache_lines[j].timestamp = time;
							load_miss++;
							mem_access += my_cache->miss_penalty;
						} else { /*set is full. time to evict*/

							if(my_cache->replacement == 1){ /*FIFO policy*/
								int k;
								int first = INT_MAX; /*Arbitrarily high*/
								int first_place = 0;
								for(k = 0; k < my_cache->lines_per_set; k++){
									if(my_cache->cache_sets[add->set].cache_lines[k].timestamp < first){
										first = my_cache->cache_sets[add->set].cache_lines[k].timestamp;
										first_place = k;
									}
								}
								my_cache->cache_sets[add->set].cache_lines[first_place].tag = add->tag;
								my_cache->cache_sets[add->set].cache_lines[first_place].valid = 1;
								my_cache->cache_sets[add->set].cache_lines[first_place].timestamp = time;
								load_miss++;
								mem_access += my_cache->miss_penalty;

							} else { /*Random replacement*/
								int ran = rand();
								int place = ran % my_cache->lines_per_set;
								my_cache->cache_sets[add->set].cache_lines[place].tag = add->tag;
								my_cache->cache_sets[add->set].cache_lines[place].valid = 1;
								my_cache->cache_sets[add->set].cache_lines[place].timestamp = time;
								load_miss++;
								mem_access += my_cache->miss_penalty;
							}
						}
					}
					break;
				}


				break;
			default:
				printf("Error in Trace file, expected load (l) or store (s) instruction but got '%c'", instr);
				print_help();
		}

		/*Address debugging info*/
		//printf("%u\t%u\t%u\t%u\n", add->line + add->set + add->tag, add->line, add->set, add->tag);
		free(add);
		time++;
	}

	/*Cache info*/
	//print_cache(my_cache);
	//puts("");

	/*Hit ^ Miss Statistics*/
	printf("%f\n", (float)(load_hits + store_hits) / (loads + stores)); /*Total hit rate*/
	printf("%f\n", (float)(load_hits) / (loads)); /*Load Hit Rate*/
	printf("%f\n", (float)(store_hits) / (stores)); /*Store Hit Rate*/
	printf("%d\n", mem_access + intermediate); /*Total Run Time (Cycles)*/
	printf("%f\n", (float)mem_access / (loads + stores)); /*Average Memory Access Latency*/
	free_cache(my_cache);
	free(conf);
	free(trace);
	return 0;
}
