package edu.upenn.cis455.YASE.crawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class SyncRawWriter {
	private File file;
	private FileWriter fw;
	private BufferedWriter bw;
	public  Logger logger = Logger.getLogger(SyncRawWriter.class);
	private final boolean debug = true;
	private int totalPages = 0;
	
	public SyncRawWriter(String rawPath){
		file = new File(rawPath);
		try {
			if (!file.exists()) {
				file.createNewFile();
				System.out.println("Create: " + file.getAbsolutePath());
			}
			fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);
			
		} catch (IOException e) {
			System.out.println("Cannot create raw.txt " + rawPath);
			System.exit(0);
		}
		logger.setLevel(Level.WARN);
	}
	
	public synchronized void write(String url, long time, String title, String content){
		StringBuilder sb = new StringBuilder();	
		sb.append(url);
		sb.append("\t");
		sb.append(time);
		sb.append("\t");
		sb.append(title);
		sb.append("\t");
		sb.append(content);
		sb.append(System.lineSeparator());
//		if(debug){
//			logger.info(sb.toString());
//		}
		
		try {
			bw.write(sb.toString());
			totalPages++;
			
			//System.out.println("current total: " + totalPages);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.warn(e.toString());
			e.printStackTrace();
		}
	}
	
	public synchronized int getTotalPages(){
		return this.totalPages;
	}
	
	public void close(){
		try {
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
