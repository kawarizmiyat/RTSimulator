package com.algorithms.coverage;

import com.simulator.SimSystem;

public class RREReader extends SingleRoundReader {

	public RREReader(SimSystem sim, int id) {
		super(sim, id);
	}

	@Override
	protected WriteMessage getWriteMessage() {
		RREWriteMessage rreMsg = new RREWriteMessage(this.id, 
				this.numNeighborTags);
		return rreMsg;
	}


}
