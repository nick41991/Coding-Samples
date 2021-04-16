/*************************************************************************
/
/	filename: 	cache.h
/
/	description: 	Prototypes cache.c functions
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

struct address_t{
	unsigned int line;
	unsigned int set;
	unsigned int tag;
};

struct line_t{
	/*Store base address as well as separated address*/
	int valid; /*0 or 1 indicates the line is in use*/
	int timestamp;
	int tag; 	/*[set .. 32]*/
};

struct set_t{
	struct line_t *cache_lines; /*holds num_lines cache lines*/
};

struct cache_t{
	/*Initializers*/
	int line_size;/*+power of 2*/
	int associativity; /*0,1 or power of 2*/
	int data_size;/*+power of 2 * 1KB*/
	int replacement; /*0 or 1*/
	int miss_penalty; /*+int*/
	int write_alloc; /*0 or 1*/

	/*Memory Address Info*/
	int line_bits;
	int set_bits;
	int tag_bits;

	/*Set info*/
	int num_lines;
	int num_sets;
	int lines_per_set;

	/*Memory*/
	struct set_t *cache_sets;
};

struct address_t *new_address(char *address, int line_bits, int set_bits);
struct set_t * new_sets(int num_sets, int lines_per_set);
struct cache_t *new_cache(struct cache_t *new, FILE *conf);
void print_cache(struct cache_t *cache);
void free_cache(struct cache_t *cache);
