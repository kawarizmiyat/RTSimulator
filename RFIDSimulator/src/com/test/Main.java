package com.test;

import java.util.ArrayList;

import com.filefunctions.GraphExtractor;
import com.simulator.SimSystem;

public class Main  {
	public static void main(String [] args) {
		
		// new SimSystem();
		//System.out.println("program terminates");

		ArrayList< ArrayList<Integer> > g = 
				GraphExtractor.readFile("test.txt");
		
		
		SimSystem sim = new SimSystem();
		sim.setRTGraph("gde", g);
		sim.run();
		
	}
}

