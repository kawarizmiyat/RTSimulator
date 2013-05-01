package com.algorithms.coverage;

import java.io.PrintStream;
import java.util.ArrayList;

import com.simulator.Event;
import com.simulator.EventType;

public abstract class Tag {

	public int id, numReads, numOverWrites;
	public ArrayList<Integer> neighborsReaders;
	public final static char myType = 't';
	
	protected double now;
	protected PrintStream log = System.out; 
	protected boolean D = true;
	
	public Tag(int id) { 

		this.id = id; 
		neighborsReaders = new ArrayList<Integer>();
		numReads = 0; 
		numOverWrites = 0;
	}
	
	public void addReaderNeighbor(int i) {
		neighborsReaders.add(i);
	}
	
	public abstract void handleReceivedMessage(Message message);
	public abstract TagContent readTag();


	public void handleEvent(Event e) {

		if (D) { 
			log.printf("tag %d handling a new Event at %f \n", 
					this.id, e.time);
		}
		
		now = e.time; 
		switch (e.action) { 
		
		case EventType.MESSAGE: 
			handleReceivedMessage(e.message);
			break ;
		
		default: 
			System.out.printf("Only messages are allowed in handleEvent");
			System.exit(0);
			break; 
		}
		
	}
	
	
}
