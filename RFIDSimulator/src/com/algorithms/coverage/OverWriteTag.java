package com.algorithms.coverage;

import com.simulator.Event;
import com.simulator.EventType;

public abstract class OverWriteTag extends Tag {

	public TagContent tc;
	protected boolean D = true;
	
	public OverWriteTag(int id) {
		super(id);

	}

	public TagContent readTag() {
		this.numReads ++ ;
		return tc; 
	}
	
	@Override
	public void handleReceivedMessage(Message message) {

		if (message.msgType == SingleRoundReader.MSG_OVERWRITE) { 
			overWrite(message); 
		
		} else { 
			log.printf("unrecognized message at tag %d \n", this.id);
			System.exit(0);
		}
		
	}

	// abstract function. 
	protected abstract void overWrite(Message msg);
	


}
