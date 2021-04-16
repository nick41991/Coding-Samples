/**
 * @file scrabble_lookup.c
 *
 * @author Nicholas Smith
 *
 * @date 4/14/2017
 *
 * Assignment: Homework 6B
 *
 * @brief Loads the Scrabble dictionary into memory and allows the user to validate if a word is inside the dictionary
 *
 * @bugs
 *
 * @todo none
 */
#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>
#include <ctype.h>

#define LEN 128
/*Set this to 1 if menu is to be used and 0 if debugging*/
#define MENU 0

struct node_t {
        char *word; /* dictionary word */
        struct node_t *next;
};

struct list_t {
        int nnodes; /* number of nodes list contains */
        struct node_t *head; /*head of the list */
};

struct list_t **load_dictionary(FILE *dic, struct list_t **dict, int tbl_size);

struct node_t *new_node(char *buf);

unsigned int hash(char *key, int table_size);

void debug_print(struct list_t **dict, int tbl_size);

int hash_dict_srch(char *word, struct list_t **dict, int tbl_size);

int main(int argc, char *argv[])
{
        /*Usage statement*/
        if(argc != 2){
                printf("usage ./scrabble_lookup [TABLE_SIZE]\n");
                printf("where TABLE_SIZE is the size of the hash table to build\n");
                exit(EXIT_FAILURE);
        }

        int i;

        int option = 0;
        char word[LEN];

        /*Dictionary Initialization*/
        size_t tbl_size = strtol(argv[1], NULL, 10);
        struct list_t **dict = malloc(sizeof(struct list_t *) * tbl_size);
        assert(dict);
        struct node_t *tmp;

        FILE *dic;
        dic = fopen("scrabble.txt", "r");

        dict = load_dictionary(dic, dict, (int)tbl_size);

        /*Debug print*/
        if(MENU == 0){
                debug_print(dict, tbl_size);
                goto END;
        }

        /*Menu*/
        while(option != 2){
                printf("Scrabble Menu\n");
                printf("1. find if a word is in the scrabble dictionary\n");
                printf("2. quit\n\n");
                printf("Enter an option [1, 2]: ");
                scanf("%d", &option);

                switch(option){
                        case 1:
                                printf("Enter word: ");
                                scanf("%s", word);
                                if(hash_dict_srch(word, dict, (int)tbl_size) == 1){
                                        printf("%s is a valid Scrabble word\n\n", word);
                                }
                                else{
                                        printf("%s is not a valid Scrabble word\n\n", word);
                                }
                                break;
                        case 2:
                                printf("Goodbye.\n");
                                break;
                        default:
                                printf("Error: Unrecognized input. Please try again\n\n");

                }
        }


        /*Free Memory*/
        END:
        for(i = 0; i < tbl_size; i++){
                if(dict[i] != NULL){
                        while(dict[i]->head != NULL){
                                tmp = dict[i]->head->next;
                                free(dict[i]->head->word);
                                free(dict[i]->head);
                                dict[i]->head = tmp;
                        }
                        free(dict[i]);
                }
        }
        free(dict);
        fclose(dic);

	return 0;
}

/**
 * Loads a dictionary into memory from a file
 * @param dic the file to be read from
 * @param dict the array of sentinel nodes for linked lists which will hold the words of the dictionary
 * @param tbl_size the number of lists in the dict array
 * return the completed dictionary
 */
struct list_t **load_dictionary(FILE *dic, struct list_t **dict, int tbl_size)
{
        char buf[LEN];
        unsigned int hash_value;
        struct node_t *new;
        while(fscanf(dic, "%s\n", buf) != EOF){
                new = new_node(buf);
                hash_value = hash(new->word, tbl_size);

                if(dict[hash_value] == NULL){
                        dict[hash_value] = malloc(sizeof(struct list_t));
                        assert(dict[hash_value]);
                        dict[hash_value]->nnodes = 0;
                        dict[hash_value]->head = NULL;
                }

                dict[hash_value]->nnodes++;

                if(dict[hash_value]->head == NULL){
                        dict[hash_value]->head = new;
                }else{
                        new->next = dict[hash_value]->head;
                        dict[hash_value]->head = new;
                }

        }
        return dict;
}

/**
 * Creates a new node of type node_t using the data in buf
 * @param buf the string to go into the node's word member
 * return the new node
 */
struct node_t *new_node(char *buf)
{
        struct node_t *new = malloc(sizeof(struct node_t));
        assert(new);
        new->next = NULL;
        new->word = malloc(strlen(buf) * sizeof(char));
        assert(new->word);
        strncpy(new->word, buf, strlen(buf));
        return new;
}

/**
 * Generates a hash value using Bernstein's function
 * @param key the key to be hashed
 * return the hashed key
 */
unsigned int hash(char *key, int table_size)
{
        int i;
        unsigned int k = 0;
        for(i = 0; i < strlen(key); i++){
                k = (33 * k) + (int)key[i];
        }
        return k % table_size;
}

/**
 * Prints every node in each list of the dictionary array
 * @param dict the array of lists to be printed
 * @param tbl_size the number of lists in the array
 */
void debug_print(struct list_t **dict, int tbl_size)
{
        int i;
        int count;
        struct node_t *tmp;
        for(i = 0; i < tbl_size; i++){
                printf("%5d: ", i);
                if(dict[i] != NULL){
                        count += dict[i]->nnodes;
                        tmp = dict[i]->head;
                        if(tmp != NULL){
                                while(tmp->next != NULL){
                                        if(tmp->next->next != NULL){
                                                printf("%s, ", tmp->word);
                                        }else{
                                                printf("%s", tmp->word);
                                        }
                                        tmp = tmp->next;
                                }
                        }
                }
                puts("");
        }
        printf("\ntable size %d load factor %3.2lf\n", tbl_size, ((double)count) / tbl_size);
}

/**
 * Searches a dictionary table to see if a word is valid
 * @param word the word to be checked for
 * @param dict the dictionary to be searched
 * @param tbl_size the size of the dictionary table
 * return 0 if the word is not found and 1 if it is
 */
int hash_dict_srch(char *word, struct list_t **dict, int tbl_size)
{
        unsigned int hash_value = hash(word, tbl_size);
        struct node_t *tmp;
        /*if the list word hashed to is unitialized then its not in the dictionary*/
        if(dict[hash_value] == NULL){
                return 0;
        }
        tmp = dict[hash_value]->head;
        while(tmp->next != NULL){
                if(strncmp(tmp->word, word, 16) == 0){
                        return 1;
                }
                tmp = tmp->next;
        }
        return 0;
}
