package edu.upenn.cis455.YASE.crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;


/**
 * Distributed Crawler Thread
 * @author Chaoyi Huang 
 */
public class CrawlerWorkerThread implements Runnable {

	private URLFrontier urlFrontier;
	private URLFilter urlFilter;
	private SyncRawWriter rawWriter;
	private LinkExtractor linkExtractor;
	private RobotsTxtManager robotsTxtManager;

	public  Logger logger = Logger.getLogger(CrawlerWorkerThread.class);

	public CrawlerWorkerThread(URLFrontier urlFrontier,
			URLFilter urlFilter, 
			SyncRawWriter rawWriter, LinkExtractor linkExtractor, RobotsTxtManager robotsTxtManager) {
		this.urlFrontier = urlFrontier;
		this.urlFilter = urlFilter;
		this.rawWriter = rawWriter;
		this.linkExtractor = linkExtractor;
		this.robotsTxtManager = robotsTxtManager;
		logger.setLevel(Level.WARN);
	}

	public void run() {
		while (true&&!Thread.currentThread().isInterrupted()) {
			try {
				logger.warn(Thread.currentThread().getName() + " Frontier Empty!");
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				return;
			}
			logger.debug("urlFrontier.isEmpty(): " + urlFrontier.isEmpty());	

			while (!urlFrontier.isEmpty()&&!Thread.currentThread().isInterrupted()) {

				String urlString = null;
				try{
					urlString = urlFrontier.poll();
					if(urlString==null)
						continue;
					logger.debug(System.currentTimeMillis() + Thread.currentThread().getName() + " poll: " + urlString);	

					URL url = null;
					try{
						url = new URL(urlString);
					}catch(MalformedURLException e){
						logger.debug(e);
						continue;
					}
					String urlHost = url.getHost();
					String urlPath = url.getPath();
					//long robotstart = System.currentTimeMillis();
					//logger.debug(Thread.currentThread().getName()+" Requesting Robots: " + urlString);	

					// check robots.txt complaint
					if(!robotsTxtManager.isCached(urlHost)){
						logger.debug(Thread.currentThread().getName()+" Requesting Robots " + urlString);	
						RobotsClient robotsClient = new RobotsClient(urlFilter, urlHost);
						RobotsTxt robotsTxt = robotsClient.getRobotsObj();
						robotsTxtManager.addRobotsObj(urlHost, robotsTxt);
					}else{
						logger.debug(Thread.currentThread().getName()+" Robots Found: " + urlString);	

					}
					//logger.debug((System.currentTimeMillis()-robotstart) + Thread.currentThread().getName()+" Robots finished: " + urlString);	

					if(robotsTxtManager.isAllow(urlHost,urlPath)){
						//logger.info("Robots allowed! " );
						// check crawler delay complaint
						boolean delayAllowed = false;
						long currentTime = System.currentTimeMillis();
						synchronized (urlFilter) {							
							delayAllowed = (currentTime - urlFilter.lastHostVisitedTime(urlHost))>robotsTxtManager.getDelay(urlHost);
							if(delayAllowed){
								urlFilter.setHostTime(urlHost, currentTime);
							}
						}
						if (delayAllowed){
							logger.debug(System.currentTimeMillis() + Thread.currentThread().getName()+" Downloading: " + urlString);	
							CrawlerHttpClient crawlerHttpClient = new CrawlerHttpClient(urlString);
							String redirectString = crawlerHttpClient.redirectTo();
							if(redirectString!=null){
								if(redirectString.contains("?")){
									continue;
								}
								if(!redirectString.toLowerCase().startsWith("http")){
									redirectString = urlString + redirectString;
								}
								urlFrontier.addOne(redirectString);
								logger.debug("Redirect: " + redirectString);
								continue;
							}
							Document htmlDoc = crawlerHttpClient.getDoc();
							if(htmlDoc!=null){
								ContentExtractor contentExtractor = new ContentExtractor(htmlDoc);
								String content = contentExtractor.getContent();
								String title  =contentExtractor.getTitle();
								rawWriter.write(urlString, System.currentTimeMillis(), title, content);

								LinkedList<String> mylinks = linkExtractor
										.getMyLinks(htmlDoc, urlString);
//								System.out.println("mylinks: " + mylinks);
								urlFrontier.addBatch(mylinks);	
								logger.debug(Thread.currentThread().getName()+" Used: " + (System.currentTimeMillis()- currentTime) + " Completed: " + urlString);	

							}else{
								logger.debug(urlString + " doc is null!! " + urlFrontier.isEmpty());
							}
						}else{
							urlFrontier.addOne(urlString);
							logger.debug(urlString + " delay not allow! " + urlFrontier.isEmpty());
						}
					} else {
						logger.debug(urlString + " Not allow!");
					}
				}catch(Exception e){
					logger.warn(urlString);
					logger.warn(e.toString());
					for(StackTraceElement s:e.getStackTrace())
						logger.warn(s);
					//e.printStackTrace();
				}
			}
		}
	}
}
