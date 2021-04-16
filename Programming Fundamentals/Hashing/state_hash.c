/**
 * @file state_hash.c
 *
 * @author Nicholas Smith
 *
 * @date 4/14/2017
 *
 * Assignment: Homework 6B
 *
 * @brief Generates a hash table of states and thier postal codes then lets the user search for the name by entering a two-digit abbreviation
 *
 * @bugs Only for the Northern Mariana Islands, a '!' character always appends itself to the state name
 *
 * @todo none
 */
#include <stdio.h>
#include <ctype.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

#define LEN 4096
#define TABLE_SIZE 200

struct postal_t {
        char *abrv; /*two digit abbreviation, capitalized */
        char *name; /*name in start case */
};

struct postal_t new_postal(char *input_string);

int hash(char *key);

int probe(int hash_value, int i);

void set_visited(int visited[]);

int check_visited(int visited[]);

void debug_print(struct postal_t *postal);

int main(int argc, char *argv[])
{
        char buf[LEN];
        int i;
        int hash_value;
        int visited[TABLE_SIZE];

        set_visited(visited);


        char *input = malloc(3 * sizeof(char));

        /*Array of postal structs*/
        struct postal_t *postal = malloc(TABLE_SIZE * sizeof(struct postal_t));

        struct postal_t cur;

	FILE *fp = fopen("postal", "r");
	assert(fp);

        /*Hash table initialization*/
	while(fgets(buf, LEN, fp)) {
                cur = new_postal(buf);
                hash_value = hash(cur.abrv);

                /*i gets reset with every insertion*/
                i = 1;

                /*Check if the current index found by hash() is already filled and if so uses a quadratic probe to find a new index*/
                CHECK: if(postal[hash_value].abrv != NULL){
                        hash_value = probe(hash_value, i);
                        /*Increment i in case i^2 didn't create an open index*/
                        i++;
                        goto CHECK;
                }
                postal[hash_value] = cur;

        }

        debug_print(postal);

        /*Menu- find a state in table from abbreviation. q to quit*/
        while(1){
                TOP:
                printf("Enter a two-digit state abbreviation (q to quit): ");
                scanf("%s", input);
                if(strncmp(input, "q", 2) == 0){
                        break;
                }
                /*Hashing the input gives a start position to search*/
                hash_value = hash(input);

                CHECK1:
                if(postal[hash_value].abrv == NULL || strncmp(input, postal[hash_value].abrv, 2) != 0){

                        visited[hash_value] = 1;
                        hash_value++;
                        hash_value = hash_value % TABLE_SIZE;


                        if(check_visited(visited) == 1){
                                printf("State not found\n\n");
                                set_visited(visited);
                                goto TOP;
                        }

                        goto CHECK1;
                }else{
                        printf("%s\n\n", postal[hash_value].name);
                }
        }

        /*Memory cleanup*/
        for(i = 0; i < TABLE_SIZE; i++){
                free(postal[i].name);
                free(postal[i].abrv);
        }

        free(postal);
        free(input);

        fclose(fp);

        return 0;
}


/**
 * Parses a string to generate a new postal_t structure
 * @param input_string the string containing the data for the structure
 * return the new structure
 */
struct postal_t new_postal(char *input_string)
{
        struct postal_t new;
        int i;
        int space_count = 0;

        for(i = 0; input_string[i] != '\0'; i++){
                if(isspace(input_string[i]) != 0){
                        space_count++;
                }
                if(isspace(input_string[i]) == 0){
                        space_count = 0;
                }
                if(space_count == 2){
                        new.name = calloc((i - 1) , sizeof(char));
                        assert(new.name);
                        memcpy(new.name, input_string, (i - 1) * sizeof(char));
                        break;
                }
        }

        new.abrv = malloc(2 * sizeof(char));
        assert(new.abrv);

        memcpy(new.abrv, (input_string + (strlen(input_string) - 3) * sizeof(char)), 2 * sizeof(char));

        return new;
}

/**
 * Generates a hash value using Bernstein's function
 * @param key the key to be hashed
 * return the hashed key
 */
int hash(char *key)
{
        int i;
        int k = 0;
        for(i = 0; i < strlen(key); i++){
                k = (33 * k) + (int)key[i];
        }
        return k % TABLE_SIZE;
}

/**
 * Performs a quadratic probe on a previously determined hash value to mitigate collisions
 * @param hash_value the value to be changed
 * @param i the current number to be squared and added to the hash_value
 * return the new value
 */
int probe(int hash_value, int i)
{
        return (hash_value + (i * i)) % TABLE_SIZE;
}

/**
 * Sets all the elements in an array to zero
 * @param visited the array to be zeroed
 */
void set_visited(int visited[])
{
        int i;
        for(i = 0; i < TABLE_SIZE; i++){
                visited[i] = 0;
        }
}

/**
 * Checks to see if all the elements in the hash table have been visited
 * @param visited the array to be checked
 * return 1 if all elements have been checked, 0 if not all have
 */
int check_visited(int visited[])
{
        int i;
        for(i = 0; i < TABLE_SIZE; i++){
                if(visited[i] == 0){
                        return 0;
                }
        }
        return 1;
}

/**
 * Prints the elements of the hash table and the indecies at which they are stored
 * @param postal the table to be rp
 */
void debug_print(struct postal_t *postal)
{
        int i;
        for(i = 0; i < TABLE_SIZE; i++){
                if(postal[i].abrv != NULL){
                        printf("i = %d\n", i);
                        printf("%s\t%s\n", postal[i].abrv, postal[i].name);
                }
        }
}
