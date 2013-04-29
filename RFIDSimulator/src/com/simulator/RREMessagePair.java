package com.simulator;

public class RREMessagePair {

	int id; 
	int numTags;
	
	
	public RREMessagePair(int id, int numTags) {
		super();
		this.id = id;
		this.numTags = numTags;
	}


	@Override
	public String toString() {
		return "RREMessagePair [id=" + id + ", numTags=" + numTags + "]";
	} 
	
	
	
}
