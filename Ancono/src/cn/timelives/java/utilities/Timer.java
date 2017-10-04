package cn.timelives.java.utilities;

import java.util.concurrent.TimeUnit;

public class Timer {
	private final TimeUnit tu;
	private long start=-1;
	private long end =-1;
	private boolean started = false;
	public static final TimeUnit DEFAULT_UNIT = TimeUnit.NANOSECONDS;
	public Timer(TimeUnit tu){
		this.tu = tu;
	}
	public Timer(){
		this.tu = DEFAULT_UNIT;
	}
	
	public void start(){
		if(started)
			throw new IllegalStateException("Already started");
		start = System.nanoTime();
		started =true;
	}
	public long end(){
		if(!started)
			throw new IllegalStateException("Haven't been started");
		end = System.nanoTime();
		started =false;
		return tu.convert(end-start, DEFAULT_UNIT);
	}
	public long getCostTime(){
		if(started)
			throw new IllegalStateException("counting");
		return tu.convert(end-start, DEFAULT_UNIT);
	}
}
