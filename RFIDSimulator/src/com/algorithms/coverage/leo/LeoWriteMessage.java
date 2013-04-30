package com.algorithms.coverage.leo;

import com.algorithms.coverage.WriteMessage;

public class LeoWriteMessage extends WriteMessage {

	public int id;
	
	public LeoWriteMessage(int id) {
		super();
		this.id = id;
	}

	@Override
	public String toString() {
		return "LeoWriteMessage [id=" + id + "]";
	}

	
	

}
