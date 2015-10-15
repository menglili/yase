package edu.upenn.cis455.YASE.crawler;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class RobotsClient {

	private String robotsString;
	
	public RobotsClient(URLFilter urlFilter, String hostString) throws NoSuchAlgorithmException{
		
		if(hostString ==null){
			robotsString= null;
		}
		try {
			urlFilter.setHostTime(hostString, System.currentTimeMillis());
			CrawlerHttpClient robotsTxt = new CrawlerHttpClient("http://"
					+ hostString + "/robots.txt");

			if (robotsTxt.getStatus() != 200) {
//				System.out.println("status: " + robotsTxt.getStatus());
				robotsString = null;
			} else {
				robotsString = robotsTxt.getContentString();
			}
		} catch (IOException e) {
			robotsString = null;
		}		
		
	}
	public RobotsTxt getRobotsObj() throws NoSuchAlgorithmException {

		if(robotsString==null){
			return null;
		}
//		System.out.println("robotsString: " + robotsString);
		RobotsTxt robotsTxtObj = new RobotsTxt();
		try {
			String UserAgent[] = robotsString.split("User-agent: ");
			boolean foundMyName = false;
			for (String s : UserAgent) {
				// System.out.println("UserAgent: " + s);
				String[] attr = s.split(System.lineSeparator());
				if (attr.length > 1) {
					// System.out.println("agent name: " + attr[0]);
					if (attr[0].equals(CrawlerMain.USER_AGENT)) {
						foundMyName = true;
						for (String a : attr) {
							String[] pair = a.split(": ");
							if (pair[0].equals("Disallow")) {
								if (pair.length == 2) {
									// System.err.println("getPath: " +
									// thisURL.getPath() + " disallow" +
									// pair[1]);
									robotsTxtObj.addDisAllow(pair[1]);
								}
							} else if (pair[0].equals("Crawl-delay")) {
								if (pair.length == 2) {
									robotsTxtObj.addDelay(Integer
											.valueOf(pair[1]));
								}
							}
						}
					}
				}
			}

			if (!foundMyName) {
				for (String s : UserAgent) {
					// System.out.println("UserAgent: " + s);
					String[] attr = s.split(System.lineSeparator());
					if (attr.length > 1) {
						// System.out.println("agent name: " + attr[0]);
						if (attr[0].equals("*")) {
							for (String a : attr) {
								String[] pair = a.split(": ");
								if (pair[0].equals("Disallow")) {
									if (pair.length == 2) {
										// System.err.println("getPath: " +
										// thisURL.getPath() + " disallow: " +
										// pair[1]);
										robotsTxtObj.addDisAllow(pair[1]);
									}
								} else if (pair[0].equals("Crawl-delay")) {
									if (pair.length == 2) {
										robotsTxtObj.addDelay(Integer
												.valueOf(pair[1]));
									}
								}
								// System.out.println("delayTime: " +
								// delayTime);
							}
						}
					}
				}
			}

		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		}
		return robotsTxtObj;
	}

}
