package com.algorithms.coverage.gdesi;

import java.util.ArrayList;

import com.algorithms.coverage.WriteMessage;

public class GDESIWriteMessage extends WriteMessage {

	public int id; 
	public ArrayList<Integer> pv; 
	public int wv;
	public int round; 
	
	public GDESIWriteMessage(int id, ArrayList<Integer> pv, 
			int wv, int round) {
		this.id = id; 
		this.wv = wv;			// it may be weird to use wv, but see paper for more details.

		this.round = round;
		
		this.pv = new ArrayList<Integer>();
		for (int i = 0; i < pv.size(); i++) { 
				this.pv.add(pv.get(i));
		}
		
	}

}
