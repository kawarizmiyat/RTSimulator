package com.test;

import java.util.ArrayList;

import com.filefunctions.GraphExtractor;
import com.simulator.SimSystem;

public class Main  {
	public static void main(String [] args) {
		
		// new SimSystem();
		//System.out.println("program terminates");

		ArrayList< ArrayList<Integer> > g = 
				GraphExtractor.readFile("scen_5");
		
		
		SimSystem sim = new SimSystem();
		sim.setMaxIterations(3);
		sim.setRTGraph("gdeSi", g);
		sim.run();
		
//		ArrayList<Integer> f = g.get(0); 
//		ArrayList<Integer> s = g.get(1);
//		ArrayList<Integer> i = MyUtil.interesect(f,s);
//		ArrayList<Integer> fms = MyUtil.setDifference(f, s);
//		ArrayList<Integer> smf = MyUtil.setDifference(s, f);
//	
//		print(f); 
//		print(s);
//		print(i);
//		print(fms);
//		print(smf);
			
	}
	
	

	private static void print(ArrayList<Integer> f) {
		for (int i = 0; i < f.size(); i++) { 
			System.out.printf("%d ", f.get(i)); 
		}
		System.out.println();
	}



	
	
}

