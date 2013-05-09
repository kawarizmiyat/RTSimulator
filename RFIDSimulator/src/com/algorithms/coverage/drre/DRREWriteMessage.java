package com.algorithms.coverage.drre;

import com.algorithms.coverage.WriteMessage;

public class DRREWriteMessage extends WriteMessage {

	public int id, weight; 
	
	public DRREWriteMessage(int id, int weight) {
		this.id = id; 
		this.weight = weight; 
	}

	@Override
	public String toString() {
		return "DRREWriteMessage [id=" + id + ", weight=" + weight + "]";
	}

	
	
	
}
