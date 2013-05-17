package com.algorithms.coverage.gdesi;

import java.util.ArrayList;

public class GlobalViewerGDESI {

	public ArrayList< ArrayList<Integer> > PVS; 
	
	public GlobalViewerGDESI(int numReaders) {
		PVS = new ArrayList< ArrayList<Integer> >(); 
		for (int i = 0; i < numReaders; i++) { 
			PVS.add(new ArrayList<Integer>());
		}
	}
	
}
