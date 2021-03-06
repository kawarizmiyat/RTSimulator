package com.simulator; 

import java.io.PrintStream;
import java.util.ArrayList;

import com.algorithms.coverage.Message;
import com.algorithms.coverage.Reader;
import com.algorithms.coverage.Tag;
import com.algorithms.coverage.TagContent;
import com.algorithms.coverage.drre.DRREReader;
import com.algorithms.coverage.drre.DRRETag;
import com.algorithms.coverage.gde.GDEReader;
import com.algorithms.coverage.gde.GDETag;
import com.algorithms.coverage.gde.LimitedGDEReader;
import com.algorithms.coverage.gdesi.GDESIReader;
import com.algorithms.coverage.gdesi.GDESITag;
import com.algorithms.coverage.leo.LeoReader;
import com.algorithms.coverage.leo.LeoTag;
import com.algorithms.coverage.random.RandomReader;
import com.algorithms.coverage.random.RandomTag;
import com.algorithms.coverage.randomplus.MinMaxReader;
import com.algorithms.coverage.randomplus.MinMaxTag;
import com.algorithms.coverage.randomplus.RandomPlusReader;
import com.algorithms.coverage.randomplus.RandomPlusTag;
import com.algorithms.coverage.randomplus.SimpleRandomPlusReader;
import com.algorithms.coverage.rre.RREReader;
import com.algorithms.coverage.rre.RRETag;



public class SimSystem  {
	// public FutureEventList future;	// Note that we used herein FutureEventList
	// instead of EventQueue.

	public EventQueue future;

	// for keeping statistics
	private final double END = 200; // marks the end of the simulation period

	
	private final int MAX_ITERATIONS = 2; 
	private int maxIterations;
	
	private int numReaders;
	private int numTags; 
	private boolean initReaders = false;

	private ArrayList<Reader> readersTable;
	private ArrayList<Tag> tagsTable;



	private static final boolean D = false; 
	private static final boolean badDebug = false;

	private static final int ROUNDS = 20;
	
	private final PrintStream log = System.out; 

	private SimResult simResult; 
	
	public SimSystem() { 


		setupSimulator(); 
		maxIterations = MAX_ITERATIONS;
		//test();


		/*
		setupReaders(numReaders);
		setupTags(numTags);

		setupNeighbors();
		for (int i = 0; i < numReaders; i++) {
			setInitiator(i);
		}
		*/ 

		
		
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

	@SuppressWarnings("unused")
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

		
		if (D) { 
			log.printf("reader %d is set to be initiator \n", i);
		} 

		Event e = new Event(); 
		e.time = 0; 
		e.action = EventType.MESSAGE; 
		Message m = new Message(-1, i, Reader.MSG_INIT, "", 
				'r', 'r');
		e.message = m;
		
	
		this.future.enter(e);
	}

	public void setupSimulator() { 
		// future = new FutureEventList();
		future = new EventQueue();
	}

	public void run(){
		System.out.println("**** simulation starts **** ");		


		while(true){

			if(0 == future.getNbEvents()) { 

				if (correctTermination()) { 
					log.printf("*** simulation terminated *** \n");
					analyzeResults();
				} else { 
					log.printf("error: some readers did not terminate \n");
				}

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

				
				if (badDebug) { 
					log.println("Next Event is of type message " +
							nextE.message.msgType); 
					log.printf("receivedId: %d \n", nextE.message.receiverId);
					log.printf("target type: %c \n", nextE.message.targetType);
				} 

				
				
				int handlerId = nextE.message.receiverId;


				if (nextE.message.targetType == 't')  {
					tagsTable.get(handlerId).handleEvent(nextE);
				} else if (nextE.message.targetType == 'r') { 
					readersTable.get(handlerId).handleEvent(nextE);
				}


			}


		}

	}


	private boolean correctTermination() {
		for (int i = 0; i < this.readersTable.size(); i++) { 
			if (! readersTable.get(i).isTerminated()) { 
				log.printf("at least Reader %d did not terminate \n",
						i);
				return false;
			}
		}
		return true;
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

		numTags = getTagsSize(g);
		numReaders = g.size();
		initiateReaders(numReaders, numTags, algorithm);

		for (int i = 0; i < g.size(); i++) { 

			for (int j = 0; j < g.get(i).size(); j++) { 
				readersTable.get(i).addTagNeighbor(g.get(i).get(j));
			}

		}
		
		for (int i = 0; i < readersTable.size(); i ++ ) { 
			setInitiator(i);
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
			
				
			} else if (algorithm.equals("randomPlus")) {
				
				for (int i = 0; i < readersSize; i++) { 
					
					readersTable.add(
							new SimpleRandomPlusReader(
									this, i, maxIterations)
							);
				}
				
				for (int i = 0; i < tagsSize; i++) { 
					tagsTable.add(new RandomPlusTag(i));
				}
				
			} else if (algorithm.equals("gde")) { 
				
				for (int i = 0; i < readersSize; i++ ) {
					readersTable.add(new GDEReader(this, i));
				}
				
				for (int i = 0; i < tagsSize; i++) { 
					tagsTable.add(new GDETag(i));
				}
				
			} else if (algorithm.equals("limitedGDE")) { 
				
				for (int i = 0; i < readersSize; i++) { 
					readersTable.add(new LimitedGDEReader(this, i, maxIterations)); 
				}
				
				for (int i = 0; i < tagsSize; i++) { 
					tagsTable.add(new GDETag(i));
				}
				
			} else if (algorithm.equals("gdeSi")) {
				
				for (int i = 0; i < readersSize; i++) { 
					readersTable.add(new GDESIReader(this, i)); 
				}
				
				for (int i = 0; i < tagsSize; i++) { 
					tagsTable.add(new GDESITag(i));
				}
				
				
			} else if (algorithm.equals("drre")) { 
				
				for (int i = 0; i < readersSize; i++) { 
					readersTable.add(new DRREReader(this, i)); 
				}
				
				for (int i = 0; i < tagsSize; i++) { 
					tagsTable.add(new DRRETag(i));
				}
				
				
			} else if (algorithm.equals("minMax")) { 
				
				for (int i = 0; i < readersSize; i++) { 
					readersTable.add(new MinMaxReader(this, i));
				}
				
				
				for (int i = 0; i < tagsSize; i++) { 
					tagsTable.add(new MinMaxTag(i));
				}
				
				
			} else { 
				System.out.printf("Error: cannot initiate, " +
						"algorithm %s is not recognized \n", algorithm);
			
				System.out.println("Accepted algorithms are: " +
						"rre, random, leo, randomPlus, dge, limitedGDE," +
						" gdeSi, drre, minMax");
				System.exit(0);
			}
			
			initReaders = true;
		}

	}



	public void setMaxIterations(int i) {
		this.maxIterations = i; 
	}


	private void analyzeResults() {

		simResult = new SimResult();
		
		log.printf("*** analyzing results *** \n");
		
		simResult.nonRedundantReaders = new ArrayList<Integer>();
		for (int i = 0; i < readersTable.size(); i++) { 
			if (readersTable.get(i).ownedTags.size() > 0) { 

				 
					log.printf("reader %d is not redundant: ",
						readersTable.get(i).id);
					for (int j = 0; j < readersTable.get(i).ownedTags.size(); j++) { 
						log.printf("%d ", readersTable.get(i).ownedTags.get(j));
					}
					log.printf("\n");
				
				simResult.nonRedundantReaders.add(readersTable.get(i).id);
				simResult.numOwnedTags += readersTable.get(i).ownedTags.size();
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

		
		simResult.numTags = tagsTable.size();
		simResult.numReaders = readersTable.size();
		simResult.nonRedundant = simResult.nonRedundantReaders.size();
		simResult.numOverWrites = overAllOverWrites; 
		simResult.numReads = overAllReads;

		// It should be working for all algorithms. 
		// We made a change in class Reader. We added round .
		if (roundAlgorithm()) { 
			
			// calculating the maximum round. 
			int maxRound = 0; 
			for (int i = 0; i < readersTable.size(); i++) { 
				int t = readersTable.get(i).round;
				if (t > maxRound) { 
					maxRound = t;
				}
			}
			
			simResult.rounds = maxRound;
		
			
			// calculating the number of readers per round.
			for (int i = 0; i < SimSystem.ROUNDS; i++) { 
				int temp = 0;
				for (int j = 0; j < readersTable.size(); j++) { 
					if (readersTable.get(j).round >= i) { 
						temp ++;
					}
				}
				simResult.readersPerRound.add(temp);
			}
			
			
		}

		
		
		
		
	}

	

	private boolean roundAlgorithm() {
		
		return true;
//		return (readersTable.get(0) instanceof GDEReader || 
//				readersTable.get(0) instanceof GDESIReader || 
//				readersTable.get(0) instanceof RandomPlusReader || 
//				readersTable.get(0) instanceof MinMaxReader );
		
	}



	public SimResult getResult() {
		return simResult;
	}

}


