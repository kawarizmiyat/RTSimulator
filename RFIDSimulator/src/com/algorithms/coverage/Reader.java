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
	
	// Variables needed to any Event handler in this simulation
	protected double now;
	protected PrintStream log = System.out; 
	private boolean D = true;
	
	public String status; 

	public static final String MSG_INIT = "init";
	
	
	public ArrayList<Integer> ownedTags;
	
	public Reader(SimSystem sim, int id) { 
		this.sim = sim; 
		this.id = id; 
		neighborsTags = new ArrayList<Integer>();
		neighborsReaders = new ArrayList<Integer>();
		numNeighborTags= 0;
	}
	
	public abstract void handleEvent(Event e);
	protected abstract void initProtocol() ;
	protected abstract void handleReceivedMessage(Message message);
	public abstract boolean isTerminated();
	protected abstract void changeStatus(String str);
	
	
	protected  void updateTimer(double d) { 
		if (d < now) { 
			log.printf("Error (updateTimer) at Reader %d: schduler error, event " +
					"in the past !", this.id);
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

		if (msg.sourceType == 'r') { return "Reader"; }
		else if (msg.sourceType == 't') { return "Tag"; }
		else { 
			log.printf(
					"Error: sourceType can be either r or t but " +
					"found %c \n", msg.sourceType);
			System.exit(0);
		}
		return null;
	}

	private String getDebugTargetString(Message msg) {
		if (msg.targetType == 'r') { return "Reader"; }
		else if (msg.targetType == 't') { return "Tag"; }
		else { 
			log.printf(
					"Error: sourceType can be either r or t but " +
					"found %c \n", msg.targetType);
			System.exit(0);
		}
		return null;
	}

	// there should be a better implementation to this function
	protected void scheduleTimer(double d, String timerType) {
		
		
		if (D) { 
			log.printf(
					"%d schedules a timer for %f time slots \n", 
					this.id, d);
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
	}

	public void addTagNeighbor(int i) { 
		neighborsTags.add(i);
		numNeighborTags ++; 
	}
	
}
