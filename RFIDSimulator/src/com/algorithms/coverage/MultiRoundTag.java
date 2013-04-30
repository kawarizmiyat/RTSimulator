package com.algorithms.coverage;

import com.algorithms.coverage.random.RandomTagContent;
import com.algorithms.coverage.random.RandomWriteMessage;
import com.simulator.Event;
import com.simulator.EventType;

public class MultiRoundTag extends Tag {


	public TagContent tc;
	protected boolean D = true;
	
	public MultiRoundTag(int id) {
		super(id);
		tc = new MultiRoundTagContent();

	}

	public TagContent readTag() {
		this.numReads ++ ;
		return tc; 
	}
	
	@Override
	public void handleReceivedMessage(Message message) {

		if (message.msgType == MultiRoundReader.MSG_OVERWRITE) { 
			overWrite(message); 
		
		} else { 
			log.printf("unrecognized message at tag %d \n", this.id);
			System.exit(0);
		}
		
	}

	// abstract function  TODO: make it abstract 
	protected void overWrite(Message msg) { 
		if (D) { 
			log.printf("At overwrite %d orig:(%f,%d) \n", this.id, 
					((MultiRoundTagContent) tc).rand, 
					((MultiRoundTagContent) tc).id);
		}
		
		// It is ok to use RandomWriteMessage for the time been.
		// It would be preferred to add a new class 
		// MultiRoundWriteMessage. -- TODO:
		RandomWriteMessage mc = (RandomWriteMessage) msg.msgContent;
		
		MultiRoundTagContent temp = new MultiRoundTagContent(mc.id, mc.rand);
		
		
		if (D) { 
			log.println("Actual tag content " + tc);
			log.println("to be written tcontent: " + temp);
		} 
		
		if (((MultiRoundTagContent) tc).compareTo(temp) < 0) { 
			
			((MultiRoundTagContent) tc).id = temp.id; 
			((MultiRoundTagContent) tc).rand = temp.rand; 
			numOverWrites ++;
			
			if (D) { 
				log.printf("Switching the current value of tag %d to + " +
						"(%f,%d) \n", 
						this.id, 
						((MultiRoundTagContent) tc).rand, 
						((MultiRoundTagContent) tc).id);
			}
		}
		
		
		if (D) { 
			log.println("new value of tag content is " + this.tc);
		} 



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
