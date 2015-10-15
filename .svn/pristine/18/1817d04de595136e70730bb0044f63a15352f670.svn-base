package edu.upenn.cis455.YASE.crawler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class CrawlerMain {

	public static final String MY_URLFRONTIER_DIR = "MyURLFrontier";
	public static final String OTHER_URLFRONTIER_DIR = "OtherURLFrontier";
	public static final int LOCAL_FRONTIER_CACHE_SIZE = 10000;
	public static final int LOCAL_DUE_CACHE_SIZE = 5000000;
	public static final int ROBOTS_CACHE_SIZE = 200000;
	public static final  String USER_AGENT = "cis455crawler";
	public static long defalutDelay;

	public static final Format formatter = new SimpleDateFormat(
			"MM-dd-HH_mm_ss");
	public static final long SYSTEM_START_TIME = System.currentTimeMillis();
	public static final String SYSTEM_START_TIME_STRING = formatter
			.format(new Date(SYSTEM_START_TIME));
	public static final String RAW_FILENAME = "raw_" + SYSTEM_START_TIME_STRING
			+ ".txt";
	public static final String LINKS_FILENAME = "links_"
			+ SYSTEM_START_TIME_STRING + ".txt";

	public static void main(String args[]) throws NoSuchAlgorithmException,
			IOException {

		if (args.length != 7) {
			System.out
					.println("usage: workerList myWorkerID rootDir numberOfThreads seedfile pageGoal defaultDelay");
			System.exit(0);
		}

		String workerListPath = args[0];
		int numberOfWorkers = 0;
		BufferedReader br = null;
		ArrayList<ArrayList<String>> workerList = null;
		try {
			br = new BufferedReader(new FileReader(workerListPath));
			String line;

			workerList = new ArrayList<ArrayList<String>>();

			while ((line = br.readLine()) != null) {
				String[] pairs = line.split("\t");
				ArrayList<String> ipAndPort = new ArrayList<>();
				ipAndPort.add(pairs[0]);
				ipAndPort.add(pairs[1]);
				workerList.add(ipAndPort);
				numberOfWorkers++;
			}
		} catch (IOException e) {
			System.out.println(workerListPath + " does not existed!");
			System.exit(0);
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("numberOfWorkers: " + numberOfWorkers);
		int myWorkerID = Integer.valueOf(args[1]);
		String rootDir = args[2];
		int numberOfThreads = Integer.valueOf(args[3]);
		String seedFile = args[4];
		
		int pageGoal = Integer.valueOf(args[5]);
		defalutDelay = Integer.valueOf(args[6]);

		URLFrontier urlFrontier = new URLFrontier(numberOfThreads*3, numberOfWorkers, rootDir + "/"
				+ MY_URLFRONTIER_DIR, LOCAL_FRONTIER_CACHE_SIZE);

		URLFilter urlFilter = new URLFilter(LOCAL_DUE_CACHE_SIZE);
		RobotsTxtManager robotsTxtManager = new RobotsTxtManager(
				ROBOTS_CACHE_SIZE);
		SyncRawWriter rawWriter = new SyncRawWriter(rootDir + "/"
				+ RAW_FILENAME);
		SyncLinkWriter linkWriter = new SyncLinkWriter(rootDir + "/"
				+ LINKS_FILENAME);
		SyncFroniterWriter froniterWriter = new SyncFroniterWriter(rootDir
				+ "/" + OTHER_URLFRONTIER_DIR, numberOfWorkers);
		LinkExtractor linkExtractor = new LinkExtractor(urlFilter, linkWriter,
				froniterWriter, numberOfWorkers, myWorkerID);

		// Receive url listener
		Thread crawlerListenerThread = new Thread(new CrawlerListenerThread(
				urlFilter, urlFrontier, Integer.valueOf(workerList.get(
						myWorkerID).get(1))));
		crawlerListenerThread.start();

		// send url thread start
		Thread sendURLThread = new Thread(new SendURLThread(myWorkerID,
				workerList, froniterWriter));
		sendURLThread.start();

		// put seed to URL Frontier		
		readSeeds(urlFrontier, seedFile, linkExtractor);
		
		ArrayList<Thread> threaArrayList = new ArrayList<Thread>();
		for (int i = 0; i < numberOfThreads; i++) {
			Thread thread = new Thread(new CrawlerWorkerThread(urlFrontier,
					urlFilter, rawWriter, linkExtractor, robotsTxtManager)," Worker No: " + i);
			threaArrayList.add(thread);
			thread.start();
		}

		// check point thread start
		Thread checkPointThread = new Thread(new CheckPointThread(urlFrontier,
				urlFilter, robotsTxtManager, rawWriter, linkWriter,
				froniterWriter, threaArrayList, pageGoal));
		checkPointThread.start();

	}
	
	public static void readSeeds(URLFrontier urlFrontier, String seedFile, LinkExtractor linkExtractor) throws NoSuchAlgorithmException{
		
		
		BufferedReader br = null;
		LinkedList<String> myLinks = new LinkedList<String>();
		try {
			br = new BufferedReader(new FileReader(seedFile));
			String line;
			while ((line = br.readLine()) != null) {
//				System.out.println(line);
				linkExtractor.distributeLinks("http://" + line.toLowerCase(), myLinks);
			}
			urlFrontier.addBatch(myLinks);
			
		}catch(IOException e){
			System.err.println("Read seed file failed");
		}
		urlFrontier.sync();
	}
}
