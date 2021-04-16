/**
 * @file scrabble.c
 *
 * @author Nicholas Smith
 *
 * @date 3/27/2017
 *
 * Assignment: Homework 5
 *
 * @brief Loads the Scrabble dictionary into memory and lets users validate words and get the best play from their tiles.
 *
 * @bugs Leaks 3 blocks of 16 bytes each. Sometimes pulls random words when using the best play function.
 *
 * @todo none
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

char **load_dictionary(FILE *dic);
void clear_word(char *word);
int bin_dict_srch(char *word, char **dictionary, int dic_size);
int count_elem(char **array);
void best_play(char *tiles, char **dictionary, int dic_size);
void count_letters(char *word, int *dest);
void free_dictionary(char **dictionary, int dic_size);

int main(void)
{
        system("clear");
        FILE *dic;
        dic = fopen("scrabble.txt", "r");

        char **dictionary;

        dictionary = load_dictionary(dic);
        int dic_size = count_elem(dictionary);
        fclose(dic);
        int option = 0;
        /*15 is the max number of tiles that can fit on a straightline on a scrabble board so 15 + 1 for null*/
        char word[16];

        while(option != 3){

                clear_word(word);

                printf("Scrabble Menu\n");
                printf("1. find if a word is in the scrabble dictionary\n");
                printf("2. determine best play from tiles\n");
                printf("3. quit\n\n");
                printf("Enter an option [1, 2, 3]: ");
                scanf("%d", &option);

                switch(option){
                        /*1) validate word
                          2) find best play
                          3) quit
                          anything else) input error message
                        */
                        case 1:
                                printf("Enter word: ");
                                scanf("%s", word);
                                if(bin_dict_srch(word, dictionary, dic_size) == 1){
                                        printf("%s is a valid Scrabble word\n\n", word);
                                }
                                else{
                                        printf("%s is not a valid Scrabble word\n\n", word);
                                }
                                break;

                        case 2:
                                printf("Enter tiles as a word: ");
                                scanf("%s", word);
                                best_play(word, dictionary, dic_size);
                                break;
                        case 3:
                                printf("Goodbye.\n");
                                break;
                        default:
                                printf("Error: Unrecognized input. Please try again\n\n");
                }
        }

        free_dictionary(dictionary, dic_size);

	return 0;
}

/**
 * Loads a dictionary dynamically into memory using a doubling scheme
 * @param *dic a pointer to the dictionary file to be loaded
 * return a pointer to the completed dictionary
 */
char **load_dictionary(FILE *dic)
{
        int i = 2;
        int j = 0;
        char **tmp;
        char **dictionary = malloc(i * sizeof(char *));
        /*15 is the max number of tiles that can fit on a straightline on a scrabble board so 15 + 1 for null*/
        dictionary[j] = malloc(16 * sizeof(char));

        if(dictionary == NULL){
                printf("Error in allocating memory\n");
                exit(100);
        }


        while(fscanf(dic, "%s\n", dictionary[j]) != EOF){

                j++;

                if (j >= i - 1){
                        i *= 2;
                        tmp = realloc(dictionary, (i * sizeof(char *)));
                }
                if(tmp == NULL){
                        printf("Error in allocating memory\n");
                        exit(100);
                }
                dictionary = tmp;
                dictionary[j] = malloc(16 * sizeof(char));

        }
        /*Frees the excess memory allocated during the doubling scheme*/
        free(dictionary[j]);
        dictionary = realloc(dictionary, ((j + 1) * sizeof(char *)));
        dictionary[j] = NULL;
        return dictionary;
}

/**
 * Counts the number of strings in an array of strings
 * @param array the array of strings
 * return the count of the number of strings
 */
int count_elem(char **array)
{
        int count = 0;
        while(array[count] != NULL){
                count++;
        }
        /*-1 because of fscanf reading one too many elements*/
        return count;
}

/**
 * Zeros out a string to prevent unintended chars from interfering with functions that use the word
 * @param word the word to be cleared
 */
void clear_word(char *word)
{
        *word = (char)0;
}

/**
 * Uses a binary search to seach through an array of strings (the dictionary)
 * @param word the string to be searched for
 * @param dictionary the array of strings to be searched
 * @param dic_size the number of elements in the dictionary array
 * return 1 if the word was found, 0 if it was not
 */
int bin_dict_srch(char *word, char **dictionary, int dic_size)
{
        int start_index = 0;
        int end_index = dic_size - 1;
        int mid_index = (dic_size - 1) / 2;
        do{
                /*Check to see if the current mid point and end points lexographically are equivalent to the word that is being searched for*/
                if(strncmp(word, dictionary[mid_index], 16) == 0 || strncmp(word, dictionary[start_index], 16) == 0 || strncmp(word, dictionary[end_index], 16) == 0){
                        return 1;
                }
                else {
                        if(strncmp(word, dictionary[mid_index], 16) < 0){
                                end_index = mid_index;
                                mid_index = (end_index + start_index) / 2;
                        }
                        else if(strncmp(word, dictionary[mid_index], 16) > 0){
                                start_index = mid_index;
                                mid_index = (end_index + start_index) / 2;
                        }
                }
        }while(start_index != end_index && mid_index != end_index && mid_index != start_index);
        /*If the function terminates without finding the value it will return 0*/
        return 0;
}

/**
 * Counts the number of each letter in a string
 * @param word the word to be counted
 * @param dest the array in which the results are to be stored in
 */
void count_letters(char *word, int *dest)
{
        int i;
        /*Zero's out the block of memory so it can be reused*/
        for(i = 0; i < 26; i++){
                dest[i] = 0;
        }
        /*Counts the number of times a letter appears in a word*/
        for(i = 0; i < strlen(word); i++){
                dest[word[i] - 97]++;
        }
}

/**
 * Determines the best scrabble play given the users current tile rack
 * @param rack the tiles in the users rack
 * @param dictionary the list of allowable words rack is being compared to
 * @param dic_size the size of the dictionary array
 */
void best_play(char *rack, char **dictionary, int dic_size)
{
        /*Index 0 is a, index 25 is Z*/
	int point_values[] = {1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10};
        /*letter equivalent:  a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p,  q, r, s, t, u, v, w, x, y,  z*/
        /*Indexes             0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15, 16,17,18,19,20,21,22,23,24, 25*/


        int i, j;
        int tiles[26];
        count_letters(rack, tiles);
        int word[26];
        int cur_score;
        int best_score = 0;
        int best_index;

        for(i = 0; i < dic_size; i++){
                cur_score = 0;
                /*Just streamlines the program. If there aren't enough tiles in the rack, there's no point in checking*/
                if(strlen(dictionary[i]) <= strlen(rack)){
                        count_letters(dictionary[i], word);
                        /*Checks to see if the word can be spelt and if so tallys points*/
                        for(j = 0; j < 26; j++){
                                if(tiles[j] < word[j]){
                                        cur_score = 0;
                                        break;
                                }
                                if(tiles[j] != 0 && tiles[j] >= word[j]){
                                        cur_score = cur_score + (word[j] * point_values[j]);
                                }
                        }
                        /*Evaluates what the current best play found is*/
                        if(cur_score > best_score){
                                best_index = i;
                                best_score = cur_score;
                        }
                }
        }
        if (best_score != 0)
                printf("\nbest play is '%s' (%d points)\n\n", dictionary[best_index], best_score);
        else
                printf("There are no available plays.\n\n");
}

void free_dictionary(char **dictionary, int dic_size)
{
        int i;
        for(i = 0; i < dic_size; i++){
                free(dictionary[i]);
        }
        free(dictionary);
}
