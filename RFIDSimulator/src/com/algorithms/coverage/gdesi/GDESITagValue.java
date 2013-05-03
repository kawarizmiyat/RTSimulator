package com.algorithms.coverage.gdesi;

public class GDESITagValue implements Comparable<GDESITagValue> {



	protected int id,  pvSize; 
	
	public GDESITagValue(int pvSize, int id) {
		this.id = id; 
		this.pvSize = pvSize; 
	}

	@Override
	public int compareTo(GDESITagValue s) {
		
		if (this.pvSize > s.pvSize || (this.pvSize == s.pvSize && this.id > s.id)) { 
			return 1; 
		} 
		
		if (this.pvSize == s.pvSize && this.id == s.id) { 
			return 0;
		}
		
		else return -1; 
		
	}	
	
	public String toString() { 
		String s = "(ps: " + this.pvSize + " , " + id + " ).";
		return s; 
	}
	
}
