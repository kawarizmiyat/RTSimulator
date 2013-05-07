package com.test;

import java.util.ArrayList;

import com.filefunctions.GraphExtractor;
import com.simulator.SimResult;
import com.simulator.SimSystem;

public class Main  {
	public static void main(String [] args) {

		ArrayList<SimResult> results = new ArrayList<SimResult>();


		for (int k = 1; k < 2; k++ ) { 
			//String filename = "Files/region_graph_" + k + ".dat"; 
			String foldername = "/home/ahmed/Desktop/Thesis " +
					"Files/chapters/rfid/coverage/journal/" +
					"rfid_journal_experiments/exp_100_tags/";
			String filename = "result_" + k + ".out";
			System.out.println("opening " + foldername + filename );
			
			ArrayList< ArrayList<Integer> > g = 
					GraphExtractor.readFile(foldername+filename);


			SimSystem sim = new SimSystem();
			sim.setMaxIterations(6);
			sim.setRTGraph("gde", g);
			sim.run();
			
			SimResult result = sim.getResult();
			results.add(result);
			
		}


		for (int i = 0; i < results.size(); i++)
			System.out.println(results.get(i));
	
		
		
		
		ArrayList<Integer> allNumReaders, allNumOverWrites, 
			allRounds, allNonRedundant, allNumReads, allNumTags, 
			allNumOwnedTags; 
		
		allNumReaders = new ArrayList<Integer>();
		allNumOverWrites = new ArrayList<Integer>();
		allRounds = new ArrayList<Integer>();
		allNumTags = new ArrayList<Integer>();
		allNumOwnedTags = new ArrayList<Integer>();
		allNonRedundant = new ArrayList<Integer>();
		allNumReads = new ArrayList<Integer>();
		
		for (int i = 0; i < results.size(); i++ )  {
			SimResult r = results.get(i);
			allNumReaders.add(r.numReaders);
			allNonRedundant.add(r.nonRedundant);
			allRounds.add(r.rounds); 
			allNumOverWrites.add(r.numOverWrites); 
			allNumReads.add(r.numReads);
			allNumTags.add(r.numTags);
			allNumOwnedTags.add(r.numOwnedTags);
		

		}
		
		StatPair a = new StatPair();

		a.meanStd(allNumReaders); 
		a.print("allNumReaders");


		a.meanStd(allNumTags); 
		a.print("allNumTags");

		
		a.meanStd(allNumReads);
		a.print("allNumReads");
		
		a.meanStd(allNumOverWrites); 
		a.print("allNumOverWrites");		
		
		a.meanStd(allNonRedundant);
		a.print("allNonRedundant");
		
	
		
		a.meanStd(allRounds); 
		a.print("allRounds");
		

		for (int i = 0; i < results.size(); i++) { 
			print(results.get(i).readersPerRound);
		}
		
		
//		allNumReaders.add(r.numReaders);
//		allNonRedundant.add(r.nonRedundant);
//		allRounds.add(r.rounds); 
//		allNumOverWrites.add(r.numOverWrites); 
//		allNumReads.add(r.numReads);
//		allNumTags.add(r.numTags);
//		allNumOwnedTags.add(r.numOwnedTags);
		
		

	}

	








	private static void print(ArrayList<Integer> f) {
		for (int i = 0; i < f.size(); i++) { 
			System.out.printf("%d ", f.get(i)); 
		}
		System.out.println();
	}



	
	


}

class StatPair { 
	static double std; 
	static double mean; 
	
	public  void meanStd(ArrayList<Integer> list) { 
		double sum = 0; 
		for (int i = 0; i < list.size(); i++) 
			sum += list.get(i);
		mean = sum / (double) list.size(); 
		
		double sqrdSum = 0.0;
		for (int i = 0; i < list.size(); i++) { 
			sqrdSum += Math.pow((list.get(i) - mean), 2); 
		}
		
		double var = sqrdSum / (double) list.size(); 
		std = Math.sqrt(var);
	}

	public void print(String string) {
		System.out.println(string + " : "+ this);
		
	}

	@Override
	public String toString() {
		return "statPair [mean=" + mean + ", std=" + std + "]";
	}
	
	
}
