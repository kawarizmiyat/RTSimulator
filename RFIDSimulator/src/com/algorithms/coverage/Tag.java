package com.algorithms.coverage;

import java.io.PrintStream;
import java.util.ArrayList;

import com.simulator.Event;
import com.simulator.Message;

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
	
	protected abstract void handleReceivedMessage(Message message);
	protected abstract void handleEvent(Event e);
	


	
}
