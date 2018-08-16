/*
Author: Alexander Rickford
Purpose: use system calls to take character input from standard input,
select and sort alphabet strings between 5 to 35 characters long, and
output to standard output along with a duplicate count
*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <utime.h>

const int SIZE = 36;

typedef struct _list{
	char word[35];
	int count;
	struct _list *next;
} list;

int main(int argc, char** argv){
	int pfd1[2];//pipe file descriptor for pipe 1
	int pfd2[2];//pipe file descriptor for pipe 2
	pid_t pid1, pid2;//process id for children
	pipe(pfd1);
	pipe(pfd2);
	pid1 = fork();
	pid2 = fork();
	if(pid1==0){ //child 1 reads input from parent sorts it and outputs to
				//child 2
		dup2(pfd1[0], STDIN_FILENO);
		dup2(pfd2[1], STDOUT_FILENO);
		//close unnecessary file descriptors
		close(pfd1[0]);
		close(pfd1[1]);
		close(pfd2[0]);
		close(pfd2[1]);
		FILE *in = fdopen(STDIN_FILENO, "r");
		FILE *out = fdopen(STDOUT_FILENO, "w");
		execl("/usr/bin/sort", "sort", (char *) NULL);//use sort to order input strings
	}
	else if(pid2 == 0){ //child 2 reads input from child 1 and outputs to
						//terminal
		dup2(pfd2[0], STDIN_FILENO); //move pfd to standard input
		//close unnecessary file descriptors
		close(pfd1[0]);
		close(pfd1[1]);
		close(pfd2[0]);
		close(pfd2[1]);
		FILE *in = fdopen(STDIN_FILENO, "r");// create file 'in' set to 
											//read only
		char word[SIZE];
		for(int i = 0; i < SIZE; i++) { 
			word[i] = '\0';
		}
		int count = 0, test = 0;
		list *mylist = (list *) malloc(sizeof(list));
		list *pointer = mylist;
		for(int i = 0; i < SIZE; i++) {
			mylist->word[i]='\0';
		}
		mylist -> count = 0;
		//continue reading from pipe in until it is empty
		while((fgets(word, SIZE + 1, in)) != NULL){
			//use size+1 to account for null character at max string size

			//read string from word
			for(int c = 0; c < SIZE; c++){
				if(word[c] == '\n') {
					word[c] = '\0';
					for(list *ptr = mylist; ptr != NULL;
						ptr = ptr -> next){
						//test for duplicate strings
						test = 0;
						for(int i = 0; i < SIZE; i++){
							if(pointer -> word[i] != word[i])
								test = 1;
						}//for
						if(test == 0){
							//test failed don't add string increase 
							//string's count
							pointer -> count++;
							break;
						}
						pointer = ptr;
					}//for
					if(test == 1){
						//test passed, add new string
						pointer -> next = (list *) malloc(sizeof(list));
						pointer -> count = 1;
						for(int i = 0; i < SIZE; i++){
							if(i < c) 
								pointer -> word[i] = word[i];
							else 
								pointer -> word[i] = '\0';
						}//for
					}
					for(int i = 0; i < SIZE; i++) {
						word[i]='\0';
					}//for
					break;
				}
			}//for
		}
		//deallocate pointer->next
		free(pointer -> next);
		pointer -> next = NULL;
		//output contents of mylist
		for(list *ptr = mylist; ptr != NULL; ptr = ptr -> next){
			printf("%s\t%5d\n", ptr -> word, ptr -> count);
		}
		fclose(in);
	}
	else{ //parent reads from standard input and outputs to child 1
		//close unnecessary file descriptors
		close(pfd1[0]);
		close(pfd2[0]);
		close(pfd2[1]);
		FILE *in = fdopen(STDIN_FILENO, "r");//create file 'in' set to
											//read only
		FILE *out = fdopen(pfd1[1], "w");//create file 'out' set to
											//write only
		char word[SIZE];
		char string[SIZE];
		int count = 0;
		for(int i = 0; i < SIZE; i++) {
			word[i] = '\0';
		}//for
		for(int i = 0; i < SIZE; i++) {
			string[i] = '\0';
		}//for
		//continue reading from pipe in until it is empty
		while((fgets(string, SIZE, in)) != NULL){
			//look for alphabet strings between 5 to 35 charcters long
			for(int c = 0; string[c] != '\0' 
				|| (count > 4 && count < SIZE - 1); c++){
				//capitalize characters
				if(string[c] < 97) 
					string[c] += 32;
				if(string[c] >= 97 && string[c] <= 122){
					//only accept alphabet characters
					if(count < SIZE - 1){
						word[count] = string[c];
						count++;
					}
				}
				else{
					//invalid character found
					if(count > 4){
					//output string if larger than 4 characters
						char *tmp = (char *) malloc(sizeof(char) 
													 * (count + 2));
						tmp[count] = '\n';
						tmp[count + 1] ='\0';
						for(int i = 0; i < count; i++){
							tmp[i] = word[i];
						}
						fputs(tmp, out);
						free(tmp);
						tmp = NULL;
					}
					//reset count and word
					count = 0;
					for(int i = 0; i < SIZE; i++){
						word[i] = '\0';
					}//for
				}
			}//for
		}//while
		fclose(in);//close file in
		fclose(out);//close file out
		wait(NULL);//kill child 1
		wait(NULL);//kill child 2
	}
}//main