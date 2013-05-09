package com.algorithms.coverage.drre;

import com.algorithms.coverage.Message;
import com.algorithms.coverage.Tag;
import com.my.utilities.MyUtil;

public class DRRETag extends Tag {

	
	private boolean D = true; 
	
	
	public DRRETag(int id) {
		super(id);
		tagContent = new DRRETagContent();
	}

	@Override
	public void handleReceivedMessage(Message message) {
		
		if (message.msgType == DRREReader.MSG_APPEND) { 
			append(message);
		} else if (message.msgType == DRREReader.MSG_OVERWRITE) { 
			overWrite(message);
		}

	}

	private void overWrite(Message message) {
		
		
		int senderId, senderWeight;
		DRREWriteMessage mc = (DRREWriteMessage) message.msgContent;
		DRRETagContent tc = (DRRETagContent) tagContent; 
		
		senderId = mc.id; 
		senderWeight = mc.weight; 
		
		if (senderWeight > tc.maxReaderWeight || 
				(senderWeight == tc.maxReaderWeight && 
				senderId > tc.maxReader) ) { 
			
			tc.maxReaderWeight = senderWeight; 
			tc.maxReader = senderId; 
		
				
			this.numOverWrites ++; 
			
			if (D) { 
				log.printf("*** tag %d updated maxReader to %d with weight %d \n", 
						this.id, tc.maxReader, tc.maxReaderWeight);
			}
			
		} 
		
	}

	private void append(Message message) {
		
		this.numOverWrites ++; 
		
		int r = ((DRREWriteMessage) message.msgContent).id;
		// also, we can simply enter the message sender id. 
		// i.e.:" 
		// int r = message.senderId; 
		
		((DRRETagContent) tagContent).readers.add(r);
		
		if (D) { 
			log.printf("tag %d added new reader %d to its readers - " +
					"the new readers: %s \n", 
					this.id, r, MyUtil.toString(((DRRETagContent) tagContent).readers));
		}
		
		// there should be no redundant data in readers ! 
		// there is no need to test it though. - it's just a waste of time. 	
		
		
	}

}
