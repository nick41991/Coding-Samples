#include <stdio.h>
#include <stdlib.h>
#include <math.h>

int main()
{
	char *x = "0xFF32E10C";
	unsigned int tag;
	unsigned int block;
	unsigned int set;
	int blocks = 3;
	int sets = 11;
	int tags = 31 - sets - blocks;

	unsigned int addr = strtol(x, NULL, 16);
	printf("%u\n", addr);

	block = addr % (int)pow(2, blocks);
	set = (addr - block) % (int)pow(2, sets + blocks);
	tag = addr - (block + set);

	printf("%u\n%u\n%u\n%u\n", block, set, tag, block+tag+set);

	return 0;
}
