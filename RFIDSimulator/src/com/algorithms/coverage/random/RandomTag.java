package com.algorithms.coverage.random;

import com.algorithms.coverage.Message;
import com.algorithms.coverage.OverWriteTag;

public class RandomTag extends OverWriteTag {

	public RandomTag(int id) {
		super(id);
		tagContent = new RandomTagContent();
	}

	@Override
	protected void overWrite(Message msg) {

		
		RandomWriteMessage mc = (RandomWriteMessage) msg.msgContent; 
		RandomTagContent temp = new RandomTagContent(mc.id, mc.rand);
		
		
		if (D) { 
			log.printf("tag %d at overwrite, orig value: (%f,%d), " +
					"to be written: (%f, %d) \n", this.id, 
					((RandomTagContent) tagContent).rand, 
					((RandomTagContent) tagContent).id, 
					temp.rand, temp.id);
		}
		
		
		if (((RandomTagContent) tagContent).compareTo(temp) < 0) { 
			
			((RandomTagContent) tagContent).id = temp.id; 
			((RandomTagContent) tagContent).rand = temp.rand; 
			numOverWrites ++;
			
			if (D) { 
				log.printf("*** tag %d had its value overwritten *** \n", this.id);
			}
			
		}
		

	}

}
