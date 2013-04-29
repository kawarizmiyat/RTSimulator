package com.simulator; 

import java.io.PrintStream;
import java.util.ArrayList;



public class SimSystem  {
	// public FutureEventList future;	// Note that we used herein FutureEventList
	// instead of EventQueue.
	
	public EventQueue future;
	
	// for keeping statistics
	private final double END = 2000; // marks the end of the simulation period
	private final int numReaders = 3;
	private final int numTags = 4; 
	
	private ArrayList<RREReader> readersTable;
	private ArrayList<RRETag> tagsTable;
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
		
		run();
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
		readersTable = new ArrayList<RREReader>();
		for (int i = 0; i < n; i++) { 
			if (D) { 
				log.printf("Creating reader %d \n", i);
			}
			readersTable.add(new RREReader(this, i));
		}

	}

	public void setupTags(int n) { 
		tagsTable = new ArrayList<RRETag>();
		for (int i = 0; i < n; i++) { 
			tagsTable.add(new RRETag(i));
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
		Message m = new Message(-1, i, RREReader.MSG_INIT, "", 
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

	public RRETagContent readTag(Integer i) {
		
		return tagsTable.get(i).readTag();
		
	}


}
