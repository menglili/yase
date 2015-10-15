package edu.upenn.cis455.YASE.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

public class RobotsTxtManager {
	private LinkedHashMap<String, RobotsTxt> robotsTxtMap;
	

	public RobotsTxtManager( final int size) {
		
		robotsTxtMap = new LinkedHashMap<String, RobotsTxt>(size + 1, .75F,
				true) {
			private static final long serialVersionUID = 1L;

			public boolean removeEldestEntry(Map.Entry<String, RobotsTxt> eldest) {
				return size() > size;
			}
		};
	}

	public synchronized boolean isCached(String hostString){
		
		return robotsTxtMap.containsKey(hostString);
	}
	
	public synchronized void addRobotsObj(String hostString, RobotsTxt robotsTxt){
		robotsTxtMap.put(hostString, robotsTxt);
	}


	public synchronized boolean isAllow(String hostString, String queryString) throws NoSuchAlgorithmException {

		RobotsTxt robotsTxt = robotsTxtMap.get(hostString);

		if (robotsTxt != null) {
			for (String disallow : robotsTxt.getDisAllowSet()) {
				if (queryString.startsWith(disallow)) {
					return false;
				}
			}
			return true;
		} else {
//			System.out.println("robotsTxt: null " + hostString);
			return true;
		}
	}

	public synchronized long getDelay(String host) {
		Long delay = CrawlerMain.defalutDelay;
		if (robotsTxtMap.containsKey(host)) {
//			System.out.println("Had robotsTxt: " + host);
			RobotsTxt robotsTxt = robotsTxtMap.get(host);
			if(robotsTxt!=null){
				Long delayData = robotsTxt.getDelay();
				if (delayData != null) {
					delay = delayData;
				}
			}
		}
		if(delay<=0){
			delay = (long) 100;
		}
		return delay;
	}

}
