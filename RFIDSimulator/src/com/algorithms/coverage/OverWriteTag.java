package com.algorithms.coverage;

public abstract class OverWriteTag extends Tag {


	protected boolean D = true;
	
	public OverWriteTag(int id) {
		super(id);

	}

	@Override
	public void handleReceivedMessage(Message message) {

		if (message.msgType == SingleRoundReader.MSG_OVERWRITE) { 
			overWrite(message); 
		
		} else { 
			log.printf("unrecognized message at tag %d \n", this.id);
			System.exit(0);
		}
		
	}

	// abstract function. 
	protected abstract void overWrite(Message msg);
	


}
