package edu.upenn.cis455.YASE.crawler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class URLFrontier {
	private SyncQueue[] qList;
	public final Logger logger = Logger.getLogger(URLFrontier.class);
	private int numOfQueue;
	private int currentQueue = 0;
	private int numberOfWorkers;
	
	public URLFrontier(int numOfQueue, int numberOfWorkers, String otherURLFrontierPath, int cacheSize){
		File dir = new File(otherURLFrontierPath);
		if (dir.exists()) {
			delete(dir);
		}
		dir.mkdir();
		qList = new SyncQueue[numOfQueue];
		this.numberOfWorkers = numberOfWorkers;
		for (int i = 0; i < numOfQueue; i++) {
			File subdir = new File(otherURLFrontierPath+"/Queue"+i);
			subdir.mkdir();
			qList[i] = new SyncQueue(subdir.toString(),"Queue" + i,cacheSize);
		}
		this.numOfQueue = numOfQueue;
		logger.setLevel(Level.WARN);
	}
	public synchronized String poll() throws IOException {
//		logger.info("From queue: " + currentQueue);
		String url = qList[currentQueue].poll();
		currentQueue++;
		if(currentQueue == numOfQueue){
			currentQueue = 0;
		}
		return url;
		
	}
	
	public synchronized void addOne(final String element){
		
		try {
			URL url = new URL(element);
			int queueID = getQueueID(url.getHost());
//			System.out.println("queueID: " + queueID);
			qList[queueID].addOne(element);
//			logger.warn("add one: " + element);
		} catch (Exception e) {
			logger.debug(e);
		}
//		logger.debug("queue is empty: " + isEmpty());
	}
	
	public synchronized void sync(){
		for(int i =0;i < qList.length; i++){
			qList[i].sync();
		}
	}
	
	public synchronized void addBatch(LinkedList<String> urlList) throws NoSuchAlgorithmException, IOException{
		for(String url:urlList){
			addOne(url);
		}
	}
	
	public synchronized boolean isEmpty() {
		for(int i =0;i < qList.length; i++){
			if(!qList[i].isEmpty()){
				return false;
			}
		}
		return true;
	}

	public synchronized void close() {
		for(int i =0;i < qList.length; i++){
			qList[i].close();
		}
	}
	
	private int getQueueID(String host) throws NoSuchAlgorithmException{
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.update(host.getBytes());
		byte byteData[] = messageDigest.digest();
		int hash = byteData[0]+byteData[1]+byteData[2]+byteData[3]+512;
		return hash/numberOfWorkers%numOfQueue;
	}
	
	private synchronized void delete(File file) {
		if (file.isDirectory()) {
			if (file.list().length == 0) {
				file.delete();
			} else {
				String files[] = file.list();

				for (String temp : files) {
					File fileDelete = new File(file, temp);
					delete(fileDelete);
				}

				if (file.list().length == 0) {
					file.delete();
				}
			}

		} else {
			file.delete();
		}
	}
	
	

}
