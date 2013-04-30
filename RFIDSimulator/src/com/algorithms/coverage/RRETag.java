package com.algorithms.coverage;


public class RRETag extends OverWriteTag {

	public RRETag(int id) {
		super(id);
		tc = new RRETagContent();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void overWrite(Message msg) {
		if (D) { 
			log.printf("At overwrite %d orig:(%d,%d) \n", this.id, 
					((RRETagContent) tc).numTags, 
					((RRETagContent) tc).id);
		}
		
		RREWriteMessage mc = (RREWriteMessage) msg.msgContent; 
		RRETagContent temp = new RRETagContent(mc.id, mc.numTags);
		
		
		if (D) { 
			log.println("Actual tag content " + tc);
			log.println("to be written tcontent: " + temp);
		} 
		
		if (((RRETagContent) tc).compareTo(temp) < 0) { 
			
			((RRETagContent) tc).id = temp.id; 
			((RRETagContent) tc).numTags = temp.numTags; 
			numOverWrites ++;
			
			if (D) { 
				log.printf("Switching the current value of tag %d to + " +
						"(%d,%d) \n", this.id, 
						((RRETagContent) tc).numTags, 
						((RRETagContent) tc).id);
			}
		}
		
		
		if (D) { 
			log.println("new value of tag content is " + this.tc);
		} 


	}

}
