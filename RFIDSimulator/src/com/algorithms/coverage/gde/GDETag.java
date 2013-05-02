package com.algorithms.coverage.gde;

import com.algorithms.coverage.Message;
import com.algorithms.coverage.Tag;
import com.algorithms.coverage.TagContent;

public class GDETag extends Tag {

	public boolean active;
	public GDETagContent tc;
	
	
	public GDETag(int id) {
		super(id);
		active = true;
		tc = new GDETagContent();
	}

	
	@Override
	public void handleReceivedMessage(Message message) {

		if (message.msgType == GDEReader.MSG_OVERWRITE) { 
			overWrite(message); 
			
		} else if (message.msgType == GDEReader.MSG_DEACTIVATE ) { 
			deactivate();
		
		} else { 
			log.printf("unrecognized message at tag %d \n", this.id);
			System.exit(0);
		}
		
	}



	private void overWrite(Message message) {

		GDEWriteMessage mc = (GDEWriteMessage) message.msgContent; 
		GDETagContent temp = new GDETagContent(mc.id, mc.numTags, mc.round);
		
		
		if (D) { 
			log.printf("tag %d at overwrite, orig value: (%d,%d, %d), " +
					"to be written: (%d, %d, %d) \n", this.id, 
					((GDETagContent) tc).numTags, 
					((GDETagContent) tc).id, 
					((GDETagContent) tc).round, 
					temp.numTags, temp.id, temp.round);
		}

		
		if (((GDETagContent) tc).compareTo(temp) < 0) { 
			
			((GDETagContent) tc).id = temp.id; 
			((GDETagContent) tc).numTags = temp.numTags; 
			((GDETagContent) tc).round = temp.round;
			
			numOverWrites ++;
			
			if (D) { 
				log.printf("*** tag %d had its value overwritten *** \n", this.id);
			}
			
		}
		

		
	}


	private void deactivate() {
		
		if (D) { 
			log.printf("**** tag %d is deactivated ****** \n", this.id);
		}
		
		active = false;
		tc.active = false;
		return;
	}


	@Override
	public TagContent readTag() {
		this.numReads ++ ;
		return tc; 
	}

}
