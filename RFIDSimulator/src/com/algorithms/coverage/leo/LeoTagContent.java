package com.algorithms.coverage.leo;

import com.algorithms.coverage.TagContent;



public class LeoTagContent extends TagContent implements
		Comparable<LeoTagContent> {

	public LeoTagContent(int id) {
		this.id = id;
	}

	public LeoTagContent() {
		this.id = -1;
	}

	@Override
	public int compareTo(LeoTagContent o) {
		if (o.id == -1) { 
			return 1; 
		} else if (o.id == this.id){ 
			return 0;
		} else { 
			return -1;
		}
		
		
	}

	@Override
	public String toString() {
		return "LeoTagContent [id=" + id + "]";
	}
	
	

}
