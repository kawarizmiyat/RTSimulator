package com.test;

import java.util.ArrayList;
import java.util.Random;

public class Tester1 {

	
	public Tester1() { 
		int MAX = 10; 
		ArrayList<Integer> list = new ArrayList<Integer>(); 
		
		for (int i = 0 ; i < 5; i++) { 
			list.add(new Random().nextInt(MAX));
		}
		
		int id = new Random().nextInt(MAX); 
		
		print(list);
		removeTag(list, id); 
		System.out.printf("id: %d \n", id);
		print(list);
	}
	
	
	private void print(ArrayList<Integer> list) {
		for (int i = 0; i < list.size(); i++) { 
			System.out.printf("%d ", list.get(i));
		}
	}


	private boolean removeTag(ArrayList<Integer> list, Integer id) {
		if (list.size() == 0) { 
			
			// log.printf("warning: the list in removeTag is empty ! \n");
			// System.exit(0);
			return false;
		}
		
		int index = -1; 
		for (int i = 0; i < list.size(); i++ ) { 
			if (list.get(i) == id) { 
				index = i;
				break;
			}
		}
		
		if (index == -1) { 
			// log.printf("warning: we cannot find %d in the list ! \n", id);
			return false;
		}
		
		list.remove(index);
		return true;

	}

}
