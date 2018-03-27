package cn.timelives.java.utilities;

import java.util.Random;

public class MyRandom {
	private static Random rd = new Random();
	
	private MyRandom(){
		rd = new Random();
	}
	
	
	public static <T> T ranObj(T[] array){
		int bound = array.length;
		int pos = rd.nextInt(bound);
		return array[pos];
	}
	
	
	public static void reset(){
		rd = new Random();
	}
	
	public static void reset(long seed){
		rd = new Random(seed);
	}
}
