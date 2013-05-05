package com.simulator;

import java.util.ArrayList;

public class MyUtil {

	

	public  static ArrayList<Integer> interesect(ArrayList<Integer> f, ArrayList<Integer> s) { 
		ArrayList<Integer> res = new ArrayList<Integer>();
		
		int i = 0, j = 0; 
		while (i != f.size() && j != s.size()) { 
			

			
			if (f.get(i) < s.get(j)) {
				i ++;
			} else if (f.get(i) > s.get(j)) { 
				j ++;
			} else { 
				
				res.add(f.get(i)); 
				i ++;  j ++;
			}
		}
		
		
		return res; 
	}

	public static ArrayList<Integer> setDifference(ArrayList<Integer> f, ArrayList<Integer> s) {

		ArrayList<Integer> result = new ArrayList<Integer>(); 
		
		int i = 0, j = 0; 
		while (i != f.size() && j != s.size()) { 
			
			if (f.get(i) < s.get(j)) { 
				result.add(f.get(i)); i ++; 
			} else if (f.get(i) > s.get(j)) { 
				j ++; 
			} else { 
				i ++; j++;
			}
			
		}
		
		// 
		// f.clear();
		// f.addAll(result);
		return result;
		
	}
}
