/*
Author: Alexander Rickford
Purpose: a command line menu system for an archive built using 
system calls

built for linux system calls
*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dirent.h>
#include <unistd.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <utime.h>

#define ARMAG "!<arch>\n"
#define SARMAG 8
#define ARFMAG "`\n"
///*
struct ar_hdr
{
	char ar_name[16]; 		///* '/' terminated file member name *
    char ar_date[12];		///* file member date *
    char ar_uid[6];			///* file member user identification *
    char ar_gid[6];			///* file member group identification *
    char ar_mode[8];		///* file member mode (octal) *
    char ar_size[10];		///* file member size *
    char ar_fmag[2];		///* header trailer string *
};

void append(char* archive, char** filenames, int length);

void extract(char* archive, char** filenames, int length);

void printTable(char* archive);

int main(int argc, char** argv){

	if(argc == 1){
		//print usage
		printf("myar: Custom ar command\n"
			"Usage: myar [options] archivefile filename ...\n"
			"options:\n"
			"\tq\t-append named files to archive\n"
			"\tx\t-extract named files\n"
			"\tt\t-print table of contents for archive\n"
			"\tA\t-append all ordinary files in the current directory\n");
		exit(1);
	}

	char* option = argv[1];

	if(*option == 'q'){
		//append named files

		int fd, files = argc - 3;
		mode_t permissions = S_IRUSR | S_IWUSR | S_IRGRP | S_IWGRP | S_IROTH | S_IWOTH;

		fd = open(argv[2], O_WRONLY | O_APPEND);
		char** filenames = (char **) malloc(sizeof(char *) * files);

		if(fd == -1){
			fd = open(argv[2], O_WRONLY | O_CREAT | O_APPEND, permissions);
			write(fd, ARMAG, SARMAG);
			close(fd);
		}

		for(int c = 0; c < files; c++){
			filenames[c] = argv[c + 3];
		}//for
		append(argv[2], filenames, files);	
	}
	else if(*option == 'x'){
		//extract named files

		int fd, files = argc - 3;

		fd = open(argv[2], O_RDONLY);

		if(fd == -1){
			printf("Archive file %s does not exist\n", argv[2]);
			exit(1);
		}
		else{
			char* names[files]; 
			for(int c = 0; c < files; c++){
				names[c] = argv[c + 3];
			}
			extract(argv[2], names, files);//have an offset for filenames
		}
	}
	else if(*option == 't'){
		//print table of contents for archive

		int fd;

		fd = open(argv[2], O_RDONLY);

		if(fd == -1){
			printf("Archive file  %s does not exist.\n", argv[2]);
			exit(1);
		}
		else{
			printTable(argv[2]);
		}
	}
	else if(*option == 'A'){
		//archive all ordinary files in local directory

		DIR *dp;
		struct dirent *dirp;
		struct stat file;
		int count = 0;
		
		if((dp = opendir(".")) == NULL){
			printf("Could not open current directory\n");
			exit(1);
		}
		int fd = open(argv[2], O_WRONLY | O_APPEND);
		if(fd == -1){
			printf("Archive file %s does not exist\n", argv[2]);
		}
		while((dirp = readdir(dp)) != NULL){
			if(stat(dirp->d_name, &file) >= 0){
				if(S_ISREG(file.st_mode) != 0){
					append(argv[2], dirp->d_name, 1);
				}
			}
		}
		char** filenames = (char **) malloc(sizeof (char *) * count);
		rewinddir(dp);
		int c = 0;
		while((dirp = readdir(dp)) != NULL){
			if(stat(dirp->d_name, &file) >= 0){
				if(S_ISREG(file.st_mode) != 0){
					filenames[c++] = dirp->d_name;
				}
			}
		}
		append(argv[2], filenames, count);
	}
	else{
		printf("Improper command\n");
		exit(1);
	}
}//main

void append(char* archive, char** filenames, int flength){

	struct ar_hdr myhead;
	struct stat buf2;
	int sr2 = stat(archive, &buf2); //gain info about archive
	int fd1 = open(archive, O_WRONLY | O_APPEND);//open archive in write
											//only with append on

	if(fd1 == -1 || sr2 == -1){
		printf("Error opening archivefile %s\n", archive);
		return;
	}

	for(int j = 0; j < flength; j++){

		struct stat buf;
		int count = 0;
		int fd2 = open(filenames[j], O_RDONLY);
		int sr = stat(filenames[j], &buf);

		if(fd2 != -1 && sr != -1){
			int length = 60 + buf.st_size 
							+ ((buf.st_size % 2 == 0)? 0 : 1);
			char* string;
			char content[length + 1];

			for(int i = 0; i <= length; i++){
			 content[i] = '\0';

			}//for

			for(int i = 0; i < 16; i++, count++){
				if(i < strlen(filenames[j])){
					content[count] = filenames[j][i];
				}
				else if(i == strlen(filenames[j])){
					content[count] = '/';
				}
				else{
					content[count] = ' ';
				}
			}//for

			int size = 0;
			size = snprintf(NULL, 0, "%d", buf.st_mtime);
			string = (char *) malloc(sizeof(char) * (size + 1) );
			snprintf(string, size + 1, "%d", buf.st_mtime);
			for(int i = 0; i < 12; i++, count++){
				if(i < size){
					content[count] = string[i];
				}
				else{
					content[count] = ' ';
				}
			}
			free(string);
			size = snprintf(NULL, 0, "%d", buf.st_uid);
			string = (char *) malloc(sizeof(char)* (size + 1) );
			snprintf(string, size + 1, "%d", buf.st_uid);

			for(int i = 0; i < 6; i++, count++){
				if(i < size){
					content[count] = string[i];
				}
				else{
					content[count] = ' ';
				}
			}//for

			free(string);
			size = snprintf(NULL, 0, "%d", buf.st_gid);
			string = (char *) malloc(sizeof(char)* (size + 1) );
			snprintf(string, size + 1, "%d", buf.st_gid);
			string[size] = '\0';

			for(int i = 0; i < 6; i++, count++){
				if(i < size){
					content[count] = string[i];
				}
				else{
					content[count] = ' ';
				}
			}//for

			free(string);
			size = snprintf(NULL, 0, "%d", buf.st_mode);
			string = (char *) malloc(sizeof(char)* (size + 1) );
			snprintf(string, size + 1, "%d", buf.st_mode);
			for(int i = 0; i < 8; i++, count++){
				if(i < size ){
					content[count] = string[i];
				}
				else{
					content[count] = ' ';
				}
			}//for

			free(string);
			size = snprintf(NULL, 0, "%d", buf.st_size);
			string = (char *) malloc(sizeof(char)* (size + 1) );
			snprintf(string, size + 1, "%d", buf.st_size);
			for(int i = 0; i < 10; i++, count++){
				if(i < size){
					content[count] = string[i];
				}
				else{
				content[count] = ' ';
				}	
			}
			free(string);
			content[count++] = '`';
			content[count++] = '\n';
			int i = 1, big = buf.st_blksize;
			while(i > 0){
				char data[big];
				i = read(fd2, data, big);
				for(int c = 0; c < i; c++, count++){
					content[count] = data[c];
				}
			}//while

			if((buf.st_size % 2) != 0){
				content[count++] = '\n';
			}

			content[count] = '\0';
			big = buf2.st_blksize;
			char data[big + 1];
			data[big] = '\0';

			for(int c = 0 ; c < count; c++){
				if(c != 0 && (c % big) == 0){
					write(fd1, data, big);
				}
				data[c % big] = content[c];
				if(c == count - 1){
					write(fd1, data, (c + 1) % big);
				}
			}//for

			close(fd2);
		}
		else{
			printf("Could not load file %s\n", filenames[j]);
		}

	}//for
	close(fd1);
	
	
}//append

void extract(char* archive, char** filenames, int length){
	int offset, fd1 = open(archive, O_RDONLY);
	struct stat buf;
	int sr2 = stat(archive, &buf);
	if(sr2 == -1) return;
	int big = buf.st_blksize;
	char data[17];

	for(int i = 0; i < 17; i++) {
		data[i] = '\0';
	}

	lseek(fd1, 8, SEEK_SET);
	int i = 1;

	while(i > 0){
		i = read(fd1, data, 16);
		int k = 1;
		for(int j = 0; j < length; j++){
			k = 1;
			for(int c = 0; c < strlen(filenames[j]); c++){
				if(data[c] != filenames[j][c]){
					k = 0;
					c = strlen(filenames[j]);
				}
			}
			if(k == 1){
				printf("File: %s was found!\n", filenames[j]);
				mode_t mode;
				struct stat buf2;
				time_t date;
				int uid, gid, size, offset,  check, l = 0;
				char info[big + 1];
				info[big] = '\0';

				//get data
				read(fd1, data, 12);
				sscanf(data, "%d", &date);
				read(fd1, data, 6);
				sscanf(data, "%d", &uid);
				read(fd1, data, 6);
				sscanf(data, "%d", &gid);
				read(fd1, data, 8);
				sscanf(data, "%d", &mode);
				read(fd1, data, 10);
				sscanf(data, "%d", &size);
				lseek(fd1, 2, SEEK_CUR);

				char input[size + 1];
				input[size] = '\0';

				for(int c = size / big; c > 0; c /= big){
					check = read(fd1, info, big);
					for(int i = 0; i < big; i++){
						input[l++] = info[i];
					}//for
				}//for
				check = read(fd1, info, (size % big));
				for(int i = 0; i < (size % big); i++){
					input[l++] = info[i];
				}//for

				offset = (size % 2 == 0)? 0 : 1;
				lseek(fd1, offset, SEEK_CUR);
				int fd3 = open(filenames[j], O_WRONLY | O_CREAT, mode);
				stat(filenames[j], &buf2);
				int big2 = buf2.st_blksize;
				char data2[big2 + 1];

				for(int c = 0 ; c < size; c++){
					data2[big2] = '\0';
					if(c != 0 && (c % big2) == 0){
						write(fd3, data2, big2);
					}
					data2[c % big2] = input[c];
					if(c == size - 1){
						write(fd3, data2, (c + 1) % big2);
					}
				}//for

				close(fd3);
				struct utimbuf oldtime;
				oldtime.actime = date;
				oldtime.modtime = date;
				check = utime(filenames[j], &oldtime);
				j = length;
			}

		}//for

		if(k == 0){
			lseek(fd1, 32, SEEK_CUR);
			read(fd1, data, 10);
			int size;
			sscanf(data, "%d", &size);
			size += (size % 2 == 0)? 2 : 3;
			lseek(fd1, size, SEEK_CUR);
		}

	}//while
	close(fd1);

}//extract

void printTable(char* archive){
	int fd1 = open(archive, O_RDONLY);
	char data[16];
	int i = 1;
	lseek(fd1, 8, SEEK_SET);
	while(i > 0){
		i = read(fd1, data, 16);
		char* info;
		int k = 0;
		int count = 0;
		for(int c = 0; c < 16; c++){

			if(data[c] != '/'){
				count++;
			}

			else{
				k = 1;
				c = 15;
			}

		}//for

		if(k == 1){
			
			info = (char *) malloc(sizeof(char) * (count+ 1));

			for(int c = 0; c < count; c++) {
				info[c] = data[c];
			}//for

			info[count] = '\0';
			printf("%s\n", info);
			free(info);
		}

		lseek(fd1, 32, SEEK_CUR);
		read(fd1, data, 10);
		int size;
		sscanf(data, "%d", &size);
		size += (size % 2 == 0)? 2 : 3;
		lseek(fd1, size, SEEK_CUR);

	}//while

	close(fd1);

}//printTable