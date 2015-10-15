package edu.upenn.cis455.YASE.crawler;

import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * 
 * @author Chaoyi Huang
 *
 *	Periodically save URL Set and URL Frontier to file
 *	 
 */
public class CheckPointThread implements Runnable{

	private URLFrontier urlFrontier;
	private URLFilter urlFilter;
	private RobotsTxtManager robotsTxtManager;
	private SyncRawWriter rawWriter;
	private SyncFroniterWriter froniterWriter;
	private ArrayList<Thread> threaArrayList;
	private SyncLinkWriter linkWriter;
	public final Logger logger = Logger.getLogger(CheckPointThread.class);
	private int pageGoal;
	private final int checkInterval = 60000;

	public CheckPointThread(URLFrontier urlFrontier, URLFilter urlFilter,
			RobotsTxtManager robotsTxtManager, SyncRawWriter rawWriter,SyncLinkWriter linkWriter,
			SyncFroniterWriter froniterWriter, ArrayList<Thread> threaArrayList, int pageGoal) {

		this.pageGoal = pageGoal;
		this.urlFrontier = urlFrontier;
		this.urlFilter = urlFilter;
		this.robotsTxtManager = robotsTxtManager;
		this.rawWriter = rawWriter;
		this.froniterWriter =froniterWriter;
		this.threaArrayList = threaArrayList;
		this.linkWriter = linkWriter;
		logger.setLevel(Level.INFO);
	}

	@Override
	public void run() {
		
		while(true){
			try {
				Thread.sleep(checkInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			};
			
			if(rawWriter.getTotalPages() > pageGoal){
				
				for(Thread t: threaArrayList){
					t.interrupt();
				}
				for(Thread t: threaArrayList){
					try {
						t.join(5000);
						logger.warn(t.getName() + " is terminated!");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				urlFrontier.close();
				rawWriter.close();
				froniterWriter.close();
				linkWriter.close();
				logger.warn("<--- Completed Total Pages: " + rawWriter.getTotalPages() + " Used: " + (System.currentTimeMillis() - CrawlerMain.SYSTEM_START_TIME)/1000 + " Seconds --->");
				System.exit(0);
			}else{
				logger.warn("Crawled Pages: " + rawWriter.getTotalPages() + " Used: " + (System.currentTimeMillis() - CrawlerMain.SYSTEM_START_TIME)/1000 + " Seconds");
			}
			
			
		}
		// TODO Auto-generated method stub
		
	}
	
}
