package com.simulator;

public class Message {

	public int senderId, receiverId; 
	public String msgType;
	public Object msgContent;
	

	
	
	public Message(int sid, int rid, String mt, Object mc) {
		this.senderId = sid; 
		this.receiverId = rid; 
		this.msgType = mt; 
		this.msgContent = mc; 
	}



	public String toString() { 
		String str = ""; 
		str += "s: " + senderId + " "; 
		str += "r: " + receiverId + " "; 
		str += "mt: " + msgType + " "; 
		str += "mc: " + msgContent + " ";
		
		return str;
	}
}
