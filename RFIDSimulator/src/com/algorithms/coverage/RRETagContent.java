package com.algorithms.coverage;

public class RRETagContent implements Comparable<RRETagContent>  {

	
	public int id; 
	public int numTags;
	
	
	public RRETagContent() { 
		id = -1; 
		numTags = -1;
	}
	
	public RRETagContent(int id, int numTags) {
		super();
		this.id = id;
		this.numTags = numTags;
	}




	@Override
	public String toString() {
		return "RRETagContent [id=" + id + ", numTags=" + numTags + "]";
	}




	@Override
	public int compareTo(RRETagContent o) {

		int oid = o.id;
		int onumTags = o.numTags; 
		
		if (this.numTags == onumTags && this.id == oid) {
			return 0; 
		}
		
		if (this.numTags > onumTags) { return 1; }
		

		
		// We may remove this statement below. In such case, 
		// the first to write is the first to own (if all values are 
		// equal). This is a non-determinstic method though. 
		// We add the following line to compare lexicographically 
		// (numTags, id). We may test it in both ways. 
		if (true) {
			if (this.numTags == onumTags && 
				this.id > oid) {return 1; }
		}
		
		return -1;
		
	}
	
	
	
	
}
