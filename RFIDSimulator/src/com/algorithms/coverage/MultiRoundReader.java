package com.algorithms.coverage;

import java.util.Random;

import com.algorithms.coverage.random.RandomWriteMessage;
import com.simulator.SimSystem;

public class MultiRoundReader extends Reader {

	public boolean active; 
	public int round; 
	public int maxIterations; 
	
	
	public double rand;
	
	private static final boolean D = true;
	
	protected static final String STAT_IDLE = "STAT_IDLE"; 
	protected static final String STAT_WRITE = "STAT_WRITE";
	protected static final String STAT_READ = "STAT_READ";
	protected static final String STAT_TERMINATE = "STAT_TERMINATE";
	
	protected static final String MSG_TIMER_WRITE = "MSG_TIMER_WRITE";
	protected static final String MSG_TIMER_READ = "MSG_TIMER_READ";
	protected static final String MSG_OVERWRITE = "MSG_OVERWRITE";
	
	public MultiRoundReader(SimSystem sim, int id, int maxIt) {
		super(sim, id);
		active = true;
		round = 0;
		
		maxIterations = maxIt; 
		changeStatus(MultiRoundReader.STAT_IDLE);
		
	}


	@Override
	protected void initProtocol() {
		
		startRound();
	}

	protected void startRound() {
		round ++;
		rand = new Random().nextDouble(); 
		
		if (D) { 
			log.printf("Starting round %d at Reader %d " +
					"with %f as rand \n", round, this.id, this.rand);
		}
		
		if (round > maxIterations) { 
			changeStatus(MultiRoundReader.STAT_TERMINATE); 
			return;
		}
		
		
		
		// start algorithm here: 
		// for every active neighbor tag: 
		// write content .. 
		// But wait ! -- we are implementing RANDOM+ here. 
		// Therefore, for every neighbor tag.
		
		// for each neighbor tag, 
		// send a message of type: overwrite.
		for (int i = 0; i < neighborsTags.size(); i++) { 
			
			
			// general use (create an abstract function generate 
			// write message). 
			// Now: we give an implementation of getWriteMessage(). When implementing another 
			// algorithm, we make it abstract in this case. 
			WriteMessage msg = getWriteMessage();
			
			Message m = new Message(this.id, neighborsTags.get(i), 
					MultiRoundReader.MSG_OVERWRITE, 
					msg, 
					Reader.myType, 
					't');
					
			sendMessage(m);
		}
		
		 scheduleTimer(2* this.msgDelay(), 
				MultiRoundReader.MSG_TIMER_WRITE); 
		 changeStatus(MultiRoundReader.STAT_WRITE);
	
		
	}


	private WriteMessage getWriteMessage() {
		RandomWriteMessage msg = new RandomWriteMessage(this.id, this.rand);
		return msg;
	}


	@Override
	protected void handleReceivedMessage(Message message) {
		if (this.id != message.receiverId) { 
			log.printf("Error: received message is not destined to" +
					"the correct destination (%d != %d) \n", 
					this.id, message.receiverId);
		}
		
		
		if (D) { 
			log.printf("Reader %d received message (t: %s) from (%d) \n", 
					this.id, message.msgType, message.senderId);
		}
		
		if (status == MultiRoundReader.STAT_IDLE) { 
			handleStatusIdle(message);
		
		} else if (status == MultiRoundReader.STAT_READ) { 
			handleStatusRead(message);
			
		} else if (status == MultiRoundReader.STAT_WRITE) { 
			handleStatusWrite(message);

		} else { 
			log.printf("Error: cannot receive message at state %s \n",
					status);
			System.exit(0);
		}
	}

	private void handleStatusRead(Message message) {
		
		
		if (message.msgType == MultiRoundReader.MSG_TIMER_READ) { 
			
			if (isRedundant()) { 
				changeStatus(MultiRoundReader.STAT_TERMINATE);
			} else { 
				startRound();
			}
			
		} else { 
			log.printf("Error: Message type %s is not accepted " +
					"at STATE_READ \n", message.msgType); 
			System.exit(0);
		}
		
		
	}


	private void handleStatusWrite(Message message) {
		
		// for each neighbor tag, 
		// read current content, if ccontent id is equal to 
		// my id, then become owner.
		
		// TODO: how to implement the read operation ?
		
		if (message.msgType == MultiRoundReader.MSG_TIMER_WRITE) { 
		
		for (int i = 0; i < neighborsTags.size(); i++) { 
			TagContent tc = (TagContent) this.sim.readTag(neighborsTags.get(i));
			
		
			if (tc.id == this.id) { 
				ownTag(neighborsTags.get(i));
			}
		}

		} else { 
			
			log.printf("Error at Reader %d: Message type %s " +
					"received from %d is not accepted " +
					"at STATE_WRITE \n", this.id, 
					message.msgType, message.senderId); 
			System.exit(0);
			
		}
		
		
		// TODO: 
		// update timer here. 
		// require a better understanding of how timer work.
		// One way of implementing this is by sending a dummy message 
		// to yourself and then do that. However, this is equivalent
		// to increasing the timer at TIMER_WAIT.

		/*
		updateTimer(now + 1);
		
		if (isRedundant()) { 
			changeStatus(MultiRoundReader.STAT_TERMINATE);
		} else { 
			// Here we should make some modifications ! 
			changeStatus(MultiRoundReader.STAT_READ);
		}

		*/ 
		
		// A better way to implement this is the following. 
		// change status to STAT_READ. schedule a timer to 
		// now + 1. 
		// In handleSateRead(). Check if redundant. 
		// If yes. Go to terminate. 
		// Otherwise. StartRound again. 
		 scheduleTimer(this.msgDelay(), 
					MultiRoundReader.MSG_TIMER_READ); 
			 changeStatus(MultiRoundReader.STAT_READ);
		
		
	}
	

	
	
	private boolean isRedundant() {
		return  (ownedTags.size() == 0);
	}


	private void handleStatusIdle(Message message) {
		
		
		if (message.msgType == Reader.MSG_INIT) { 
			
			initProtocol();
		}  else { 
			log.printf("Error: cannot receive an init message in IDLE");
			System.exit(0);
		}
		
	}




	@Override
	public boolean isValidStatus(String str) {
		return (str == MultiRoundReader.STAT_IDLE || 
				str == MultiRoundReader.STAT_WRITE ||
				str == MultiRoundReader.STAT_READ ||
				str == MultiRoundReader.STAT_TERMINATE); 
	}


	@Override
	public boolean isTerminatedStatus(String str) {
		return (status == MultiRoundReader.STAT_TERMINATE); 
	}





}
