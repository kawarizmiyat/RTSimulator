package com.algorithms.coverage.random;

import com.algorithms.coverage.Message;
import com.algorithms.coverage.OverWriteTag;
import com.algorithms.coverage.rre.RRETagContent;
import com.algorithms.coverage.rre.RREWriteMessage;

public class RandomTag extends OverWriteTag {

	public RandomTag(int id) {
		super(id);
		tc = new RandomTagContent();
	}

	@Override
	protected void overWrite(Message msg) {
		if (D) { 
			log.printf("At overwrite %d orig:(%f,%d) \n", this.id, 
					((RandomTagContent) tc).rand, 
					((RandomTagContent) tc).id);
		}
		
		RandomWriteMessage mc = (RandomWriteMessage) msg.msgContent; 
		RandomTagContent temp = new RandomTagContent(mc.id, mc.rand);
		
		
		if (D) { 
			log.println("Actual tag content " + tc);
			log.println("to be written tcontent: " + temp);
		} 
		
		if (((RandomTagContent) tc).compareTo(temp) < 0) { 
			
			((RandomTagContent) tc).id = temp.id; 
			((RandomTagContent) tc).rand = temp.rand; 
			numOverWrites ++;
			
			if (D) { 
				log.printf("Switching the current value of tag %d to + " +
						"(%f,%d) \n", 
						this.id, 
						((RandomTagContent) tc).rand, 
						((RandomTagContent) tc).id);
			}
		}
		
		
		if (D) { 
			log.println("new value of tag content is " + this.tc);
		} 


	}

}
