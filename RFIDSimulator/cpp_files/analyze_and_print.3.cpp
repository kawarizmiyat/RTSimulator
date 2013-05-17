// print results: 
#include <math.h>
#include <iostream>
#include <stdio.h>
#include <string>
#include <string.h>
#include <vector>
#include <stdlib.h>
#include <map>
using namespace std; 

#define TAGS_SIZE 	7
string tags[] = {"<num_redundant>", "<num_non_redundant>", "<iterations>", "<num_tags_phase>", "<num_readers_phase>", "<id>", "<tags>"};

enum Type {INT, FLOAT, STRING, NONE};
enum TagType {NUM_REDUNDANT, NUM_NON_REDUNDANT, ITERATIONS, NUM_TAGS_PHASE, NUM_READERS_PHASE, ID, TAGS};



int process_int(int t_num, vector<string>* str, TagType t);
int process_vector_int(int, vector<string>*, TagType);
float average(vector<float>* array); 
float average(vector<int>* array);
float stdev(vector<int>* array); 
float stdev(vector<float>* array);

char* str_bool(bool f);
int is_in_array(string*, string, int size);
bool is_in_map(map<int, vector<int> >* m, int rmt);
int getline(FILE* f, char line[], int max);
int getline_by_number(FILE* f, char line[], int max, int line_number);
bool tokenize(char* line, vector<string>* str);
char* trim(char* line);
void trim(char* input, char* output); 
bool reset_file(FILE* f); 
void print_string(string str); 
int is_tag(string str);
Type return_type(const char* str);
void print_vector(vector<int>& v); 
int process_tag_in_line(int t_num, vector<string>* str, int tag_type);
void treat_line(); 
void reset_line();
void save_results();
void analyze_results(); 
void print_in_file(char* filename); 
void print_analyzed_results();

vector<double> avg_readers_per_phase, std_readers_per_phase, avg_tags_per_phase, std_tags_per_phase; 
double avg_iterations, std_iterations, avg_num_redundant, std_num_redundant, avg_num_non_redundant, std_num_non_redundant; 

vector<int> file_iterations, file_num_redundant, file_num_non_redundant;
vector<vector<int> > file_tags_per_phase, file_readers_per_phase;

int current_id, iterations, num_redundant, num_non_redundant; 
vector<int> current_tags, tags_per_phase, readers_per_phase; 
map<int, vector<int> > graph; 
bool D = true; 





int main (int argc, char** argv) { 

	FILE* f; 
	char line[2000];
	vector<string> str_tokens;
	char buffer[50];
	int i = 0, tag_type, line_counter = 0;	
	int max_num_files = 25;
	
	
	int num_tags = atoi(argv[1]);

	for (int j = 1; j <= max_num_files; j++) { 
	 
	sprintf(buffer, "exp_%d/out_results_%d.out", num_tags, j); 
	printf("opening file %s \n", buffer);
	f = fopen(buffer, "r"); 	
	if (!f) { 
		printf("error: file could not be open \n");
		return 0;
	}


	i = 0;
	line_counter = 0;
	while(getline(f, line, 2000) != EOF) {  
		
		reset_line(); 
		str_tokens.clear();
		if (! tokenize(line, &str_tokens))
			printf("tokenize did not succeed \n"); 


		for (int i = 0; i < str_tokens.size();  ) {
			tag_type = is_tag(str_tokens.at(i)); 
			if (tag_type == -1 && i == 0) { printf("error: expecting a tag at position 0 \n"); break; }

			i = process_tag_in_line(i, &str_tokens, tag_type);
			// i ++;			
			if (i == -1) { 
			 	printf("error: exit\n");
			 	return 0; 
			}
		}
		treat_line();
		 
	}
		fclose(f);
		save_results();

	}

	 

	print_in_file("output_res.out"); 
	analyze_results(); 	
	print_analyzed_results(); 
	
}

void print_analyzed_results() { 

	printf("average # iterations: %f \n", avg_iterations);
	printf("average # num redundant: %f \n", avg_num_redundant);
	printf("average # num non redundant: %f \n", avg_num_non_redundant);


	printf("# iterations: ");
	for (int i = 0; i < file_iterations.size(); i++) 
		printf("%d ", file_iterations.at(i));
	printf("\n");


	printf("# non redundant: ");
	for (int i = 0; i < file_num_non_redundant.size(); i++) 
		printf("%d ", file_num_non_redundant.at(i));
	printf("\n");

	

	printf("# redundant: ");
	for (int i = 0; i < file_num_redundant.size(); i++) 
		printf("%d ", file_num_redundant.at(i));
	printf("\n");


	//printf("averge # readers per phase: "); 
	//for (int i = 0; i < avg_readers_per_phase.size(); i++) printf("%f ", avg_readers_per_phase[i]); 
	//printf("\n");

	//printf("averge # tags per phase: "); 
	//for (int i = 0; i < avg_tags_per_phase.size(); i++) printf("%f ", avg_tags_per_phase[i]); 
	//printf("\n");



	printf("stdev # iterations: %f \n", std_iterations);
	printf("stdev # num redundant: %f \n", std_num_redundant);
	printf("stdev # num non redundant: %f \n", std_num_non_redundant);
	
	//printf("stdev # readers per phase: "); 
	//for (int i = 0; i < std_readers_per_phase.size(); i++) printf("%f ", std_readers_per_phase[i]); 
	//printf("\n");

	//printf("stdev # tags per phase: "); 
	//for (int i = 0; i < std_tags_per_phase.size(); i++) printf("%f ", std_tags_per_phase[i]); 
	//printf("\n");

}

void print_in_file(char* filename) { 
	FILE* f = fopen(filename, "w"); 
	map<int, vector<int> >::iterator it; 

	if (!f) { 
		printf("Error: file %s cannot be open \n", __FUNCTION__); 
		return; 
	} else { 
		it = graph.begin(); 
		while (it != graph.end()) { 
			for (int i =0 ; i < it->second.size(); i++) { 
				fprintf(f, "%d ", it->second[i]);
			}	
			fprintf(f, "\n");	
			it ++;
		}
	}
	fclose(f);	

}

float average(vector<float>* array) { 
	float sum = 0; 
	for (int i = 0; i < array->size(); i++) sum += array->at(i);
	return (sum / (float) array->size());
}

float average(vector<int>* array) { 
	float sum = 0; 
	for (int i = 0; i < array->size(); i++) sum += array->at(i);
	return (sum / (float) array->size());
}

void treat_line() { 
	if (current_id > -1) { 
		graph[current_id] = current_tags; 
	}
}

void reset_line() { 
	current_id = -1; 
	current_tags.clear();
}

void print_vector(vector<int>& v) { 
	for (int i = 0; i < v.size(); i++) 
		printf("%d ", v[i]); 
	printf("\n");
}

int is_in_array(string* array, string str, int size) { 
	// printf("at %s: with size = %d \n", __FUNCTION__, size); 

	int i = 0;
	while (i < size) { 
		 
		if (array[i].compare(str) == 0) { return i; }
		i ++; 
	}
	return -1; 
}

int getline_by_number(FILE* f, char line[], int max, int line_number) {

	char c; 

	// go to line line_number 
	int j = 0; 
	if (line_number > 0) { 
		while ((c = fgetc(f)) != EOF) { 
			if (c == '\n') { j++; if (j == line_number) break; }
		}
		if (c == EOF) { return EOF; }
	}

	int result = getline(f, line, max);
	reset_file(f); 
	return result; 

}

bool tokenize(char* line, vector<string>* str) { 
	if (!line) { printf("error: line"); return false; } 
	if (!str) { printf("error: str"); return false; }
	
	
	string str_pointer; 
	char* res = new char[1256];
	trim(line, res);
	//char* res = trim(line);
	char* tok = NULL; 

	tok = strtok(res, " "); 
	while (tok != NULL) { 
		//str_pointer = new string(tok); 
		str_pointer.clear();
		str_pointer.append(tok);
		// printf("pushing %s \n", tok);
		str->push_back(str_pointer);
		// delete str_pointer; 
		tok = strtok(NULL," ");
		
	}

	// delete [] res; 
	return true; 
}



void trim(char* input, char* output) { 
	if (!input) return; 
	if (!output) return; 

	int r_index = 0; 
	while (*input) { 
		if (*input == '\t' || *input == ' ') { 
			output[r_index++] = ' ';
			
		} else { 
			output[r_index++] = *input; 
			
		}
		input++;
	}	
	output[r_index] = '\0';
}


bool reset_file(FILE* f) {
	return fseek(f, SEEK_SET, 0);
} 

int getline(FILE* f, char line[], int max)
{
	int nch = 0;
	char c;
	max = max - 1;			/* leave room for '\0' */

	while((c = fgetc(f)) != EOF)
	{
		if(c == '\n')
			break;

		if(nch < max)
		{
			line[nch] = c;
			nch = nch + 1;
		}
	}

	if(c == EOF && nch == 0)
		return EOF;

	line[nch] = '\0';
	return nch;
}

void print_string(string str) { 
	cout << str << endl; 
}

int is_tag(string str) { 
	return (is_in_array(tags, str, TAGS_SIZE)); 
}

Type return_type(string str){ 
	
	Type result = NONE; 
	bool cond_float = false; 

	char* t_cstr = new char [str.size()+1];
  	strcpy (t_cstr, str.c_str());
	char* cstr = t_cstr;
	
	while(*cstr) { 
		// printf("testing t_str = %c \n", *cstr);
		if (!(*cstr >= '0' && *cstr <= '9')) { 
		if (*cstr != '.') { delete [] t_cstr; return  STRING;  } 
		if (*cstr == '.' && cond_float) { delete [] t_cstr; return STRING; }
		else if (*cstr == '.' && !cond_float) { cond_float = true;  } 
		}
		cstr++; 
	}
	if (cond_float) { result = FLOAT; /* printf("result is set to float \n"); */ } 
	else { result = INT; /* printf("result is set to int \n"); */ } 
	
	delete [] t_cstr; 
	return result; 
	
}



int process_tag_in_line(int t_num, vector<string>* str, int tag_type) { 
	// TODO: a map of functions 
	if (D) { printf("at %s t_num = %d  tag_type = %d \n",__FUNCTION__, t_num, tag_type);   }	

	t_num ++;

	string tags[] = {"<num_redundant>", "<num_non_redundant>", "<iterations>", "<num_tags_phase>", "<num_readers_phase>", "<id>", "<tags>"};
	if (tag_type == 0) { return process_int(t_num, str, NUM_REDUNDANT); }
	if (tag_type == 1) { return process_int(t_num, str, NUM_NON_REDUNDANT); }
	if (tag_type == 2) { return process_int(t_num, str, ITERATIONS); }
	if (tag_type == 3) { return process_vector_int(t_num, str, NUM_TAGS_PHASE); }
	if (tag_type == 4) { return process_vector_int(t_num, str, NUM_READERS_PHASE); }
	if (tag_type == 5) { return process_int(t_num, str, ID); }
	if (tag_type == 6) { return process_vector_int(t_num, str, TAGS); }

}


int process_int(int t_num, vector<string>* str, TagType t) { 
	if (D) printf("at %s \n",__FUNCTION__); 

	if (return_type(str->at(t_num)) != INT) { 
		printf("error at %s: - expecting integer but found %d \n", __FUNCTION__, return_type(str->at(t_num)));
		return -1; 
	}	
	else {
		char* cstr;
		cstr = new char [str->at(t_num).size()+1];
  		strcpy (cstr, str->at(t_num).c_str());
		
		switch (t) { 
			case ID: 
				current_id = atoi(cstr); 
				break; 
			case NUM_REDUNDANT: 
				num_redundant = atoi(cstr); 
				break; 
			case NUM_NON_REDUNDANT: 
				num_non_redundant = atoi(cstr); 
				break; 
			case ITERATIONS: 
				iterations = atoi(cstr); 
				break; 
			default: 
				printf("Error: we can not treat tagtype %d \n", t);
				abort();	
		}

		delete [] cstr; 
	}
	t_num ++;

	if ( t_num < str->size() && is_tag(str->at(t_num)) == -1) { printf("error at %s; - expecting tag \n", __FUNCTION__); return -1; }
	
	if (D) printf("returning %d from %s \n", t_num, __FUNCTION__);
	return t_num;

}


int process_vector_int(int t_num, vector<string>* str, TagType t) { 
	if (D) printf("at %s \n", __FUNCTION__); 


	if (D) printf("t_num : %d - str->size() : %d \n", t_num, str->size());
	
	// while (is_tag(str->at(t_num)) == -1 && t_num < str->size()) { 
	while (t_num < str->size())  {
		if (is_tag(str->at(t_num)) == -1) { 		
			if (return_type(str->at(t_num)) != INT) { 
				cout << "error at process_black - expecting integer or tag but found " ;
				cout << str->at(t_num) << " which is " << return_type(str->at(t_num));
				return -1; 
			}
		// process black

			char* cstr;
			cstr = new char [str->at(t_num).size()+1];
  			strcpy (cstr, str->at(t_num).c_str());

			switch (t) { 
				case NUM_TAGS_PHASE: 
					tags_per_phase.push_back(atoi(cstr)); 
					break; 
				case NUM_READERS_PHASE: 
					readers_per_phase.push_back(atoi(cstr));
					break; 
				case TAGS: 
					current_tags.push_back(atoi(cstr));
					break; 
				default: 
					printf("Error: we can not treat tagtype %d \n", t);
					abort();
			}

	
			t_num ++;
			delete [] cstr;
		} else {
			break;
		}
	}

	if (D) printf("returning %d from %s \n", t_num, __FUNCTION__);
	return t_num; 

}


void save_results() { 
	file_iterations.push_back(iterations); 
	file_num_redundant.push_back(num_redundant); 
	file_num_non_redundant.push_back(num_non_redundant); 
	file_tags_per_phase.push_back(tags_per_phase);
	file_readers_per_phase.push_back(readers_per_phase);	
}

void analyze_results() { 
	avg_iterations  = average(&file_iterations); 
	std_iterations  = stdev(&file_iterations); 
	
	avg_num_redundant = average(&file_num_redundant);
	std_num_redundant = stdev(&file_num_redundant);

	avg_num_non_redundant = average(&file_num_non_redundant); 
	std_num_non_redundant = stdev(&file_num_non_redundant);

	vector<int> temp_vector; 
	int max_vector_size = 0; 
	for (int i = 0; i < file_tags_per_phase.size(); i++) { 
		if (file_tags_per_phase[i].size() > max_vector_size) max_vector_size = file_tags_per_phase[i].size(); 
	}

	for (int i = 0; i < max_vector_size; i++) {
		for (int j = 0; j < file_tags_per_phase.size(); j++) {
			if (i < file_tags_per_phase[j].size()) { 
				temp_vector.push_back(file_tags_per_phase[j][i]);
			}

			avg_tags_per_phase.push_back(average(&temp_vector));
			std_tags_per_phase.push_back(stdev(&temp_vector));
			temp_vector.clear();			
		}
	}


	for (int i = 0; i < file_readers_per_phase.size(); i++) { 
		if (file_readers_per_phase[i].size() > max_vector_size) max_vector_size = file_readers_per_phase[i].size(); 
	}

	for (int i = 0; i < max_vector_size; i++) {
		for (int j = 0; j < file_readers_per_phase.size(); j++) {
			if (i < file_readers_per_phase[j].size()) { 
				temp_vector.push_back(file_readers_per_phase[j][i]);
			}

			avg_readers_per_phase.push_back(average(&temp_vector));
			std_readers_per_phase.push_back(stdev(&temp_vector));
			temp_vector.clear();			
		}
	}
}

float stdev(vector<int>* array) { 
	float avg = average(array); 
	float sum_squares = 0.0; 
	for (int i = 0; i < array->size(); i++)  
		sum_squares += pow(((float) array->at(i) - avg),2);
	return sqrt(sum_squares);
}


float stdev(vector<float>* array) { 
	float avg = average(array); 
	float sum_squares = 0.0; 
	for (int i = 0; i < array->size(); i++)  
		sum_squares += pow(((float) array->at(i) - avg),2);
	return sqrt(sum_squares);
}

