package com.algorithms.coverage.gdesi;

import com.algorithms.coverage.Message;
import com.algorithms.coverage.Tag;

public class GDESITag extends Tag {

	public GDESITag(int id) {
		super(id);
		tagContent = new GDESITagContent();
	}

	@Override
	public void handleReceivedMessage(Message message) {

		if (message.msgType == GDESIReader.MSG_APPEND) { 
			append(message); 

		} else { 
			log.printf("unrecognized message at tag %d \n", this.id);
			System.exit(0);
		}

	}

	private void append(Message message) {
		
		
		// TODO: we should keep the largest two elements always. 
		// How to do that ? -- this will be useful for 
		// findMax function. 
		
		this.numOverWrites ++; 
		GDESIWriteMessage m = (GDESIWriteMessage) message.msgContent; 
		
		
		if ( ((GDESITagContent) tagContent).currentRound < m.round) { 
			((GDESITagContent) tagContent).clear();
		}

		((GDESITagContent) tagContent).add(m.id, m.wv, m.round); 
		
		if (D) { 
			log.printf("new tag %d content: ", this.id);
			log.print(tagContent); 
			log.printf("\n");
			
		}
		
	}

	



}



