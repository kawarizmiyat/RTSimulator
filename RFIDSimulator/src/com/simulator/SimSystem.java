package com.simulator; 

import java.io.PrintStream;
import java.util.ArrayList;

import com.algorithms.coverage.Message;
import com.algorithms.coverage.Reader;
import com.algorithms.coverage.SingleRoundReader;
import com.algorithms.coverage.OverWriteTag;
import com.algorithms.coverage.Tag;
import com.algorithms.coverage.TagContent;
import com.algorithms.coverage.leo.LeoReader;
import com.algorithms.coverage.leo.LeoTag;
import com.algorithms.coverage.random.RandomReader;
import com.algorithms.coverage.random.RandomTag;
import com.algorithms.coverage.rre.RREReader;
import com.algorithms.coverage.rre.RRETag;



public class SimSystem  {
	// public FutureEventList future;	// Note that we used herein FutureEventList
	// instead of EventQueue.

	public EventQueue future;

	// for keeping statistics
	private final double END = 2000; // marks the end of the simulation period

	private final int numReaders = 3;
	private final int numTags = 4; 
	private boolean initReaders = false;

	private ArrayList<Reader> readersTable;
	private ArrayList<Tag> tagsTable;
	private ArrayList<Integer> nonRedundantReaders;


	private static final boolean D = false; 
	private final PrintStream log = System.out; 

	public SimSystem() { 


		setupSimulator(); 
		//test();


		setupReaders(numReaders);
		setupTags(numTags);

		setupNeighbors();
		for (int i = 0; i < numReaders; i++) {
			setInitiator(i);
		}


	}



	@SuppressWarnings("unused")
	private void test() {

		for (int i = 0; i < 10; i++) { 
			Event e = new Event(); 
			e.time = 1; 

			this.future.enter(e); 
		}

		while (! this.future.isEmpty()) { 
			Event e = this.future.getNext();
			log.printf("(%d, %f, %f) \n", 
					e.id, e.time, e.randomBreaker);
		}


		System.exit(0);
	}

	public void setupReaders(int n) {
		readersTable = new ArrayList<Reader>();
		for (int i = 0; i < n; i++) { 
			if (D) { 
				log.printf("Creating reader %d \n", i);
			}
			readersTable.add(new LeoReader(this, i));
		}

	}

	public void setupTags(int n) { 
		tagsTable = new ArrayList<Tag>();
		for (int i = 0; i < n; i++) { 
			tagsTable.add(new LeoTag(i));
		}
	}

	private void setupNeighbors() {

		// TODO: 
		readersTable.get(0).addTagNeighbor(0); 
		readersTable.get(0).addTagNeighbor(1);

		// readersTable.get(1).addTagNeighbor(0);
		readersTable.get(1).addTagNeighbor(1);
		readersTable.get(1).addTagNeighbor(2);

		readersTable.get(2).addTagNeighbor(2);
		readersTable.get(2).addTagNeighbor(3);




	}

	private void setInitiator(int i) {

		/*
		if (D) { 
			log.printf("Reader %d is set to be initiator \n", i);
		}*/ 

		Event e = new Event(); 
		e.time = 0; 
		e.action = EventType.MESSAGE; 
		Message m = new Message(-1, i, SingleRoundReader.MSG_INIT, "", 
				'r', 'r');
		e.message = m;
		this.future.enter(e);
	}

	public void setupSimulator() { 
		// future = new FutureEventList();
		future = new EventQueue();
	}

	public void run(){
		System.out.println("simulation starts");		


		while(true){

			if(0 == future.getNbEvents()) { 
				log.printf("No more events \n");
				testTermination();
				analyzeResults();

				break;
			}

			Event nextE = future.getNext();


			if(nextE == null) {
				System.out.println("null pointer"); 
				break;
			}

			if(nextE.time > END) {
				System.out.println("END time = " + nextE.time); 
				break;
			}

			// only tags can receive messages: 
			if (nextE.action == EventType.MESSAGE) {

				/*
				if (D) { 
					log.println("Next Event is of type message " +
							nextE.message.msgType); 
					log.printf("receivedId: %d \n", nextE.message.receiverId);
					log.printf("target type: %c \n", nextE.message.targetType);
				}*/ 

				int handlerId = nextE.message.receiverId;


				if (nextE.message.targetType == 't')  {
					tagsTable.get(handlerId).handleEvent(nextE);
				} else if (nextE.message.targetType == 'r') { 
					readersTable.get(handlerId).handleEvent(nextE);
				}


			}


		}

	}

	private void analyzeResults() {
		nonRedundantReaders = new ArrayList<Integer>();
		for (int i = 0; i < readersTable.size(); i++) { 
			if (readersTable.get(i).ownedTags.size() > 0) { 

				// if (D) { 
				log.printf("Reader %d is not redundant \n",
						readersTable.get(i).id);
				//}
				nonRedundantReaders.add(readersTable.get(i).id);
			}
		}

		int overAllOverWrites = 0; 
		int overAllReads = 0; 
		for (int i = 0; i < tagsTable.size(); i++) { 
			overAllOverWrites  += tagsTable.get(i).numOverWrites;
			overAllReads += tagsTable.get(i).numReads;
		}
		log.printf("Number of over writes %d \n", overAllOverWrites);
		log.printf("Nuber of reads %d \n", overAllReads);



	}

	private void testTermination() {
		for (int i = 0; i < this.readersTable.size(); i++) { 
			if (! readersTable.get(i).isTerminated()) { 
				log.printf("At least Reader %d did not terminate \n",
						i);
			}
		}
	}

	// This functions works as a connector between 
	// readers and tags. -- This should exists in every 
	// SimSystem class. 
	// One solution to force the existence of this is to 
	// let SimSystem implements an interface that 
	// contains an abstract version of this function.
	public TagContent readTag(Integer i) {

		return tagsTable.get(i).readTag();

	}


	// This is a simple graph extractor. The graph 
	// contains all the reader-tag relationship. 
	// We assume that line i contains all the neighbor tags 
	// of reader i.
	public void setRTGraph(String algorithm, ArrayList<ArrayList<Integer>> g) {

		int tagsSize = getTagsSize(g);
		initiateReaders(g.size(), tagsSize, algorithm);

		for (int i = 0; i < g.size(); i++) { 

			for (int j = 0; j < g.get(i).size(); j++) { 
				readersTable.get(i).addTagNeighbor(g.get(i).get(j));
			}

		}

	}



	private int getTagsSize(ArrayList<ArrayList<Integer>> g) {
		// An easy way to implement this is to find the 
		// maximum id of tags. Here of course, we assume 
		// that the tags are serially identified from 0 to n - 1 . 
		
		int max = -1; 
		for (int i = 0; i < g.size(); i++) { 
			for (int j = 0; j < g.get(i).size(); j++) { 
				if (g.get(i).get(j) > max) { 
					max = g.get(i).get(j);
				}
			}
		}
		
		return max + 1 ;
	}



	public void initiateReaders(int readersSize, int tagsSize,
			String algorithm) {

		if (initReaders) { 
			return; 
		} else { 

			readersTable = new ArrayList<Reader>(); 
			tagsTable = new ArrayList<Tag>();

			if (algorithm.equals("rre")) { 

				for (int i = 0; i < readersSize; i++) { 
					readersTable.add(new RREReader(this, i));
				}
				
				for (int i = 0; i < tagsSize; i++) { 
					tagsTable.add(new RRETag(i));
				}

			} else if (algorithm.equals("leo")) { 

				for (int i = 0; i < readersSize; i++)  { 
					readersTable.add(new LeoReader(this, i));
				}
				
				for (int i = 0; i < tagsSize; i++) { 
					tagsTable.add(new LeoTag(i));
				}

			} else if (algorithm.equals("random")) { 
				for (int i = 0; i < readersSize; i++) { 
					readersTable.add(new RandomReader(this, i));
				}
				
				for (int i = 0; i < tagsSize; i++) { 
					tagsTable.add(new RandomTag(i));
				}
			
			} else { 
				System.out.printf("Error: cannot initiate, " +
						"algorithm %s is not recognized \n", algorithm);
			
				System.out.println("Accepted algorithms are: rre, random, leo");
				System.exit(0);
			}
			
			initReaders = true;
		}

	}

}


