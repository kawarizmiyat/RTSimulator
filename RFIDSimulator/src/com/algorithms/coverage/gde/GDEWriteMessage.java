package com.algorithms.coverage.gde;

import com.algorithms.coverage.WriteMessage;

public class GDEWriteMessage extends WriteMessage {


	public int id; 
	public int numTags; 
	public int round; 

	public GDEWriteMessage(int id, int numTags, int round) {
		this.id = id; 
		this.numTags = numTags;
		this.round = round;
	}

	@Override
	public String toString() {
		return "GDEWriteMessage [id=" + id + ", numTags=" + numTags
				+ ", round=" + round + "]";
	}
	
	
	
	
}
