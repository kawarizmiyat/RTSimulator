package com.algorithms.coverage.leo;

import com.algorithms.coverage.Message;
import com.algorithms.coverage.OverWriteTag;

public class LeoTag  extends OverWriteTag {

	public LeoTag(int id) {
		super(id);
		tagContent = new LeoTagContent();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void overWrite(Message msg) {
	
		
		LeoWriteMessage mc = (LeoWriteMessage) msg.msgContent; 
		LeoTagContent temp = new LeoTagContent(mc.id);

		
		if (D) { 
			log.printf("tag %d at overwrite, orig value: (%d), " +
					"to be written: (%d) \n", this.id, 
					((LeoTagContent) tagContent).id, 
					temp.id);
		}
		
		// The first overwrites. Everyone no ! 
		// Though be careful, this is a very simplistic 
		// MAC layer there ! 
		if (tagContent.id == -1) { 
			numOverWrites ++; 
			tagContent.id = temp.id; 

			if (D) { 
				log.printf("*** tag %d had its value overwritten *** \n", this.id);
			}
		}
		
		

		
	}

	
	
}
