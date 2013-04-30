package com.algorithms.coverage.leo;

import com.algorithms.coverage.Message;
import com.algorithms.coverage.OverWriteTag;

public class LeoTag  extends OverWriteTag {

	public LeoTag(int id) {
		super(id);
		tc = new LeoTagContent();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void overWrite(Message msg) {
		if (D) { 
			log.printf("At overwrite %d orig:(id:%d) \n", 
					this.id, 
					((LeoTagContent) tc).id);
		}		
		
		LeoWriteMessage mc = (LeoWriteMessage) msg.msgContent; 
		LeoTagContent temp = new LeoTagContent(mc.id);

		if (D) { 
			log.println("Actual tag content " + tc);
			log.println("to be written tcontent: " + temp);
		} 
		
		// The first overwrites. Everyone no ! 
		// Though be careful, this is a very simplistic 
		// MAC layer there ! 
		if (tc.id == -1) { 
			numOverWrites ++; 
			tc.id = temp.id; 
			if (D) { 
				log.printf("Switching the current value of tag %d to + " +
						"(%d) \n", 
						this.id, 
						((LeoTagContent) tc).id);
			}
		}
		
		
		
		if (D) { 
			log.println("New value of tag content is " + this.tc);
		} 

		
	}

	
	
}
