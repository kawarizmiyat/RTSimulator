package com.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import com.filefunctions.GraphExtractor;
import com.my.utilities.MyUtil;
import com.simulator.SimResult;
import com.simulator.SimSystem;

public class Main  {
	public static void main(String [] args) {


		// test(); 


		boolean debug = true; 
		ArrayList<SimResult> results = new ArrayList<SimResult>();


		for (int k = 1; k <= 100; k++ ) { 
			// String foldername = "Files/";
			// String filename = "scen_" + k ; 

			String foldername = "/home/ahmed/Desktop/Thesis " +
					"Files/chapters/rfid/coverage/journal/" +
					"rfid_journal_experiments/exp_100_tags/";
			String filename = "result_" + k + ".out";


			System.out.println("opening " + foldername + filename );

			ArrayList< ArrayList<Integer> > g = 
					GraphExtractor.readFile(foldername+filename);


			SimSystem sim = new SimSystem();
			sim.setMaxIterations(3);
			sim.setRTGraph("random", g);


			sim.run(); 


			SimResult result = sim.getResult();
			results.add(result);

		}

		if (debug) {

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


			// for (int i = 0; i < results.size(); i++) { 
			// 	print(results.get(i).readersPerRound);
			// }

		}



	}










	private static void test() {


		Integer[] sourceArr = {3,4,5,11,19};
		Integer[] destArr = {10, 11};

		ArrayList<Integer> source = new ArrayList<Integer>(Arrays.asList(sourceArr));
		ArrayList<Integer> dest = new ArrayList<Integer>(Arrays.asList(destArr));

		// System.out.println("source " + MyUtil.toString(source));
		// System.out.println("dest " + MyUtil.toString(dest));

		addUnique(dest, source);
		System.out.println("dest: " + MyUtil.toString(dest));


		System.exit(0);
	}







	private static void addUnique(ArrayList<Integer> dest,
			ArrayList<Integer> source) {


		//// 		Method: 2:

		// System.out.println("From: source " + MyUtil.toString(source));
		// System.out.println("To: dest " + MyUtil.toString(dest));

		ArrayList<Integer> result = new ArrayList<Integer>();
		int j = 0, i = 0; 
		while (j < source.size()) { 

			if (i == dest.size()) { 
				// System.out.printf("i : dest.size() \n");
				copy(source, j, result);
				break;
			}

			// System.out.printf("comp: source(j): %d - dest(i): %d \n",
			//		source.get(j), dest.get(i));

			if (source.get(j) < dest.get(i)) {
				// for sure, source.get(j) is not in dest. 

				// System.out.printf("adding %d to result", source.get(j));

				result.add(source.get(j)); 
				j ++; 

			} else if (source.get(j) > dest.get(i)) { 
				// No decision can be made.
				i ++; 

			} else { 
				// source.get(j) exists in dest.. both indices are advanced.
				i ++; j ++; 
			}

		}

		// System.out.println("dest: " + MyUtil.toString(dest));
		// System.out.println("result: " + MyUtil.toString(result));

		for (int k = 0; k < result.size(); k++) { 
			dest.add(result.get(k));
		}

		Collections.sort(dest);
		// System.out.println("dest: " + MyUtil.toString(dest));
	}






	private static void copy(ArrayList<Integer> s, int j,
			ArrayList<Integer> result) {

		for (int k = j; k < s.size(); k++) 
			result.add(s.get(k));

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
