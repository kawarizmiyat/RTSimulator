package com.algorithms.coverage.rre;

import com.algorithms.coverage.Message;
import com.algorithms.coverage.OverWriteTag;
import com.algorithms.coverage.randomplus.RandomPlusTagContent;


public class RRETag extends OverWriteTag {

	public RRETag(int id) {
		super(id);
		tc = new RRETagContent();

	}

	@Override
	protected void overWrite(Message msg) {

		
		RREWriteMessage mc = (RREWriteMessage) msg.msgContent; 
		RRETagContent temp = new RRETagContent(mc.id, mc.numTags);
		
		
		if (D) { 
			log.printf("tag %d at overwrite, orig value: (%d,%d), " +
					"to be written: (%d, %d) \n", this.id, 
					((RRETagContent) tc).numTags, 
					((RRETagContent) tc).id, 
					temp.numTags, temp.id);
		}

		
		if (((RRETagContent) tc).compareTo(temp) < 0) { 
			
			((RRETagContent) tc).id = temp.id; 
			((RRETagContent) tc).numTags = temp.numTags; 
			numOverWrites ++;
			
			if (D) { 
				log.printf("*** tag %d had its value overwritten *** \n", this.id);
			}
			
		}
		
		



	}

}
