package com.algorithms.coverage.drre;

import java.util.ArrayList;

import com.algorithms.coverage.TagContent;

public class DRRETagContent extends TagContent {
	
	public ArrayList<Integer> readers; 
	public int maxReader, maxReaderWeight; 
	
	public DRRETagContent() { 
		readers = new ArrayList<Integer>();
		maxReader = -1; 
		maxReaderWeight = -1; 
	}

	
	
}
