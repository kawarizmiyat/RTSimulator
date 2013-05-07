package com.algorithms.coverage.randomplus;


import com.algorithms.coverage.WriteMessage;
import com.simulator.SimSystem;

public class MinMaxReader extends RandomPlusReader  {

	protected static final boolean MAX = false;
	protected static final boolean MIN = true; 

	public MinMaxReader(SimSystem sim, int id) {
		super(sim, id, 2);
		// 2: for two iterations. 
	}


	protected WriteMessage getWriteMessage() {
		MinMaxWriteMessage msg = new MinMaxWriteMessage(this.id, this.rand, getMinOrMax(this.round));
		return msg;
	}

	protected boolean getMinOrMax(int round) {
		if (round == 1) { 
			return MinMaxReader.MAX; 
		} else { 
			return MinMaxReader.MIN; 
		}
	}
	
	
}
