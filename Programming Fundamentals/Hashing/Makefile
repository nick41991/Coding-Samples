CC=gcc
CFLAGS=-g -Wall

targets = state_hash scrabble_lookup

all: $(targets)

%: %.c
	$(CC) $(CFLAGS) $@.c -o $@

.PHONY: clean
clean:
	rm -rf *.o $(targets)
