package com.algorithms.coverage.gdesi;

import java.util.ArrayList;

import com.algorithms.coverage.WriteMessage;

public class GDESIWriteMessage extends WriteMessage {

	// The old version: 
	// There is a ArrayList<Integer> Pv. -- we removed this. 
	// the tag receiving the message can simply use id to access a global 
	// table that contains Pv. 
	public int id; 
	public int wv;
	public int round; 
	
	public GDESIWriteMessage(int id, int wv, int round) {
		this.id = id; 
		this.wv = wv;			// it may be weird to use wv, but see paper for more details.

		this.round = round;
		
		
	}

}
