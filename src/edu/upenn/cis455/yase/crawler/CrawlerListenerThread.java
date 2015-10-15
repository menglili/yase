package edu.upenn.cis455.YASE.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class CrawlerListenerThread implements Runnable{

	private URLFrontier urlFrontier;
	private int listeningPort;
	private URLFilter urlFilter;
	public final Logger logger = Logger.getLogger(CrawlerListenerThread.class);

	public CrawlerListenerThread(URLFilter urlFilter, URLFrontier urlFrontier, int listeningPort){
		this.urlFrontier = urlFrontier;
		this.listeningPort = listeningPort;
		this.urlFilter = urlFilter;
		logger.setLevel(Level.WARN);
	}

	@Override
	public void run() {

		String url;
		ServerSocket echoServer = null;
		try {
			echoServer = new ServerSocket(listeningPort);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		logger.warn("Start listening on: " + listeningPort);

		while(true){
			try {
				Socket clientSocket = echoServer.accept();
				InputStreamReader inputstreamreader = new InputStreamReader(clientSocket.getInputStream());
				BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
				while ((url=bufferedreader.readLine())!=null) {
					logger.debug("Receive: " + url);
					try{
						if(!urlFilter.isVisted(url)){
							//urlFilter.setVisited(url);
							urlFrontier.addOne(url);
						}else{
							logger.debug("Visited: " + url);
						}
					}catch(MalformedURLException e1){
						logger.warn(e1);
						continue;
					}
				}
				logger.debug("Receive Complete");
			}catch( IOException | NoSuchAlgorithmException e) {
				logger.warn(e);
			}
		}
	}
}
