package edu.upenn.cis455.YASE.crawler;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class SendURLThread implements Runnable {
	private int myWorkerID;
	private ArrayList<ArrayList<String>> workerList;
	private SyncFroniterWriter syncFroniterWriter;
	public final Logger logger = Logger.getLogger(SendURLThread.class);
	private final int urlSize = 10000;

	public SendURLThread(int myWorkerID, ArrayList<ArrayList<String>> workerList, SyncFroniterWriter syncFroniterWriter){
		this.myWorkerID = myWorkerID;
		this.workerList = workerList;
		this.syncFroniterWriter = syncFroniterWriter;
		logger.setLevel(Level.INFO);
	}

	@Override
	public void run() {
		while(true){
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for(int i = 0; i<workerList.size();i++){
				if(i==myWorkerID)
					continue;
				StringBuilder sb = new StringBuilder();
				for(int j = 0; j<urlSize;j++){
					String url = syncFroniterWriter.get(i);
					if(url==null)
						break;
					sb.append(url);
					sb.append(System.lineSeparator());
					logger.debug("Send: " + url);

				}
				boolean success = send(sb.toString(), workerList.get(i).get(0), Integer.valueOf(workerList.get(i).get(1)));
			}
			
		}
	}

	private boolean send(String str, String ip, int port){

		Socket socket = null;
	    OutputStreamWriter osw;
	    try {
	        socket = new Socket();
	        socket.connect(new InetSocketAddress(ip, port), port);
	        osw =new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
	        osw.write(str, 0, str.length());
	    } catch (IOException e) {
	    	logger.warn("send to worker fail " + ip+":"+port);
	    	logger.warn(e);
	    	return false;
	    } finally {
	        try {
				socket.close();
			} catch (IOException e) {
				//e.printStackTrace();
			}
	    }
		return true;
	}

}
