package com.algorithms.coverage.randomplus;

import com.algorithms.coverage.random.RandomWriteMessage;

public class MinMaxWriteMessage extends RandomWriteMessage {


	public boolean minMax; 


	public MinMaxWriteMessage(int id, double rand, boolean mm) {
		super(id, rand);
		this.minMax = mm; 
	}


	@Override
	public String toString() {
		return "MinMaxWriteMessage [id=" + id + ", rand=" + rand
				+ ", minMax=" + minMax + "]";
	}




}
