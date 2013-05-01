package com.algorithms.coverage.gde;

import com.algorithms.coverage.WriteMessage;

public class DeactivationMessage extends WriteMessage{

	public int id; 

	public DeactivationMessage(int id) {
		super();
		this.id = id; 
	}
	
	
	@Override
	public String toString() {
		return "DeactivationMessage [id=" + id + "]";
	}

	
	
	
}
