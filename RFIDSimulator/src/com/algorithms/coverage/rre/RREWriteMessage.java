package com.algorithms.coverage.rre;

import com.algorithms.coverage.WriteMessage;

public class RREWriteMessage extends WriteMessage {

	public int id; 
	public int numTags;
	
	
	public RREWriteMessage(int id, int numTags) {
		super();
		this.id = id;
		this.numTags = numTags;
	}


	@Override
	public String toString() {
		return "RREMessagePair [id=" + id + ", numTags=" + numTags + "]";
	} 
	
	
	
}
