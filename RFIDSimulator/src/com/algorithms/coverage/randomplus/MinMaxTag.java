package com.algorithms.coverage.randomplus;

import com.algorithms.coverage.Message;
import com.algorithms.coverage.OverWriteTag;
import com.algorithms.coverage.random.RandomWriteMessage;

public class MinMaxTag extends OverWriteTag {

	public MinMaxTag(int id) {
		super(id);
		tagContent = new MinMaxTagContent();
	}

	@Override
	protected void overWrite(Message msg) {

		// It is ok to use RandomWriteMessage for the time been.
		// It would be preferred to add a new class 
		// MultiRoundWriteMessage. -- TODO:
		MinMaxWriteMessage mc = (MinMaxWriteMessage) msg.msgContent;
		
		MinMaxTagContent temp = new MinMaxTagContent(mc.id, mc.rand, mc.minMax);
		
		
		if (D) { 
			log.printf("tag %d at overwrite, orig value: (%f,%d), " +
					"to be written: (%f, %d) \n", this.id, 
					((MinMaxTagContent) tagContent).rand, 
					((MinMaxTagContent) tagContent).id, 
					temp.rand, temp.id);
		}

		
		if (((MinMaxTagContent) tagContent).compareTo(temp) < 0) { 
			
			((MinMaxTagContent) tagContent).id = temp.id; 
			((MinMaxTagContent) tagContent).rand = temp.rand; 
			numOverWrites ++;
			
			if (D) { 
				log.printf("*** tag %d had its value overwritten *** \n", this.id);
			}
		}
		
		


		
	}



}
