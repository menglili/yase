package edu.upenn.cis455.YASE.searcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.LinkedList;

public class GetURLByWordThread implements Runnable{
	private String keywordString;
	private String indexerAddress;
	private LinkedList<String> urltfList;

	public GetURLByWordThread(String keywordString, String indexerAddress, LinkedList<String> urltfList){ 
		this.keywordString = keywordString;
		this.indexerAddress = indexerAddress;
		this.urltfList = urltfList;
	}
	@Override
	public void run() {

		try {
			URL url = new URL(indexerAddress+"/inverted?word="+keywordString);
//			System.out.println(url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
//			System.out.println("getResponseCode: " + conn.getResponseCode());
			if(conn.getResponseCode()==200){
				InputStream in = conn.getInputStream();
				BufferedReader bw = new BufferedReader(new InputStreamReader(in));
				String line;
				while((line=bw.readLine())!=null){
					urltfList.add(line);
				}
				bw.close();
				conn.disconnect();
			}else{
				System.out.println("Get docByWord error!");
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	
}
