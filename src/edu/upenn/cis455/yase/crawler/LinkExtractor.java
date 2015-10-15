package edu.upenn.cis455.YASE.crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.hadoop.mapred.IFile;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class LinkExtractor {
	private URLFilter urlFilter;
	private int numOfWorkers;
	private int myWorkerID;
	private SyncLinkWriter linkWriter;
	private SyncFroniterWriter froniterWriter;
	private final boolean debug = true;
	public  Logger logger = Logger.getLogger(LinkExtractor.class);
	private HashSet<String> domainExtension;


	public LinkExtractor(URLFilter urlFilter,
			SyncLinkWriter linkWriter, SyncFroniterWriter froniterWriter, int numOfWorkers, int myWorkerID) throws NoSuchAlgorithmException {
		this.urlFilter = urlFilter;
		this.numOfWorkers = numOfWorkers;
		this.myWorkerID = myWorkerID;
		this.linkWriter = linkWriter;
		this.froniterWriter = froniterWriter;
		domainExtension = new HashSet<>();
		domainExtension.add("com");
		domainExtension.add("net");
		domainExtension.add("org");
		domainExtension.add("edu");
		domainExtension.add("gov");
		domainExtension.add("mil");
		domainExtension.add("biz");
		domainExtension.add("info");

		logger.setLevel(Level.INFO);

	}

	public LinkedList<String> getMyLinks(Document htmlocument, String urlString) throws NoSuchAlgorithmException {
		LinkedList<String> myLinks = new LinkedList<String>();
		HashSet<String> allLinks = new HashSet<String>();

		URL url = null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			// e.printStackTrace();
			return null;
		}
		Elements links = htmlocument.select("a");

		for (Element link : links) {
			try{
				String linkHref = link.absUrl("href");
				if(linkHref.equals("")){
					continue;
				}


				URL currentUrl = new URL(linkHref);	
				String currentHost = currentUrl.getHost().toLowerCase();
				String currentprotocol = currentUrl.getProtocol().toLowerCase();
				String currentPath = currentUrl.getPath();
				StringBuilder sb = new StringBuilder();
				sb.append(currentprotocol);
				sb.append("://");
				sb.append(currentHost);
				if(!currentPath.equals("/")){
					sb.append(currentUrl.getPath());
				}
				String currentURLString = sb.toString();
				
				//System.out.println(currentURLString);
				
				String[] currentDomain = currentHost.split("\\.");
				if(currentDomain.length>0){
					if(!domainExtension.contains(currentDomain[currentDomain.length-1].toLowerCase())){
						//System.out.println(currentDomain[currentDomain.length-1].toLowerCase());
						continue;
					}
				}
				if(!currentprotocol.equals("http")){
					logger.debug("Protocol: " + currentprotocol);
					continue;
				}

				allLinks.add(getTopHost(currentHost));
				int workID = getWorkerID(currentHost);
				logger.debug("workID: " + workID);
				logger.debug("myWorkerID: " + myWorkerID);

				if(workID == myWorkerID){
					if(!urlFilter.isVisted(currentURLString)){
						myLinks.add(currentURLString);
						//urlFilter.setVisited(currentURLString);
					}
				}else{
					if(!urlFilter.isVisted(currentURLString)){
						froniterWriter.write(workID,currentURLString);
						//urlFilter.setVisited(currentURLString);
					}
				}
			} catch (MalformedURLException e) {
				continue;
			}
		}

		logger.debug("allLinks: " + allLinks);
		logger.debug("myLinks: " + myLinks);

		linkWriter.writeLink(getTopHost(url.getHost()), allLinks); 

		return myLinks;
	}
	
	public LinkedList<String> distributeLinks(String url, LinkedList<String> myLinks) throws NoSuchAlgorithmException{
		
		int workerID = getWorkerID(url);
		if(workerID == myWorkerID){
			//System.out.println("my url: " + url);
			myLinks.add(url);
		}
		return myLinks;
	}

	private int getWorkerID(String host) throws NoSuchAlgorithmException{
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.update(host.getBytes());
		byte byteData[] = messageDigest.digest();
		int hash = byteData[1]+128;
		return hash%numOfWorkers;
	}
	
	private String getTopHost(String host){
		String[] values = host.split("\\.");
		int len = values.length; 
		if(len<2){
			return null;
		}else if(values[len-2].equals("")){
			return null;
		}
		return (values[len-2] + "." + values[len-1]).toLowerCase();	
	}

}
