package com.algorithms.coverage.gde;

import com.algorithms.coverage.TagContent;

public class GDETagContent extends TagContent implements Comparable<GDETagContent> {

	public boolean active;
	public int numTags; 
	public int round; 

	public GDETagContent(int id, int numTags, int round) {
		this.id = id;
		this.numTags = numTags;
		this.active = true;
		this.round = round;
	}

	public GDETagContent() {
		this.id = -1;
		this.active = true;
		this.numTags = -1;
		this.round = 0;
	}
	
	
	// We don't need this constructor by the way. 
	public GDETagContent(int id, int numTags, boolean active) { 
		this.id = id; 
		this.numTags = numTags;
		this.active = active;
		
	}

	@Override
	public int compareTo(GDETagContent o) {

		int oid = o.id;
		int onumTags = o.numTags; 
		int oround = o.round;
		
		if (this.round == oround && this.numTags == onumTags && this.id == oid) {
			return 0; 
		}
		
		if (this.round > oround) { return 1; }
		
		if (this.round == oround && this.numTags > onumTags) { return 1; }
		

		
		// We may remove this statement below. In such case, 
		// the first to write is the first to own (if all values are 
		// equal). This is a non-determinstic method though. 
		// We add the following line to compare lexicographically 
		// (numTags, id). We may test it in both ways. 
		if (true) {
			if (this.numTags == onumTags &&  this.round == oround &&
				this.id > oid) {return 1; }
		}
		
		return -1;
		
	}

	@Override
	public String toString() {
		return "RRETagContent [id=" + id + ", numTags=" + numTags + "]";
	}
	

	
}
