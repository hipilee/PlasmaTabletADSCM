package com.cylinder.www.env;

public class TimeInterval {
	
	public static TimeInterval timeInterval = new TimeInterval();
	
	private TimeInterval(){

	}
	
	public static TimeInterval getInstance(){
		return timeInterval;
	}
	
	public double getInterval() {
		long presentTime = System.currentTimeMillis();
		interval = (presentTime - startTime)/60000.0;
		return interval;
	}

	public void setStartTime(long startTime) {

        this.startTime = startTime;
	}

	private long startTime;
	private double interval;

}
