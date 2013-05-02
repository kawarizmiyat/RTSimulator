package com.algorithms.coverage.gdesi;

import com.algorithms.coverage.Message;
import com.algorithms.coverage.Tag;

public class GDESITag extends Tag {

	public GDESITag(int id) {
		super(id);

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
		
		GDESIWriteMessage m = (GDESIWriteMessage) message.msgContent; 
		
		
		if ( ((GDESITagContent) tagContent).currentRound < m.round) { 
			((GDESITagContent) tagContent).clear();
		}

		((GDESITagContent) tagContent).add(m.id, m.pv, m.wv, m.round); 
		
	}

	



}



