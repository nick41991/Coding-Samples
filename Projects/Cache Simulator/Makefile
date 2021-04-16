##########################################################################
#
#	filename:	Makefile
#
#	description:	Compiles files in Lab2 cache simulator
#
#	authors:	Smith, Nicholas
#
#	class:		CSE331
#	instructor:	Zheng
#	assignment:	Lab #2
#
#	assigned:	2/19/2019
#	due:		3/8/2019
#
##########################################################################
CC=gcc
CFLAGS= -g -Wall
LIB=-lm

targets=run

all: $(targets)
error: $(error_targets)

run: run.c cache.c cache.h
	$(CC) $(CFLAGS) -c cache.c
	$(CC) $(CFLAGS) run.c $(LIB) cache.o $(LIB) -o run

clean:
	rm $(targets) *.o
