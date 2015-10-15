package edu.upenn.cis455.YASE.test;

import java.security.NoSuchAlgorithmException;

import edu.upenn.cis455.YASE.crawler.RobotsClient;
import edu.upenn.cis455.YASE.crawler.RobotsTxt;
import edu.upenn.cis455.YASE.crawler.RobotsTxtManager;
import edu.upenn.cis455.YASE.crawler.URLFilter;
import junit.framework.TestCase;

public class TestCrawler extends TestCase{
	
	public void testRobotManager() throws NoSuchAlgorithmException{
		URLFilter urlFilter = new URLFilter(10000);
		RobotsTxtManager robotsTxtManager = new RobotsTxtManager(10);
		
		if(robotsTxtManager.isCached("crawltest.cis.upenn.edu")){
			assertEquals(robotsTxtManager.isAllow("crawltest.cis.upenn.edu", "/marie/private/"),false);
			System.out.println("cached!");
		}else{
			RobotsClient robotsClient = new RobotsClient(urlFilter, "crawltest.cis.upenn.edu");
			RobotsTxt robotsTxt = robotsClient.getRobotsObj();
			robotsTxtManager.addRobotsObj("crawltest.cis.upenn.edu", robotsTxt);
			assertEquals(robotsTxtManager.isAllow("crawltest.cis.upenn.edu", "/marie/private/"),false);
			System.out.println("Not cached!");

		}
		
		if(robotsTxtManager.isCached("crawltest.cis.upenn.edu")){
			assertEquals(robotsTxtManager.isAllow("crawltest.cis.upenn.edu", "/marie/private/"),false);
			System.out.println("cached!");
		}else{
			RobotsClient robotsClient = new RobotsClient(urlFilter, "crawltest.cis.upenn.edu");
			RobotsTxt robotsTxt = robotsClient.getRobotsObj();
			robotsTxtManager.addRobotsObj("crawltest.cis.upenn.edu", robotsTxt);
			assertEquals(robotsTxtManager.isAllow("crawltest.cis.upenn.edu", "/marie/private/"),false);
			System.out.println("Not cached!");

		}
		
		if(robotsTxtManager.isCached("crawltest.cis.upenn.edu")){
			assertEquals(robotsTxtManager.getDelay("crawltest.cis.upenn.edu"), 5000);
			System.out.println("cached!");
		}else{
			RobotsClient robotsClient = new RobotsClient(urlFilter, "crawltest.cis.upenn.edu");
			RobotsTxt robotsTxt = robotsClient.getRobotsObj();
			robotsTxtManager.addRobotsObj("crawltest.cis.upenn.edu", robotsTxt);
			assertEquals(robotsTxtManager.getDelay("crawltest.cis.upenn.edu"), 5000);
			System.out.println("Not cached!");

		}
		
//		
//		assertEquals(robotsTxtManager.isAllow("crawltest.cis.upenn.edu", "/marie/private/"),false);
//		assertEquals(robotsTxtManager.isAllow("crawltest.cis.upenn.edu", "/nytimes/"),true);
//		assertEquals(robotsTxtManager.isAllow("crawltest.cis.upenn.edu", "/nytimes/"),true);
//		assertEquals(robotsTxtManager.isAllow("crawltest.cis.upenn.edu", "/marie/private/"),false);
//		assertEquals(robotsTxtManager.isAllow("www.856d722.com", "/marie/private/"),true);


	}

}
