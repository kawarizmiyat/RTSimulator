package com.algorithms.coverage.randomplus;

import java.util.Random;

import com.algorithms.coverage.Message;
import com.algorithms.coverage.Reader;
import com.algorithms.coverage.Tag;
import com.algorithms.coverage.TagContent;
import com.algorithms.coverage.WriteMessage;
import com.simulator.SimSystem;

public abstract class RandomPlusReader extends Reader  {

	public boolean active; 
	public int maxIterations; 
	
	
	public double rand;
	
	private static final boolean D = true;
	
	protected static final String STAT_IDLE = "STAT_IDLE"; 
	protected static final String STAT_WRITE = "STAT_WRITE";
	protected static final String STAT_READ = "STAT_READ";
	protected static final String STAT_TERMINATE = "STAT_TERMINATE";
	
	protected static final String MSG_TIMER_WRITE = "MSG_TIMER_WRITE";
	protected static final String MSG_TIMER_READ = "MSG_TIMER_READ";
	public static final String MSG_OVERWRITE = "MSG_OVERWRITE";
	
	public RandomPlusReader(SimSystem sim, int id, int maxIt) {
		super(sim, id);
		active = true;
		round = 0;
		
		maxIterations = maxIt; 
		changeStatus(RandomPlusReader.STAT_IDLE);
		
	}


	@Override
	protected void initProtocol() {
		
		startRound();
	}

	protected void startRound() {
		round ++;
		rand = new Random().nextDouble(); 
		
		
		
		if (round > maxIterations) { 
			changeStatus(RandomPlusReader.STAT_TERMINATE); 
			return;
		}
		
		if (D) { 
			log.printf("*** reader %d (rand:%f) starting round %d ***\n",  
					this.id, this.rand, round); 
		}		
		
		// start algorithm here: 
		// for every active neighbor tag: 
		// write content .. 
		// But wait ! -- we are implementing RANDOM+ here. 
		// Therefore, for every neighbor tag.
		
		ownedTags.clear();
		
		// for each neighbor tag, 
		// send a message of type: overwrite.
		for (int i = 0; i < neighborsTags.size(); i++) { 
			
			
			// general use (create an abstract function generate 
			// write message). 
			// Now: we give an implementation of getWriteMessage(). When implementing another 
			// algorithm, we make it abstract in this case. 
			WriteMessage msg = getWriteMessage();
			
			Message m = new Message(this.id, neighborsTags.get(i), 
					RandomPlusReader.MSG_OVERWRITE, 
					msg, 
					Reader.myType, 
					Tag.myType);
					
			sendMessage(m);
		}
		
		 scheduleTimer(2* this.msgDelay(), 
				RandomPlusReader.MSG_TIMER_WRITE); 
		 changeStatus(RandomPlusReader.STAT_WRITE);
	
		
	}


	protected abstract WriteMessage getWriteMessage();
	 // {
//		RandomWriteMessage msg = new RandomWriteMessage(this.id, this.rand);
//		return msg;
//	}


	@Override
	protected void handleReceivedMessage(Message message) {
		if (this.id != message.receiverId) { 
			log.printf("error at %d: reader %d received message is not destined to" +
					"the correct destination (%d != %d) \n", 
					this.id, this.id, message.receiverId);
		}
		
		
		if (D) { 
			
			if (message.receiverId == message.senderId) { 
				log.printf("reader %d wakes up with timer %s \n", 
						this.id, 
						message.msgType);
			} else if (message.senderId == -1 && message.msgType == Reader.MSG_INIT ) {
				
				log.printf("reader %d starts algorithm \n", this.id);
				
			} else {
			
				log.printf("reader %d received message (t: %s) from (%d) \n", 
					this.id, message.msgType, message.senderId);
			}
		}
		
		if (status == RandomPlusReader.STAT_IDLE) { 
			handleStatusIdle(message);
		
		} else if (status == RandomPlusReader.STAT_READ) { 
			handleStatusRead(message);
			
		} else if (status == RandomPlusReader.STAT_WRITE) { 
			handleStatusWrite(message);

		} else { 
			log.printf("error at reader %d: cannot receive message at state %s \n",
					this.id, status);
			System.exit(0);
		}
	}

	private void handleStatusRead(Message message) {
		
		
		if (message.msgType == RandomPlusReader.MSG_TIMER_READ) { 
			
			if (isRedundant()) { 
				if (D) { 
					log.printf("*** reader %d is found redundant and will terminate \n",
							this.id);
				}
				changeStatus(RandomPlusReader.STAT_TERMINATE);
			} else { 
				startRound();
			}
			
		} else { 
			log.printf("error at reader %d: message type %s is not accepted " +
					"at STATE_READ \n", this.id, message.msgType); 
			System.exit(0);
		}
		
		
	}


	private void handleStatusWrite(Message message) {
		
		// for each neighbor tag, 
		// read current content, if ccontent id is equal to 
		// my id, then become owner.
		
		// TODO: how to implement the read operation ?
		
		if (message.msgType == RandomPlusReader.MSG_TIMER_WRITE) { 
		
		for (int i = 0; i < neighborsTags.size(); i++) { 
			TagContent tc = (TagContent) this.sim.readTag(neighborsTags.get(i));
			
		
			if (tc.id == this.id) { 
				ownTag(neighborsTags.get(i));
			}
		}
		
		// A better way to implement this is the following. 
		// change status to STAT_READ. schedule a timer to 
		// now + 1. 
		// In handleSateRead(). Check if redundant. 
		// If yes. Go to terminate. 
		// Otherwise. StartRound again. 
		 scheduleTimer(this.msgDelay(), 
					RandomPlusReader.MSG_TIMER_READ); 
			 changeStatus(RandomPlusReader.STAT_READ);

		} else { 
			
			log.printf("error at reader %d: message type (%s) (rid:%d) " +
					"cannot be received " +
					"at STATE_WRITE \n", this.id, message.receiverId, 
					message.msgType); 
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
		

		
		
	}
	

	
	
	private boolean isRedundant() {
		return  (ownedTags.size() == 0);
	}


	private void handleStatusIdle(Message message) {
		
		
		if (message.msgType == Reader.MSG_INIT) { 
			
			initProtocol();
		}  else { 
			log.printf("error at reader %d: cannot receive message type (%s) in %s", 
					message.msgType, status);
			System.exit(0);
		}
		
	}




	@Override
	public boolean isValidStatus(String str) {
		return (str == RandomPlusReader.STAT_IDLE || 
				str == RandomPlusReader.STAT_WRITE ||
				str == RandomPlusReader.STAT_READ ||
				str == RandomPlusReader.STAT_TERMINATE); 
	}


	@Override
	public boolean isTerminatedStatus(String str) {
		return (status == RandomPlusReader.STAT_TERMINATE); 
	}




	

}
