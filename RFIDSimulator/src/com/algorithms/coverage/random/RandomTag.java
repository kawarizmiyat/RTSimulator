package com.algorithms.coverage.random;

import com.algorithms.coverage.Message;
import com.algorithms.coverage.OverWriteTag;
import com.algorithms.coverage.randomplus.RandomPlusTagContent;
import com.algorithms.coverage.rre.RRETagContent;
import com.algorithms.coverage.rre.RREWriteMessage;

public class RandomTag extends OverWriteTag {

	public RandomTag(int id) {
		super(id);
		tc = new RandomTagContent();
	}

	@Override
	protected void overWrite(Message msg) {

		
		RandomWriteMessage mc = (RandomWriteMessage) msg.msgContent; 
		RandomTagContent temp = new RandomTagContent(mc.id, mc.rand);
		
		
		if (D) { 
			log.printf("tag %d at overwrite, orig value: (%f,%d), " +
					"to be written: (%f, %d) \n", this.id, 
					((RandomTagContent) tc).rand, 
					((RandomTagContent) tc).id, 
					temp.rand, temp.id);
		}
		
		
		if (((RandomTagContent) tc).compareTo(temp) < 0) { 
			
			((RandomTagContent) tc).id = temp.id; 
			((RandomTagContent) tc).rand = temp.rand; 
			numOverWrites ++;
			
			if (D) { 
				log.printf("*** tag %d had its value overwritten *** \n", this.id);
			}
			
		}
		

	}

}
