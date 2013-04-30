package com.algorithms.coverage.random;

import com.algorithms.coverage.TagContent;

public class RandomTagContent extends TagContent 
implements Comparable<RandomTagContent>{

	public double rand; 

	public RandomTagContent() { 
		this.id = -1; 
		this.rand = -1; 
	}

	public RandomTagContent(int id, double r) { 
		this.id = id; 
		this.rand = r; 

	}



	@Override
	public int compareTo(RandomTagContent o) {

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

	@Override
	public String toString() {
		return "RandomTagContent [id=" + id + ", rand=" + rand + "]";
	}
	
	

}
