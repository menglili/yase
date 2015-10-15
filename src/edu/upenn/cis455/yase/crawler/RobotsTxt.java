package edu.upenn.cis455.YASE.crawler;

import java.util.HashSet;

public class RobotsTxt {
	private Long delay;
	private HashSet<String> disAllowHashSet;
	public RobotsTxt(){
		disAllowHashSet = new HashSet<String>();
	}
	
	public void addDisAllow(String disAllow){
		disAllowHashSet.add(disAllow);
	}
	
	public HashSet<String> getDisAllowSet(){
		return disAllowHashSet;
	}
	
	public void addDelay(int delay){
		long l = delay*1000;
		this.delay = l;
		
	}
	
	public Long getDelay(){
		return delay;
	}
	

}
