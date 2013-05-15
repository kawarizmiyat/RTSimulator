package com.my.utilities;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class StatPair {

	
	public double std; 
	public double mean; 

	public StatPair() { 

	}

	public StatPair(StatPair a) {
		this.mean = a.mean;
		this.std = a.std; 
	}

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
		// return "statPair [mean=" + mean + ", std=" + std + "]";
		DecimalFormat df = new DecimalFormat("#.##");
		return df.format(mean) + "\t" + df.format(std); 
		
	}


}
