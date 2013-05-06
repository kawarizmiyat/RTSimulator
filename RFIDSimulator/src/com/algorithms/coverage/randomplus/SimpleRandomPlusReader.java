package com.algorithms.coverage.randomplus;

import com.algorithms.coverage.WriteMessage;
import com.algorithms.coverage.random.RandomWriteMessage;
import com.simulator.SimSystem;

public class SimpleRandomPlusReader extends RandomPlusReader {


	public SimpleRandomPlusReader(SimSystem sim, int id, int maxIt) {
		super(sim, id, maxIt);
	}

	@Override
	protected WriteMessage getWriteMessage() {
		RandomWriteMessage msg = new RandomWriteMessage(this.id, this.rand);
		return msg;
	}

}
