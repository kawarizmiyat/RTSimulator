package com.algorithms.coverage.randomplus;

import com.algorithms.coverage.Message;
import com.algorithms.coverage.OverWriteTag;
import com.algorithms.coverage.TagContent;
import com.algorithms.coverage.random.RandomWriteMessage;



public class RandomPlusTag extends OverWriteTag {


	// public TagContent tc;
	protected boolean D = true;
	
	public RandomPlusTag(int id) {
		super(id);
		tagContent = new RandomPlusTagContent();
	}

	protected void overWrite(Message msg) { 
		
		// It is ok to use RandomWriteMessage for the time been.
		// It would be preferred to add a new class 
		// MultiRoundWriteMessage. -- TODO:
		RandomWriteMessage mc = (RandomWriteMessage) msg.msgContent;
		
		RandomPlusTagContent temp = new RandomPlusTagContent(mc.id, mc.rand);
		
		
		if (D) { 
			log.printf("tag %d at overwrite, orig value: (%f,%d), " +
					"to be written: (%f, %d) \n", this.id, 
					((RandomPlusTagContent) tagContent).rand, 
					((RandomPlusTagContent) tagContent).id, 
					temp.rand, temp.id);
		}

		
		if (((RandomPlusTagContent) tagContent).compareTo(temp) < 0) { 
			
			((RandomPlusTagContent) tagContent).id = temp.id; 
			((RandomPlusTagContent) tagContent).rand = temp.rand; 
			numOverWrites ++;
			
			if (D) { 
				log.printf("*** tag %d had its value overwritten *** \n", this.id);
			}
		}
		
		


	}

}
