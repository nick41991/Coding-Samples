/*************************************************************************
/
/	filename: 	cache.c
/
/	description: 	Implements cache initializers and cache functions
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

#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include "cache.h"

/**
 * Converts a memory address string to a struct address_t structure based off of cache parameters
 * @param address the address to be converted
 * @param block_bits the number of bits that represent the block in the address
 * @param set_bits the number of bits used to identify the set in the address
 */
struct address_t *new_address(char *address, int line_bits, int set_bits)
{
	struct address_t *new = malloc(sizeof(struct address_t));
	unsigned int addr = strtol(address, NULL, 16);
	new->line = addr % (int)pow(2,line_bits);
	new->set = (addr - new->line) % (int)pow(2, set_bits);
	new->tag = addr - (new->line + new->set);

	return new;
}

/**
 * Allocates space for the sets and lines in the cache
 * @param num_sets the number of sets to make
 * @param lines_per_set
 * return the new set of sets of lines
 */
struct set_t *new_sets(int num_sets, int lines_per_set)
{
	struct set_t *sets = malloc(num_sets * sizeof(struct line_t *));
	int i;
	for(i = 0; i < num_sets; i++){
		sets[i].cache_lines = malloc(lines_per_set * sizeof(struct line_t));
	}
	return sets;
}

/**
 * Initializes a cache structure based on a .conf FILE
 * @param new the cache to be initialized
 * @param conf the config to be Used
 * return the initialized cache
 */
struct cache_t *new_cache(struct cache_t *new, FILE *conf)
{
	char buffer[256];
	int i = 0;

	/*Initializers read from conf, a six line file*/
	while(fgets(buffer, 256, conf)){
		switch(i){
		case 0:
			new->line_size = atoi(buffer);
			break;
		case 1:
			new->associativity = atoi(buffer);
			break;
		case 2:
			new->data_size = atoi(buffer);
			break;
		case 3:
			new->replacement = atoi(buffer);
			break;
		case 4:
			new->miss_penalty = atoi(buffer);
			break;
		case 5:
			new->write_alloc = atoi(buffer);
			break;
		default: /*If we reach here, there is an error in file format*/
			printf("Error: configuration file format does not conform to expectations");
			exit(-1);
		}
		i++;
	}

	/*Memory address info*/
	new->line_bits = (int)(log(new->line_size)/log(2)); /*change of base formula*/

	new->num_lines= new->data_size * 1024 / new->line_size; /*#KB's * 1024 bytes/KB / bytes per line*/

	if(new->associativity > 1) {/*Avoid div by 0*/
		new->num_sets = new->num_lines / new->associativity; /*#blocks / #ways*/
		new->set_bits = (int)(log(new->num_sets) / log(2));

	} else if (new->associativity == 0) {
		new->num_sets = new->num_lines; /*Treat cache as n set*/
		new->set_bits = 0; /*Fully Associative cache- Tags used to id blocks*/

	} else {
		new->num_sets = 1; /*Direct mapped cache, while technically 0 sets, we will treat it as 1 set*/
		new->set_bits = (int)(log(new->num_lines) / log(2)); /*Set bits used to index*/
	}

	new->tag_bits = 32 - (new->set_bits + new->line_bits);

	new->lines_per_set = new->num_lines / new->num_sets;

	new->cache_sets = new_sets(new->num_sets, new->lines_per_set);

	return new;
}

/**
 * Prints cache info for debugging purposes
 * @param cache the cache to be printed
 */
void print_cache(struct cache_t *cache)
{
	printf("Initializers:\n");
	printf("Line size: %d\n", cache->line_size);
	printf("Associativity: %d\n", cache->associativity);
	printf("Data size: %d\n", cache->data_size);
	printf("Replacement: %d\n", cache->replacement);
	printf("Miss Penalty: %d\n", cache->miss_penalty);
	printf("Write Allocate: %d\n", cache->write_alloc);
	printf("--------------------\n");
	printf("Address Info:\n");
	printf("Line Bits: %d\n", cache->line_bits);
	printf("Set Bits: %d\n", cache->set_bits);
	printf("Tag Bits: %d\n", cache->tag_bits);
	printf("--------------------\n");
	printf("Set Info:\n");
	printf("# of Lines: %d\n", cache->num_lines);
	printf("# of Sets: %d\n", cache->num_sets);
	printf("Lines per set: %d\n", cache->lines_per_set);

	return;
}

/**
 * Deallocates a cache and its internal structures
 * @param cache the cache to be freed
 */
void free_cache(struct cache_t *cache){
	int i;
	for(i = 0; i < cache->num_sets; i++){
		free(cache->cache_sets[i].cache_lines);
	}
	free(cache->cache_sets);
	free(cache);
}
