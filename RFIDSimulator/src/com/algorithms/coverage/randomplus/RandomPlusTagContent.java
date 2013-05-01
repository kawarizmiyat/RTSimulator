package com.algorithms.coverage.randomplus;




import com.algorithms.coverage.TagContent;

public class RandomPlusTagContent extends TagContent 
implements Comparable<RandomPlusTagContent>{

	public double rand; 

	public RandomPlusTagContent() { 
		this.id = -1; 
		this.rand = -1; 
	}

	public RandomPlusTagContent(int id, double r) { 
		this.id = id; 
		this.rand = r; 

	}



	@Override
	public int compareTo(RandomPlusTagContent o) {

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
		return "MultiRoundTag [id=" + id + ", rand=" + rand + "]";
	}



}


