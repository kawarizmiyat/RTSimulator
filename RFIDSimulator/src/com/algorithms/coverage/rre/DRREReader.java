package com.algorithms.coverage.rre;

import com.algorithms.coverage.SingleRoundReader;
import com.algorithms.coverage.WriteMessage;
import com.simulator.SimSystem;

public class DRREReader  extends SingleRoundReader {

	public DRREReader(SimSystem sim, int id) {
		super(sim, id);
	}

	// The only difference between RRE and DRRE is that 
	// RRE use number of neigbhor tags, whereas DRRE uses the 
	// the number of neighbor readers. 
	// Therefore, the only change in getWriteMessage();
	@Override
	protected WriteMessage getWriteMessage() {
		RREWriteMessage rreMsg = new RREWriteMessage(this.id, 
				this.numNeighborReaders);
		return rreMsg;
	}

}
