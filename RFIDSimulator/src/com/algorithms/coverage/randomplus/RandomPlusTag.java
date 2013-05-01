package com.algorithms.coverage.randomplus;

import com.algorithms.coverage.Message;
import com.algorithms.coverage.Tag;
import com.algorithms.coverage.TagContent;
import com.algorithms.coverage.random.RandomWriteMessage;
import com.simulator.Event;
import com.simulator.EventType;

public class RandomPlusTag extends Tag {


	public TagContent tc;
	protected boolean D = true;
	
	public RandomPlusTag(int id) {
		super(id);
		tc = new RandomPlusTagContent();

	}

	public TagContent readTag() {
		this.numReads ++ ;
		return tc; 
	}
	
	@Override
	public void handleReceivedMessage(Message message) {

		if (message.msgType == RandomPlusReader.MSG_OVERWRITE) { 
			overWrite(message); 
		
		} else { 
			log.printf("error at tag %d: unrecognized received message (t: %s) \n", 
					this.id, message.msgType);
			System.exit(0);
		}
		
	}

	// abstract function  TODO: make it abstract 
	protected void overWrite(Message msg) { 
		
		// It is ok to use RandomWriteMessage for the time been.
		// It would be preferred to add a new class 
		// MultiRoundWriteMessage. -- TODO:
		RandomWriteMessage mc = (RandomWriteMessage) msg.msgContent;
		
		RandomPlusTagContent temp = new RandomPlusTagContent(mc.id, mc.rand);
		
		
		if (D) { 
			log.printf("tag %d at overwrite, orig value: (%f,%d), " +
					"to be written: (%f, %d) \n", this.id, 
					((RandomPlusTagContent) tc).rand, 
					((RandomPlusTagContent) tc).id, 
					temp.rand, temp.id);
		}

		
		if (((RandomPlusTagContent) tc).compareTo(temp) < 0) { 
			
			((RandomPlusTagContent) tc).id = temp.id; 
			((RandomPlusTagContent) tc).rand = temp.rand; 
			numOverWrites ++;
			
			if (D) { 
				log.printf("*** tag %d had its value overwritten *** \n", this.id);
			}
		}
		
		


	}
	
	@Override
	public void handleEvent(Event e) {

		// There is no need to debug this. 
		// if (D) { 
		//	log.printf("tag %d handles a new event at %f \n", 
		//			this.id, e.time);
		// }
		
		now = e.time; 
		switch (e.action) { 
		
		case EventType.MESSAGE: 
			handleReceivedMessage(e.message);
			break ;
		
		default: 
			log.printf("error at tag %d: only messages are allowed in handleEvent");
			System.exit(0);
			break; 
		}
		
	}



}
