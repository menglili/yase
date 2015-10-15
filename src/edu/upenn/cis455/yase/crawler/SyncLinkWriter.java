package edu.upenn.cis455.YASE.crawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;

public class SyncLinkWriter {
	private File file;
	private FileWriter fw;
	private BufferedWriter bw;

	public SyncLinkWriter(String linksPath) {
		file = new File(linksPath);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);
			
		} catch (IOException e) {
			System.out.println("Cannot create links.txt " + linksPath);
			System.exit(0);
		}
	}

	public synchronized void writeLink(String sourceURL,
			HashSet<String> destinationList) {

		StringBuilder sb = new StringBuilder();
		sb.append(sourceURL);
		sb.append("\t");
		sb.append("1.0");
		for (String url : destinationList) {
			sb.append("\t");
			sb.append(url);
		}
		sb.append(System.lineSeparator());

		try {
			bw.write(sb.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
