package edu.upenn.cis455.YASE.crawler;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class SyncFroniterWriter {
//	private static File[] fList;
//	private BufferedWriter[] bwList;
//	private static int[] urlCounter;
//	private final static int URL_FILE_LIMIT = 20000;
//	private String otherURLFrontierPath;
//	public final Logger logger = Logger.getLogger(SyncFroniterWriter.class);
	
	private SyncQueue[] qList;
	public final Logger logger = Logger.getLogger(SyncFroniterWriter.class);
	private final int CACHE_SIZE  = 10000;


	public SyncFroniterWriter(String otherURLFrontierPath, int numberOfWorkers) {
		
		File dir = new File(otherURLFrontierPath);
		if (dir.exists()) {
			delete(dir);
		}
		dir.mkdir();
		qList = new SyncQueue[numberOfWorkers];
		
		for (int i = 0; i < numberOfWorkers; i++) {
			File subdir = new File(otherURLFrontierPath+"/ToBeSent"+i);
			subdir.mkdir();
			qList[i] = new SyncQueue(subdir.toString(),"ToBeSent" + i,CACHE_SIZE);
		}
		
		logger.setLevel(Level.INFO);
	}

	public synchronized void write(int workerID, String url) {
		try {
			qList[workerID].addOne(url);
		} catch (IOException e) {
			logger.warn("Write to OtherURL fails, workerID: " + workerID);
			e.printStackTrace();
		}
	}
	
	public synchronized String get(int workerID){
		try {
			return qList[workerID].poll();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
	
	public void close(){
		for(SyncQueue sq:qList){
			sq.close();
		}
	}
}
