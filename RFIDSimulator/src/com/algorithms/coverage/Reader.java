 package com.algorithms.coverage; 

import java.io.PrintStream;
import java.util.ArrayList;

import com.simulator.Event;
import com.simulator.EventType;
import com.simulator.SimSystem;

public abstract class Reader {
	
	public SimSystem sim; 
	public int id; 
	public ArrayList<Integer> neighborsTags;
	public ArrayList<Integer> neighborsReaders;
	public final static char myType = 'r';
	public int numNeighborTags; 
	public int numNeighborReaders;
	
	// Variables needed to any Event handler in this simulation
	protected double now;
	protected PrintStream log = System.out; 
	private boolean D = false;
	
	public String status; 

	public static final String MSG_INIT = "MSG_INIT";
	
	
	public ArrayList<Integer> ownedTags;
	
	public Reader(SimSystem sim, int id) { 
		this.sim = sim; 
		this.id = id; 
		neighborsTags = new ArrayList<Integer>();
		neighborsReaders = new ArrayList<Integer>();
		numNeighborTags= 0;
		numNeighborReaders = 0;
		ownedTags = new ArrayList<Integer>();
	}
	
	
	// Note: this was moved from SingleRoundReader to Reader.
	// This is done in order for this function to be used in 
	// other type of Readers (e.g. MultiRoundReader).
	public void handleEvent(Event e) { 
		
		if (D) { 
			log.printf("reader %d handling event at time %f \n", this.id, e.time);
		}
		
		if (e.time < now) { 
			log.printf("error at reader %d: event in past at handleEvent ",
					this.id);
		}
		
		updateTimer(e.time);
		switch (e.action) { 
		
		case EventType.MESSAGE: 
			if (D) { 
				log.printf("reader %d is goint to handle received message \n", this.id);
			}
			handleReceivedMessage(e.message);
			break ;
		
		default: 
			
			log.printf("error at reader %d: Only messages are allowed in handleEvent", 
					this.id);
			System.exit(0);
			break; 
		}
			
		
	
	}
	
	
	protected abstract void initProtocol() ;
	protected abstract void handleReceivedMessage(Message message);
	public abstract boolean isValidStatus(String str);
	public abstract boolean isTerminatedStatus(String str) ;
	
	protected  void updateTimer(double d) { 
		if (d < now) { 
			log.printf("error at reader %d: event in past at updateTimer ",
					this.id);
		}
		now = d;
	}
	
	protected void sendMessage(Message msg) { 
		
		if (D) { 
			
			String source = getDebugSourceString(msg);
			String target = getDebugTargetString(msg);
			
			log.printf("%s %d sends message to %s %d \n", 
					source, msg.senderId, 
					target, msg.receiverId);
		}
		
		Event e = new Event(); 
		e.action = EventType.MESSAGE; 
		e.message = msg; 
		e.time = now + msgDelay();
		
		sim.future.enter(e);
	}
	

	private String getDebugSourceString(Message msg) {

		if (msg.sourceType == 'r') { return "reader"; }
		else if (msg.sourceType == 't') { return "tag"; }
		else { 
			log.printf(
					"error at reader %d: sourceType can be either r or t but " +
					"found %c \n", this.id, msg.sourceType);
			System.exit(0);
		}
		return null;
	}

	private String getDebugTargetString(Message msg) {
		if (msg.targetType == 'r') { return "reader"; }
		else if (msg.targetType == 't') { return "tag"; }
		else { 
			log.printf(
					"error at raeder %d: sourceType can be either r or t but " +
					"found %c \n", this.id, msg.targetType);
			System.exit(0);
		}
		return null;
	}

	// there should be a better implementation to this function
	protected void scheduleTimer(double d, String timerType) {
		
		
		if (D) { 
			log.printf(
					"reader %d schedules a timer of type %s for %f time slots \n", 
					this.id, timerType, d);
		}
		
		Message m  = new Message(this.id, this.id, 
				timerType, null, 'r', 'r');
		
		Event e = new Event(); 
		e.action = EventType.MESSAGE; 
		e.time = now + d; 
		e.message = m;
		sim.future.enter(e);
		
	}

	
	protected double msgDelay() { 
		return 1.0;
	}
	
	public void addReaderNeighbor(int i) {
		neighborsReaders.add(i);
		numNeighborReaders ++;
	}

	public void addTagNeighbor(int i) { 
		neighborsTags.add(i);
		numNeighborTags ++; 
	}
	
	protected void changeStatus(String str) {
		
		if (D) { 
			log.printf("at change status to %s \n", str);
		}
		
		if (isValidStatus(str)) { 
			
			if (D) { 
				log.printf("reader %d changed status from: %s " +
						" to %s \n", this.id, this.status, str);
			}
			
			
			status = str; 
			if (isTerminatedStatus(str)) { 
				log.printf("reader %d terminated at %f \n",
						this.id, this.now);
			}
		} else { 
			log.printf("error at reader %d: " +
					"status %s is not a valid state \n", this.id, str);
			System.exit(0);
		}
 	}

	public boolean isTerminated() {
		return isTerminatedStatus(status);
	}
	
	public void ownTag(int i) {
		
		if (D) { 
			log.printf("reader %d owns tag %d \n", 
					this.id, i);
		}
		ownedTags.add(i);
	}
	
}
