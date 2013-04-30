package com.algorithms.coverage;

import com.simulator.Event;
import com.simulator.EventType;
import com.simulator.Message;

public class RRETag extends Tag {

	public RRETagContent tc;
	protected boolean D = true;
	
	public RRETag(int id) {
		super(id);
		tc = new RRETagContent();
	}

	public RRETagContent readTag() {
		this.numReads ++ ;
		return tc; 
	}
	
	@Override
	protected void handleReceivedMessage(Message message) {

		if (message.msgType == RREReader.MSG_OVERWRITE) { 
			overwrite(message); 
		
		} else { 
			log.printf("unrecognized message at tag %d \n", this.id);
			System.exit(0);
		}
		
	}

	private void overwrite(Message message) {
		
		if (D) { 
			log.printf("At overwrite %d orig:(%d,%d) \n", this.id, 
					tc.numTags, tc.id);
		}
		
		RREMessagePair mc = (RREMessagePair) message.msgContent; 
		RRETagContent temp = new RRETagContent(mc.id, mc.numTags);
		
		/*
		if (D) { 
			log.println("Actual tag content " + tc);
			log.println("to be written tcontent: " + temp);
		}*/ 
		
		if (this.tc.compareTo(temp) < 0) { 
			this.tc.id = temp.id; 
			this.tc.numTags = temp.numTags; 
			numOverWrites ++;
			
			if (D) { 
				log.printf("Switching the current value of tag %d to + " +
						"(%d,%d) \n", this.id, this.tc.numTags, this.tc.id);
			}
		}
		
		/*
		if (D) { 
			log.println("new value of tag content is " + this.tc);
		}*/ 
		
	}

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
