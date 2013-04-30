package com.algorithms.coverage.rre;

import com.algorithms.coverage.SingleRoundReader;
import com.algorithms.coverage.WriteMessage;
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
