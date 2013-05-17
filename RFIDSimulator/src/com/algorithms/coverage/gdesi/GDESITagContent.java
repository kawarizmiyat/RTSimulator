package com.algorithms.coverage.gdesi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.algorithms.coverage.TagContent;

public class GDESITagContent extends TagContent {

	// We should create a private class here: 
	// public ArrayList<GDESITagStruct> table; 
	public HashMap<Integer, GDESITagStruct> table; 
	public int currentRound; 
	private int maxId, maxIdPvSize; 
	private static final boolean D = false; 
	
	
	public GDESITagContent() { 
		currentRound = 0;
		table = new HashMap<Integer, GDESITagStruct>();
	}
	

	public void clear() { 
		maxId = -1; 
		maxIdPvSize = -1; 
		table.clear();
	}


	public void add(int id, int wv, int round) {
		
		if (round < currentRound) {
			log.printf("error: adding a GDESITagStruct with a low (outdated) round ");
			System.exit(0);
		}
		
		GDESITagStruct t = new GDESITagStruct(id,  wv, round);
		table.put(id, t); 
		
		
//		// TODO: test me please ! 
//		if (maxIdPvSize < pv.size() || 
//				(maxIdPvSize == pv.size() && id > maxId) ) { 
//			maxId = id; 
//			maxIdPvSize = pv.size();
//			
//			//
//			//if (D) { 
//			//	log.printf("** update maximum id at the current tag" +
//			//			" to %d \n", maxId);
//			//}
//		}
		
		currentRound = round;
		
	}


	public String toString() { 
		String s = "";
		for (Map.Entry<Integer, GDESITagStruct> entry : this.table.entrySet()) { 
			s += entry.getKey() + " " + entry.getValue() + " \n";
		}
		return s; 
	}
	
	
}

class GDESITagStruct { 
	int id; 

	int wv; 
	int round; 
	
	public GDESITagStruct() { 
		id = -1; 
		wv = -1; 
		round = 0;
	}

	public GDESITagStruct(int id, int wv, int round) {
		this.id = id; 
		this.wv = wv; 
		this.round = round; 
		
	}
	
	public String toString() { 
		String s = "id: " + id + " , pvs: { "; 
		s += "}, wv: " + wv + " r: " + round + " "; 
		return s;
	}
	
}
