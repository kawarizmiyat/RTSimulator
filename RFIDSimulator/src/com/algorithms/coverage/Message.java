package com.algorithms.coverage;

public class Message {

	public int senderId, receiverId; 
	public String msgType;
	public Object msgContent;
	public char targetType, sourceType; 

	
	
	public Message(int sid, int rid, String mt, Object mc, 
			char sourceType, char targetType) {
		this.senderId = sid; 
		this.receiverId = rid; 
		this.msgType = mt; 
		this.msgContent = mc; 
		this.targetType = targetType;
		this.sourceType = sourceType;
	}



	public String toString() { 
		String str = ""; 
		str += "s: " + senderId + " "; 
		str += "r: " + receiverId + " "; 
		str += "mt: " + msgType + " "; 
		str += "mc: " + msgContent + " ";
		str += "st: " + sourceType + " ";
		str += "tt: " + targetType + ".";
		
		return str;
	}
}
