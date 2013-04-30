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
	protected void handleReceivedMessage(Message message) {

		if (message.msgType == SingleRoundReader.MSG_OVERWRITE) { 
			overWrite(message); 
		
		} else { 
			log.printf("unrecognized message at tag %d \n", this.id);
			System.exit(0);
		}
		
	}

	protected abstract void overWrite(Message msg);
	
/*	private void overwrite(Message message) {
		
		
	}
*/
	@Override
	public void handleEvent(Event e) {

		if (D) { 
			log.printf("Tag %d handling a new Event at %f \n", 
					this.id, e.time);
		}
		
		now = e.time; 
		switch (e.action) { 
		
		case EventType.MESSAGE: 
			handleReceivedMessage(e.message);
			break ;
		
		default: 
			System.out.printf("Only messages are allowed in handleEvent");
			System.exit(0);
			break; 
		}
		
	}

}
