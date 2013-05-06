package com.algorithms.coverage.randomplus;

import com.algorithms.coverage.TagContent;

public class MinMaxTagContent extends TagContent implements Comparable<MinMaxTagContent> {

	
	boolean minMax; 
	double rand; 
	
	public MinMaxTagContent() {
		id = -1; 
		rand = -1; 
		minMax = false; 
	}

	public MinMaxTagContent(int id, double rand, boolean d) { 
		this.id  = id; 
		this.rand = rand;
		this.minMax = d; 
	}
	
	@Override
	public int compareTo(MinMaxTagContent a) {
		if (a.minMax == MinMaxReader.MAX) { 
			return compare(a); 
		} else { 
			return -1 * compare(a);
		}
	}

	private int compare(MinMaxTagContent o) {

		int oid = o.id;
		double or = o.rand; 

		if (this.rand == or && this.id == oid) {
			return 0; 
		}

		if (this.rand > or) { return 1; }




		if (this.rand == or && 
				this.id > oid) {
			return 1; 
		}

		return -1;

	}
	
	

}
