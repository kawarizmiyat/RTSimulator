package com.algorithms.coverage.leo;

import com.algorithms.coverage.SingleRoundReader;
import com.algorithms.coverage.WriteMessage;
import com.simulator.SimSystem;

public class LeoReader extends SingleRoundReader {

	public LeoReader(SimSystem sim, int id) {
		super(sim, id);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected WriteMessage getWriteMessage() {
		LeoWriteMessage msg = new LeoWriteMessage(this.id);
		return msg;
		
	}

}
