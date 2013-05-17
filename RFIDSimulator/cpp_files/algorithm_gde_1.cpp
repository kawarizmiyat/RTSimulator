#include "algorithm_ver1.h"
#include <stdio.h>
#include <string>
#include <string.h>
#include <map>
#include <vector>
#include <algorithm>
using namespace std; 

// functions defintions 

void create_bipartite_graph_1(map<int, vector<int> >*); 
void create_bipartite_graph_2(map<int, vector<int> >*); 

void print_graph(map<int, vector<int> >*); 
void get_active_tags(map<int, vector<int> >*, vector<int>*, vector<tag>*); 
void get_readers(map<int, vector<int> >*, vector<reader>*);
void add_unique_int_to_vector(vector<int>*, int); 
void write_tags(vector<reader>* readers, vector<tag>* tags, vector<int>* non_set_tags);
void write_contents(int r, int t); 
void add_unique_content_to_tag(tag* t, tag_tuple* tt);
int tag_by_id(vector<tag>* , int);
int reader_by_id(vector<reader>*, int); 
void make_decision(vector<reader>*, int);
void initiate_reader_round(vector<reader>* r, int r_index); 
void make_decision_rre(vector<reader>* readers, int r_index); 
void find_max(tag* tag, vector<reader>*, int, int&);
void find_max_reader(vector<int> id, vector<int> sizes , int& max_index);
int compare_id_tagcount(int id1, int tag_count1, int id2, int tag_count2); 
void set_owner_tag(vector<reader>* readers, int r_index, int tag_id, int owner); 
void check_owner(tag* t, bool& owned, int& owner); 
void deactivate(vector<reader>* , int , vector<tag>* tags, int t_index); 
void deactivate_by_tag_id(vector<reader>* , int , int);
bool remove_to_be_written_tags(vector<reader>* readers, int r_index, int tag_id);
vector<int> intersection(vector<int> n_1, vector<int> n_2); 
void tag_is_set(vector<int>* nstags, int tag_id); 
bool is_in_vector(vector<int>* v, int value);
void add_to_owned_tags(vector<reader>* readers, int r_index, int tag_id); 
void verify_solution(vector<reader>*, vector<tag>*, vector<int>*);
void update_active_tags_readers(vector<reader>* readers, vector<tag>* tags, 
			vector<int>* non_active_tags_per_phase, vector<int>* num_active_readers_per_phase, vector<int>* non_set_tags); 


bool tokenize(char* line, vector<string>* str); 
int getline(FILE* f, char line[], int max); 
void trim(char* input, char* output); 
void extract_bipartite_graph(map<int, vector<int> >*, char*);

int get_owner_tag(vector<reader>* readers, int r_index, int tag_id); 
double average_int(vector<int>* v);

// public variables 
vector<int> active_tags; 
vector<reader> readers; 
vector<int> redundant_readers, non_redundant_readers; 
vector<tag> tags; 
vector<int> non_set_tags; 
vector<int> num_active_tags_per_phase; 
vector<int> num_active_readers_per_phase;

enum ALGORITHM {GDE, RRE};
ALGORITHM algorithm; 
bool d = true; 			// for debugging


void print_non_set_tags() { 
	
	
	for (int i =0 ; i < non_set_tags.size(); i++) { 
		tags.at(tag_by_id(&tags, non_set_tags[i])).print_tag();
	}

}

int main(int argc, char** argv) { 


	if (argc != 3) {
		printf("Error: input argument should be on form: \n"); 
		printf("./dot_exe_file input_file output_file \n");
		return 0;
	}	

	map<int, vector<int> > graph; 

	char input_file[50];
	char output_file[50];


	sprintf(input_file, "%s", argv[1]);  
	sprintf(output_file, "%s", argv[2]);

	extract_bipartite_graph(&graph, input_file);

	get_active_tags(&graph, &active_tags, &tags);

	for (int i = 0; i < active_tags.size(); i++) { 
		non_set_tags.push_back(active_tags[i]);
	} 

	get_readers(&graph, &readers);

	
		
#if 1

	////////////////////////////////////////////////////////////////////////
	int iteration_count = 1; 
	int n_active_readers;
	while (non_set_tags.size() > 0 ) { 
		
		update_active_tags_readers(&readers, &tags, &num_active_tags_per_phase, &num_active_readers_per_phase, &non_set_tags);

		printf("*********** iteration %d *********** \n", iteration_count); 
		// write_tags(&readers, &tags);			// Wait:  
		write_tags(&readers, &tags, &non_set_tags);		


		print_non_set_tags();
		#if 0
		// print tags info
		if (true) { 		
		printf("tags info at %s \n", __FUNCTION__); 
		for (int i = 0; i < tags.size(); i++) { 
		 	tags[i].print_tag();
		} 
		}
		#endif 

		#if 0	
		printf("readers with active tags at %s \n", __FUNCTION__); 
		for (int i = 0; i < readers.size(); i++) { 
		printf("reader %d: ", i);
		for (int j = 0; j < readers[i].to_be_written_tags.size(); j++) { 
				printf("%d ", readers[i].to_be_written_tags[j]);
		}
		printf("\n");
		}
		#endif 

		for (int i = 0; i < readers.size(); i++) {  
			if (algorithm == GDE) { 
				make_decision(&readers, i);
			} else if (algorithm = RRE) { 
				make_decision_rre(&readers, i);
			}
		}

		if (algorithm == RRE) { 
			for (int i = 0; i < tags.size(); i++) { 
				tag_is_set(&non_set_tags, tags[i].id);
			}
		}

		

		iteration_count ++;
		if (iteration_count > readers.size() + 1  ) { 
			printf("iteration count > reader size -- error ? ");
			// break;
		}
	}


	// printf("printing redundant readers \n");
	 for (int i = 0; i < readers.size(); i++) { 
		//printf("reader %d: ", readers[i].id); 
		if (readers[i].owned_tags.size() != 0) { 
			//printf("non redundant \n");
			non_redundant_readers.push_back(readers[i].id);
		} else { 
			//printf("redundant \n");
			redundant_readers.push_back(readers[i].id);
		}
	}


	// write in file: 
	FILE* f = fopen(output_file, "w"); 
	if (!f) { 
		printf("Error: could not open output file %s \n", output_file);
		return 0; 
	} else { 	
		fprintf(f, 	"<num_redundant> %d ", redundant_readers.size()); 
		fprintf(f,  "<num_non_redundant> %d ", non_redundant_readers.size()); 
		fprintf(f,  "<iterations> %d ", iteration_count - 1);
		fprintf(f,  "<num_tags_phase> "); 
		for (int i = 0; i < num_active_tags_per_phase.size(); i++) {
			fprintf(f, "%d ", num_active_tags_per_phase[i]);
		}
		fprintf(f, "<num_readers_phase> "); 
		for (int i = 0; i < num_active_readers_per_phase.size(); i++) { 
			fprintf(f, "%d ", num_active_readers_per_phase[i]); 
		}
		fprintf(f, "\n");

		// printing non_redundant readers:
		vector<int> temp_vector; 
		for (int i = 0; i < non_redundant_readers.size(); i++ ) { 
			fprintf(f, "<id> %d ", non_redundant_readers[i]);
			fprintf(f, "<tags> ");
			temp_vector = readers.at(reader_by_id(&readers, non_redundant_readers[i])).neighbors_tags;
			for (int j = 0; j < temp_vector.size(); j++) { 
				fprintf(f, "%d ", temp_vector.at(j));
			}
			temp_vector.clear();
			fprintf(f,"\n");				 
		} 
		fclose(f);
	}


#endif 
}

double average_int(vector<int>* v) { 
	
	double sum = 0.0; 
	for (int i = 0; i < v->size(); i++) 
		sum += (double) v->at(i); 
	return sum / (double) v->size(); 
	
}

void update_active_tags_readers(vector<reader>* readers, vector<tag>* tags, 
			vector<int>* num_active_tags_per_phase, vector<int>* num_active_readers_per_phase, vector<int>* non_set_tags) {

		int n_active_readers = 0; 
	
		num_active_tags_per_phase->push_back(non_set_tags->size());
		

		// printf("@ %s \n", __FUNCTION__);
		for (int i = 0; i < readers->size(); i++) {
			printf("********* reader %d:", i); 
			for (int j =0 ; j < readers->at(i).active_tags.size(); j++) 
				printf("%d ", readers->at(i).active_tags[j]);
			printf("\n");

			if (readers->at(i).active_tags.size() > 0) n_active_readers ++; 
		}
		num_active_readers_per_phase->push_back(n_active_readers);
}

void verify_solution(vector<reader>* readers, vector<tag>* tags, vector<int>* non_redundant_readers) {
	vector<int> tags_of_non_red; 
	vector<int> all_tags; 
	vector<int> temp_vector; 

	for (int i = 0; i < non_redundant_readers->size(); i++) {
		temp_vector = readers->at(reader_by_id(readers, non_redundant_readers->at(i))).neighbors_tags; 
		for (int j = 0; j < temp_vector.size(); j++) {
			add_unique_int_to_vector(&tags_of_non_red, temp_vector[j]);
		}
	}

	for (int i = 0; i < tags->size(); i++) { 
		all_tags.push_back(tags->at(i).id);
	}

	sort(tags_of_non_red.begin(), tags_of_non_red.end()); 
	sort(all_tags.begin(), all_tags.end());
	if (equal(tags_of_non_red.begin(), tags_of_non_red.end(), all_tags.begin())) printf("solution is correct \n"); 
	else { printf("solution is wrong \n"); abort(); }
}

void initiate_reader_round(vector<reader>* r, int r_index) { 
	
	r->at(r_index).tag_count = r->at(r_index).to_be_written_tags.size();
	if (r->at(r_index).tag_count < 0) { printf("Errora at %s \n", __FUNCTION__); abort(); }	
}


void create_bipartite_graph_1(map<int, vector<int> >* graph) { 
	
	int first_array[] = {1,2,3};
    vector<int> first (first_array, first_array + sizeof(first_array) / sizeof(int) );

	for (int i = 1; i <= 3; i++) {
		graph->insert( pair <int, vector<int> >(i, first));
	}

}

void create_bipartite_graph_2(map<int, vector<int> >* graph) { 
	int first_array[] = {1,2}; 
	int second_array[] = {2,3}; 
	int third_array[] = {3};
	
	vector<int> first (first_array, first_array + sizeof(first_array) / sizeof(int)); 
	vector<int> second (second_array, second_array + sizeof(second_array) / sizeof(int)); 
	vector<int> third (third_array, third_array + sizeof(third_array) / sizeof(int)); 
	
	graph->insert( pair <int, vector<int> >(1, first) ); 
	graph->insert( pair <int, vector<int> >(2, second) ); 
	graph->insert( pair <int, vector<int> >(3, third) ); 

}

void print_graph(map<int, vector<int> >* g) { 

	map<int, vector<int> >::iterator it; 
	it = g->begin(); 
	while (it != g->end()) { 
		fprintf(stderr, "%d: ", it->first);
		for (int i = 0; i < it->second.size(); i++) { 
			fprintf(stderr, "%d ", it->second.at(i)); 
		}
		fprintf(stderr, "\n");
		it ++;
	}
}

void get_active_tags(map<int, vector<int> >* graph, vector<int>* tags, vector<tag>* tags_info) { 
	
	//printf("at %s \n", __FUNCTION__); 

	if (!graph) return; 
	if (!tags) return; 
	if (!tags_info) return; 
	
	tags->clear(); 	
	tags_info->clear();
	

	map<int, vector<int> >::iterator it; 
	it = graph->begin(); 
	while (it != graph->end()) { 
		
		// for each tag ti in the reader tags: 
		for (int i = 0; i < it->second.size(); i++) { 
			// search for it->second[i] in tags: 
			if ( ! is_in_vector(tags, it->second[i]) ) { 
				tags->push_back(it->second[i]);
			} 
		}
		it ++;
	}

	sort(tags->begin(), tags->end());


	tag* temp_tag; 
	int found_reader; 
	vector<int>* temp; 
	for (int i = 0; i < tags->size(); i++) { 

		temp_tag = new tag(); 
		temp_tag->id = tags->at(i); 

		// find neighboring reader: 
		temp = new vector<int>(); 
		it = graph->begin(); 
		while (it != graph->end()) { 
			
			found_reader = -1; 
			for (int j = 0 ; j < it->second.size(); j++) { 
				if (tags->at(i) == it->second.at(j)) {found_reader = it->first;  break; }
			}			
			
			if (found_reader != -1) {
				temp->push_back(found_reader); 
			}	
			it++; 
		}

		for (int i = 0; i < temp->size(); i++) { 
			temp_tag->neighbors_readers.push_back(temp->at(i)); 
		}		

		temp_tag->final_owner = -1; 
		tags_info->push_back(*temp_tag); 
	}

}


bool is_in_vector(vector<int>* v, int value) { 
	if (!v) return false; 
	
	for (int i = 0; i < v->size(); i++) { 
		if (v->at(i) == value) return true;
	}

	return false;
}

void get_readers(map<int, vector<int> >* graph, vector<reader>* readers) { 
	if (!graph) return; 
	if (!readers) return; 

	readers->clear();	
	reader* temp_reader; 
	vector<int> temp_vector; 	

	map<int, vector<int> >::iterator it; 
	it = graph->begin(); 


	//printf("getting: readers info \n");
	while (it != graph->end()) { 
		temp_reader = new reader(); 				
		temp_reader->id = it->first;
		
		for (int i = 0; i < it->second.size(); i++) { 
			temp_reader->neighbors_tags.push_back(it->second.at(i));
			temp_reader->active_tags.push_back(it->second.at(i));
			temp_reader->to_be_written_tags.push_back(it->second.at(i));
			temp_reader->neighbors_tags_owners.push_back(-1); 
		}	
		readers->push_back(*temp_reader);

		it ++;

	}	

	//printf("getting neighboring readers \n");
	vector<int> intersect; 
	for (int i = 0; i < readers->size(); i++) { 
		for (int j = 0; j < readers->size(); j++) { 
			if (i != j) { 
				intersect = intersection(readers->at(i).neighbors_tags, readers->at(j).neighbors_tags);
				if (intersect.size() > 0) {
					add_unique_int_to_vector(&readers->at(i).neighbors_readers, readers->at(j).id);
				}
 				intersect.clear(); 
			}
		}
	}

}


void add_unique_int_to_vector(vector<int>* v, int x) { 
	if (!v) return ; 
	for (int i = 0; i < v->size(); i ++) { 
		if (x == v->at(i)) 
		return;
	}
	v->push_back(x);
}


//void write_tags(vector<reader>* readers, vector<tag>* tags) {
void write_tags(vector<reader>* readers, vector<tag>* tags, vector<int>* non_set_tags) {
	// there is an error here apparantly ... write should be on active tags only ! 	
	// if (d) printf("at %s \n", __FUNCTION__);

	for (int i = 0; i < readers->size(); i++) { 
		for (int j = 0; j < readers->at(i).neighbors_tags.size(); j++) { 
			
			//printf("write contents of reader: %d to tag %d \n", readers->at(i).id, readers->at(i).neighbors_tags.at(j));
			
			// if statement added: to fix error
			if (is_in_vector(non_set_tags, readers->at(i).neighbors_tags.at(j))) {  
				write_contents(readers->at(i).id, readers->at(i).neighbors_tags.at(j));
			}
		}
	}

}

void write_contents(int r, int t) { 
	//printf("write contents reader %d and tag %d \n", r, t);
	bool d; 
	if (r == 115 && t == 1) { 
		d = true; 
	} else { 
		d = false; 
	}
	d = false; 

	int index_r = reader_by_id(&readers, r); 
	int index_t = tag_by_id(&tags, t);
	
	tag_tuple temp_tuple; 
	temp_tuple.id = r;

	// find owner of tag t
	int owner = -2;
	
	for (int i = 0; i < readers[index_r].neighbors_tags.size(); i++) { 
		// if (d) printf("checking neighbor tag: %d \n", readers[index_r].neighbors_tags[i]);
		if (readers[index_r].neighbors_tags[i] == t) { 
			owner = get_owner_tag(&readers, index_r, t); 
			// owner = readers[index_r].neighbors_tags_owners[i]; 
		} 
	}

	if (owner == -2) { 
		printf("error: a neighbor tag has no corresponding owner \n"); 
		abort();
	}

	// if (owner == -1) { printf("owner is -1 \n"); }
	temp_tuple.owner = owner; 	


/*
	for (int i = 0; i < readers[index_r].active_tags.size()	; i++) { 
		temp_tuple.tags.push_back(readers[index_r].active_tags[i]); 
	}
*/ 
	for (int i = 0; i < readers[index_r].to_be_written_tags.size(); i++) { 
		temp_tuple.tags.push_back(readers[index_r].to_be_written_tags[i]);
	}
	

	if (d) { 
		printf("at %s \n", __FUNCTION__);
		printf("reader %d add unique content tuple to tag %d with: id: %d, owner: %d, tags:", r, t, temp_tuple.id, temp_tuple.owner);
		for (int i = 0; i < temp_tuple.tags.size(); i++)  
			printf("%d ", temp_tuple.tags[i]);
		printf("\n");
 	}

	add_unique_content_to_tag(&tags[index_t], &temp_tuple);

}

void add_unique_content_to_tag(tag* t, tag_tuple* tt) {
	if (!t) return; 
	if (!tt) return;
	
	//printf("at %s \n", __FUNCTION__); 
	for (int i = 0; i < t->contents.size(); i++) { 
		if (t->contents[i].id == tt->id) {
			t->contents[i].owner = tt->owner; 
			t->contents[i].tags.clear(); 
			for (int j = 0; j < tt->tags.size(); j++) { 
				t->contents[i].tags.push_back(tt->tags[j]);
			}
		//printf("content updated \n");
		return;
		}
	}
	
	//printf("content is not alraedy there \n");
	t->contents.push_back(*tt);

}

int tag_by_id(vector<tag>* tags, int t) { 
	for (int i = 0; i < tags->size(); i++) { 
		if (tags->at(i).id == t) return i;
	}
	return -1;
}

int reader_by_id(vector<reader>* readers, int r){ 
	for (int i = 0; i < readers->size(); i++) { 
		if (readers->at(i).id == r) return i; 
	}
	return -1; 
}


void make_decision_rre(vector<reader>* readers, int r_index) { 
	// printf("%s for reader %d \n", __FUNCTION__, readers->at(r_index).id);

	int index_t; 
	int max_id; 
	bool owned;
	int owner; 
	
	// copy vectors
	vector<int> active_tags; 
	for (int i = 0; i < readers->at(r_index).active_tags.size(); i++) { 
		active_tags.push_back(readers->at(r_index).active_tags[i]);
	}	


	initiate_reader_round(readers, r_index);

	for (int i = 0; i < active_tags.size(); i++) { 
		// printf("reader %d reads the contents of tag %d \n", readers->at(r_index).id, active_tags[i]);
	
		index_t = tag_by_id(&tags, active_tags[i]); 
		
		find_max(&tags[index_t], readers, r_index, max_id); 
		// printf("reader %d find reader %d as maximum  in tag %d \n", readers->at(r_index).id, max_id, active_tags[i]);		
		
		set_owner_tag(readers, r_index, active_tags[i], max_id);
		// tag_is_set(&non_set_tags, active_tags[i]); 

		// deactivate()
		if (max_id == readers->at(r_index).id) { 
			// TODO: add to my owned tags and 	
			add_to_owned_tags(readers, r_index, active_tags[i]);
			// deactivate_by_tag_id(readers, r_index, active_tags[i]);
	
		}
	}


	
}


void make_decision(vector<reader>* readers, int r_index) { 

	// for each active tag: 
	// 	1. read tag: 
	//  2. find maximum reader in tag: considering the value of reader r that r knows ! 
	//  3. set owner of tag
	//  4. update active tags 

	

	int index_t; 
	int max_id; 
	bool owned;
	int owner; 
	vector<int> to_deactivate_later;
	vector<int> intersection_result; 
	vector<int> active_tags; 
	bool d = true; 	
	
	if (d) printf("******* make decision for reader %d \n", readers->at(r_index).id);
	
	// copy vectors
	for (int i = 0; i < readers->at(r_index).active_tags.size(); i++) { 
		active_tags.push_back(readers->at(r_index).active_tags[i]);
	}	
	

	initiate_reader_round(readers, r_index);

	for (int i = 0; i < active_tags.size(); i++) { 


		index_t = tag_by_id(&tags, active_tags[i]);
		if(d) printf("reading tag %d with id: %d: ", index_t, active_tags[i]);
		check_owner(&tags[index_t], owned, owner);
		
		if (tags[index_t].contents.size() == 1 && tags[index_t].contents[0].id == readers->at(r_index).id) {
		 	if (d) printf("special case: tag %d has only one reader \n", tags[index_t].id);			
			set_owner_tag(readers, r_index, active_tags[i], readers->at(r_index).id);
			add_to_owned_tags(readers, r_index, active_tags[i]);
			tag_is_set(&non_set_tags, active_tags[i]);
			deactivate_by_tag_id(readers, r_index, active_tags[i]); 
			remove_to_be_written_tags(readers, r_index, active_tags[i]);

		} else if ( owner == readers->at(r_index).id ) { 
			if (d) printf("the owner is myself \n");
			set_owner_tag(readers, r_index, active_tags[i], owner);
			add_to_owned_tags(readers, r_index, active_tags[i]);
			tag_is_set(&non_set_tags, active_tags[i]); 
			deactivate_by_tag_id(readers, r_index,  active_tags[i]);
			remove_to_be_written_tags(readers,  r_index, active_tags[i]);			

			// readers->at(r_index).tag_count --; 
			// if (reader->at(r_index).tag_count < 0) printf("Error: reader %d has a tag count less than zero \n");
		} else if ( owner != -1) { 
			if (d) printf("the owner is not myself \n");
			set_owner_tag(readers, r_index, active_tags[i], owner); 
			tag_is_set(&non_set_tags, active_tags[i]); 
			deactivate_by_tag_id(readers, r_index, active_tags[i]); 
			if (remove_to_be_written_tags(readers, r_index, active_tags[i]))
				readers->at(r_index).tag_count --; 
			
			if (readers->at(r_index).tag_count < 0) { readers->at(r_index).tag_count = 0; }

		} else { 
			if (d) printf("we cannot decide the owner from the information given\n");	
			find_max(&tags[index_t], readers, r_index, max_id); 

			if (d) printf("after find max: we found max_id: %d \n", max_id);
			if (max_id == -1 ) {printf("Warning: max_id = -1 : %d \n", max_id); abort();}

			set_owner_tag(readers, r_index, active_tags[i], max_id);
			if (d) { 
				
				int new_owner = get_owner_tag(readers, r_index, active_tags[i]);

				printf("the new owner of tag %d for reader %d is %d ", active_tags[i], readers->at(r_index).id, 
						new_owner ); 
				printf("while max_id: %d \n", max_id);
			}

			if (max_id != readers->at(r_index).id) { 
				// TODO: [[WHY?]] 
				// instead of deactivating the tags, decrease the tagcount ! 

				// get neigbhors tags of max_id 
				// vector<int> max_id_neigbhors_tags = readers->at(reader_by_id(readers, max_id)).neighbors_tags; 
				vector<int> max_id_neigbhors_tags = readers->at(reader_by_id(readers, max_id)).to_be_written_tags;
				intersection_result.clear();
				intersection_result = intersection(readers->at(r_index).to_be_written_tags, max_id_neigbhors_tags);
				// decrease tag_cont; 
				// readers->at(r_index).tag_count -= intersection_result.size();	
				
				// GDE 1
				#if 1
				for (int k = 0; k < intersection_result.size(); k++) { 
					if (remove_to_be_written_tags(readers, r_index, intersection_result[k])) { 
						readers->at(r_index).tag_count -= 1; 
						if (readers->at(r_index).tag_count < 0) { printf("Error: tag_count < 0 at reader %d \n", r_index); abort(); }
					}
					if (d) printf("removing tag %d from written tags \n", intersection_result[k]);
				}
				#endif

				// GDE 2
				#if 0
				for (int k = 0; k < intersection_result.size(); k++) { 
					//if (remove_to_be_written_tags(readers, r_index, intersection_result[k])) { 
					if (is_in_vector(&readers->at(r_index).to_be_written_tags, intersection_result[k])) {
						readers->at(r_index).tag_count -= 1; 
						if (readers->at(r_index).tag_count < 0) { printf("Error: tag_count < 0 at reader %d \n", r_index); abort(); }
					}
					if (d) printf("removing tag %d from written tags \n", intersection_result[k]);
				}		
				#endif
			}		
		}

	}

}

int get_owner_tag(vector<reader>* readers, int r_index, int tag_id) { 
	if (!readers) return -2; 
	if (readers->at(r_index).neighbors_tags.size() != readers->at(r_index).neighbors_tags_owners.size())  { 
		printf("error at %s \n", __FUNCTION__); 
		abort();
	}

	int f_index = -1;
	for (int i = 0; i < readers->at(r_index).neighbors_tags.size(); i++) { 
		if (readers->at(r_index).neighbors_tags[i] == tag_id) {f_index = i; break; }
	}

	if (f_index == -1) { 
		printf("tag %d has no owner ! \n", tag_id);
		abort();
	}
	int result = readers->at(r_index).neighbors_tags_owners[f_index]; 
	//printf("result of %s is %d \n", __FUNCTION__,  result); 
	return result;

}

/*
void make_decision(vector<reader>* readers, int r_index) { 
	// for each active tag: 
	// 	1. read tag: 
	//  2. find maximum reader in tag: considering the value of reader r that r knows ! 
	//  3. set owner of tag
	//  4. update active tags 

	// printf("make decision for reader %d \n", readers->at(r_index).id);

	int index_t; 
	int max_id; 
	bool owned;
	int owner; 
	vector<int> to_deactivate_later;
	vector<int> intersection_result; 
	vector<int> active_tags; 


	// copy vectors
	for (int i = 0; i < readers->at(r_index).active_tags.size(); i++) { 
		active_tags.push_back(readers->at(r_index).active_tags[i]);
	}	
	

	// printf("checking already written owners of the tags \n");
	for (int i = 0; i < active_tags.size(); i++) { 
		index_t = tag_by_id(&tags, active_tags[i]);

		check_owner(&tags[index_t], owned, owner);
		printf("after check_owner of tag %d: owner: %d \n", tags[index_t].id, owner);

				
		

 
		if (tags[index_t].contents.size() == 1 && tags[index_t].contents[0].id == readers->at(r_index).id) {
			printf("special case: tag %d has only one reader \n", tags[index_t].id);			
			owned = true; 
			set_owner_tag(readers, r_index, active_tags[i], readers->at(r_index).id);
			add_to_owned_tags(readers, r_index, active_tags[i]);
			// TODO: this should be changed to deactivate later !
			// deactivate_by_tag_id(readers, r_index, active_tags[i]);
			to_deactivate_later.push_back(active_tags[i]);

			tag_is_set(&non_set_tags, active_tags[i]);
		} 
	
		if (owner == readers->at(r_index).id) { 
			printf("tag is owned by reader %d - which is me ! \n", readers->at(r_index).id);

			set_owner_tag(readers, r_index, active_tags[i], owner);
			add_to_owned_tags(readers, r_index, active_tags[i]);
			tag_is_set(&non_set_tags, active_tags[i]); 

		} else if (owner != -1) { 
			printf("tag is owned by reader %d which is not me ! \n", owner);			
	
			set_owner_tag(readers, r_index, active_tags[i], owner);
			tag_is_set(&non_set_tags, active_tags[i]); 

			vector<int> owner_neighbors_tags= readers->at(reader_by_id(readers, owner)).neighbors_tags;
			intersection_result = intersection(readers->at(r_index).neighbors_tags, owner_neighbors_tags);

			for (int j = 0; j < intersection_result.size(); j++) { 
				deactivate_by_tag_id(readers, r_index,  intersection_result[j]);
			}

		}
	}
	printf("end finding final owners phase  \n");
	printf("start finding final owners phase \n");

	initiate_reader_round(readers, r_index);

	 
	if (readers->at(r_index).active_tags.size() == 0) { 
		// consider reader->at(r_index) == inactive ! 
		printf("reader %d has no active tags ! \n", readers->at(r_index).id);
	} else { 

	// main loop: [before this loop, it is all complementary.
	for (int i = 0; i < active_tags.size(); i++) { 
		printf("reader %d reads the contents of tag %d \n", readers->at(r_index).id, active_tags[i]);
	
		index_t = tag_by_id(&tags, active_tags[i]); 
		
		find_max(&tags[index_t], readers, r_index, max_id); 
		// printf("reader %d find reader %d as maximum  in tag %d \n", readers->at(r_index).id, max_id, active_tags[i]);		
		
		set_owner_tag(readers, r_index, active_tags[i], max_id);

		if (max_id != readers->at(r_index).id) { 
			//intersection_result = intersection(readers->at(r_index).neighbors_tags, 
			//			readers->at(reader_by_id(readers, active_tags[i]).neighbor_tags);	


			// TODO: 
			// instead of deactivating the tags, decrease the tagcount ! 
			//

			// get neigbhors tags of max_id 
			vector<int> max_id_neigbhors_tags = readers->at(reader_by_id(readers, max_id)).neighbors_tags; 
			intersection_result.clear();
			intersection_result = intersection(readers->at(r_index).neighbors_tags, max_id_neigbhors_tags);
			// decrease tag_cont; 
			readers->at(r_index).tag_count -= intersection_result.size();			
		}		
	}
	}

	// deactivating some tags in the same round
	for (int i = 0; i < to_deactivate_later.size(); i++) {
		deactivate_by_tag_id(readers, r_index, to_deactivate_later[i]);
	} 
			

}
*/ 

void find_max(tag* tag, vector<reader>* readers, int r_index , int& max_id){ 
	
	//printf("at %s, tag %d, reader %d \n", __FUNCTION__, tag->id, readers->at(r_index).id);
	
	vector<int> r_neighbors; 
	vector<int> r_neighbors_size; 

	int my_id = readers->at(r_index).id; 
	// int my_tag_count = readers->at(r_index).active_tags.size();	
	int my_tag_count = readers->at(r_index).tag_count; 
	// int my_tag_count = readers->at(r_index).to_be_written_tags.size(); 

	// bool d = (readers->at(r_index).id == 1 || readers->at(r_index).id == 4); 
	bool d = false; 
	if (d) printf("my_id : %d - my_tag_count: %d \n", my_id, my_tag_count);
	if (d) printf("my calculated tag_count: %d \n", readers->at(r_index).to_be_written_tags.size());	


	for (int i = 0; i < tag->contents.size(); i++) { 
		if (d) printf("checking content[i] = %d of tag %d \n", tag->contents[i].id, tag->id);
		if (tag->contents[i].id != my_id) { 
			r_neighbors.push_back(tag->contents[i].id); 
			r_neighbors_size.push_back(tag->contents[i].tags.size());
		}
	}


	if (r_neighbors.size() > 0) { 
		int max_index; 
		find_max_reader(r_neighbors, r_neighbors_size, max_index); 


		if (d) printf("comparing maximum reader (id:tc) (%d:%d) with mine (%d:%d) \n", r_neighbors[max_index], r_neighbors_size[max_index],
					my_id, my_tag_count);
 
		if (compare_id_tagcount(r_neighbors[max_index], r_neighbors_size[max_index], my_id, my_tag_count) < 0) { 
			max_id = my_id; 
		} else { 
			max_id = r_neighbors[max_index];
		}
	} else {
		if (d) printf("since no neighbors: maximum was found to be %d \n", my_id); 
		max_id = my_id;
	}



}


void find_max_reader(vector<int> id, vector<int> sizes , int& max_index){ 
	
	if (id.size() != sizes.size()) { 
		max_index = -1; 
		return; 
	}

	max_index = 0; 
	for (int i = 1; i < id.size(); i++) { 
		if (compare_id_tagcount(id[max_index], sizes[max_index], id[i], sizes[i]) < 0) {
			max_index = i;
		}
	}
	
	// printf("max index = %d \n", max_index);	
	
}



int compare_id_tagcount(int id1, int tag_count1, int id2, int tag_count2) { 
	
	// printf("comparing (%d, %d) to (%d, %d) \n", id1, tag_count1, id2, tag_count2);
	
	if (id1 == id2 && tag_count1 == tag_count2) { return 0; }

	if (tag_count1 > tag_count2 || ((tag_count1 == tag_count2) && (id1 > id2))) {  return 1; } 
	
	return -1;  
	
}

void set_owner_tag(vector<reader>* readers, int r_index, int tag_id, int owner) {

	//printf("at %s, with reader %d, and tag_id %d and owner %d \n", __FUNCTION__, readers->at(r_index).id, tag_id, owner);

	// first search for all neighbors_tags of reader r_index. 
	vector<int> n_tags = readers->at(r_index).neighbors_tags; 
	int f_index = -1; 

	for (int i = 0; i < n_tags.size(); i++) { 
		if (tag_id == n_tags[i]) { f_index = i; break; } 
	}
	
	if (f_index == -1) { 
		printf("error at %s: tag was not found ! \n", __FUNCTION__);
	}

	if (owner == -1) {printf("the owner of tag %d was set to be -1 \n", tag_id); abort(); }
	readers->at(r_index).neighbors_tags_owners[f_index] = owner;

	// There is an error herein
	/*
	if (owner == readers->at(r_index).id) { 
		printf("*** reader %d set tag %d as owned tag \n", readers->at(r_index).id, tag_id);
		readers->at(r_index).owned_tags.push_back(tag_id);
	}
	*/ 

}

void check_owner(tag* t, bool& owned, int& owner) { 

	if (!t) return; 
	// printf("at %s with tag %d \n", __FUNCTION__, t->id);
	
	owned = true; 	
	owner = -1; 
	/*
	printf("Printing contents of tag %d : ", t->id);
	for (int i = 0; i < t->contents.size(); i++) { 
		printf("(%d, %d) ", t->contents[i].id, t->contents[i].owner); 
	} printf("\n");
	*/ 

	if (t->contents.size() == 0) {
		printf("Warning at %s: tag %d has no contents \n", __FUNCTION__, t->id); 
		owner = -1; 
		owned = false; 
		return;  
	}

	if (t->contents[0].owner == -1) {
		// printf("owned == false, t->contents[0].owner == -1 = %d \n", t->contents[0].owner);
		owned = false; return; 

	}

	// TODO
	for (int i = 1; i < t->contents.size(); i++) { 
		if (t->contents[i-1].owner != t->contents[i].owner) {
			// printf("owned = false .. t->contents[i-1].owner != t->contents[i].owner (%d, %d) \n", 
			//		t->contents[i-1].owner, t->contents[i].owner) ; 
			owned = false; return; 
		}
	}

	for (int i = 0; i < t->contents.size(); i++) {
		// printf("i:%d, (id, owner):(%d, %d) \n", i, t->contents[i].id, t->contents[i].owner);  
		if (t->contents[i].owner == -1) { 
			// printf("owned = false.. t->contents[%d].owner = %d \n", i, t->contents[i].owner);
			owned = false; return; 
		}
	}

	owner = t->contents[0].owner; 
	

}

void deactivate(vector<reader>* readers, int r_index, vector<tag>* tags, int t_index) { 
	
	//printf("at %s with reader %d and tag %d \n", __FUNCTION__, readers->at(r_index).id, tags->at(t_index).id);

	vector<int> my_active_tags = readers->at(r_index).active_tags; 
	int f_index = -1; 
	for (int i = 0; i < my_active_tags.size(); i++) { 
		if (my_active_tags[i] == tags->at(t_index).id) { f_index = i; break; }
	}

	if (f_index != -1) {
	
		// delete f_index from readers->at(r_index).active_tags; 
		readers->at(r_index).active_tags.erase(readers->at(r_index).active_tags.begin() + f_index, 
													readers->at(r_index).active_tags.begin() + f_index + 1);
	}	
}

void deactivate_by_tag_id(vector<reader>* readers, int r_index, int tag_id) { 
	


	vector<int> my_active_tags = readers->at(r_index).active_tags; 
	int f_index = -1; 
	for (int i = 0; i < my_active_tags.size(); i++) { 
		if (my_active_tags[i] == tag_id) { f_index = i; break; }
	}
	
	if (f_index != -1) { 
		// delete f_index from readers->at(r_index).active_tags; 
		readers->at(r_index).active_tags.erase(readers->at(r_index).active_tags.begin() + f_index, 
													readers->at(r_index).active_tags.begin() + f_index + 1);
	}	
}



vector<int> intersection(vector<int> n_1, vector<int> n_2) { 

	vector<int>::iterator it; 

	int min_size = (n_1.size() > n_2.size()) ? n_2.size() : n_1.size();	
	vector<int> v(min_size, -1); 

	sort(n_1.begin(), n_1.begin() + n_1.size()); 
	sort(n_2.begin(), n_2.begin() + n_2.size());

	it = set_intersection(n_1.begin(), n_1.begin() + n_1.size(), n_2.begin(), n_2.begin() + n_2.size(), v.begin());
	
	int stop;
	for (stop = 0; stop < v.size(); stop++) { 
		if (v.at(stop) == -1) break;  
	}	
	v.erase(v.begin() + stop, v.begin() + v.size()) ;
	
	
	return v; 
	
	
}

void tag_is_set(vector<int>* nstags, int tag_id) { 



	if (! nstags) return; 
	
	int f_index = -1; 
	for (int i = 0 ; i < nstags->size(); i++) { 
		if (tag_id == nstags->at(i)) { 
			f_index = i; 
			break; 
		}
	}

	if (f_index == -1){ 
		
		// printf("Warning: in %s : tag %d was not found \n", __FUNCTION__, tag_id);
		return; 
		// abort(); 
	}

	printf("#### Tag %d is set ! ###### \n", tag_id);
	nstags->erase(nstags->begin() + f_index, nstags->begin() + f_index + 1) ;

}

void add_to_owned_tags(vector<reader>* readers, int r_index, int tag_id) { 
	//printf("@ %s : reader %d tag %d \n", __FUNCTION__, readers->at(r_index).id, tag_id);
	if (! is_in_vector(&readers->at(r_index).owned_tags, tag_id) ) { 
		readers->at(r_index).owned_tags.push_back(tag_id);
	}

}

bool remove_to_be_written_tags(vector<reader>* readers, int r_index, int tag_id) { 
 	
	//printf("at %s with reader %d and tag %d \n", __FUNCTION__, readers->at(r_index).id, tag_id);

	vector<int> tags = readers->at(r_index).to_be_written_tags; 
	int f_index = -1; 
	for (int i = 0; i < tags.size(); i++) { 
		if (tags[i] == tag_id) { f_index = i; break; }
	}
	
	if (f_index != -1) { 
		// delete f_index from readers->at(r_index).active_tags; 
		readers->at(r_index).to_be_written_tags.erase(readers->at(r_index).to_be_written_tags.begin() + f_index, 
													readers->at(r_index).to_be_written_tags.begin() + f_index + 1);
		return true; 
	}	else { 
		return false; 
	}

}


// TODO: find_max seems to introduce an error  -- Done 
// TODO: When we deactivate a tag, we dont read it contents ! this is wrnog ... we must read each tag that was active at the beginning of 
// the round ! ... DONE 





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

void extract_bipartite_graph(map<int, vector<int> >* graph, char* file_name) { 

	FILE* f; 
	char line[5000];
	vector<string> str_tokens;
	char buffer[500];
	int i = 0, line_counter = 0;	

	sprintf(buffer, "%s", file_name);
	printf("reading file_name : %s, buffer: %s \n", file_name, buffer);
	 
	
	f = fopen(buffer, "r"); 	
	if (!f) { 
		printf("error: file could not be open \n");
		return;
	}

	i = 0;
	line_counter = 0;
	vector<int> tag;
	char* cstr;
	while(getline(f, line, 1256) != EOF) {  
		
		str_tokens.clear();
		if (! tokenize(line, &str_tokens))
			printf("tokenize did not succeed \n"); 


		for (int i = 0; i < str_tokens.size(); i++ ) {

			
			cstr = new char [str_tokens[i].size()+1];
  			strcpy (cstr, str_tokens[i].c_str());
		
			tag.push_back(atoi(cstr));
			delete [] cstr; 

		}
		
		sort (tag.begin(), tag.end()); 
		graph->insert(pair<int, vector<int> >(line_counter, tag));
		line_counter ++; 
		tag.clear(); 
	}	

}
