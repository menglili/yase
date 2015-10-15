package edu.upenn.cis455.YASE.searcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.LinkedList;

public class GetPreviewByURLThread implements Runnable{
	
	private String keywords;
	private String urlString;
	private String indexerAddress;
	private LinkedList<String> timeTitleAndContent;
	
	public GetPreviewByURLThread(String keywords, String urlString, String indexerAddress, LinkedList<String> titleAndContent){
		this.keywords= keywords;
		this.urlString = urlString;
		this.indexerAddress = indexerAddress;
		this.timeTitleAndContent = titleAndContent;
	}

	@Override
	public void run() {
		try {
			
			URL url = new URL(indexerAddress+"/forward?url="+urlString+"&keywords="+keywords.replaceAll(" ", "%20"));
//			URL url = new URL("http://127.0.0.1:8080/indexer/forward?url=http://www.google.com&keywords=hello%20world");
//			System.out.println(indexerAddress+"/forward?url="+urlString+"&keywords="+keywords);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
//			System.out.println("getResponseCode: " + conn.getResponseCode());
			if(conn.getResponseCode()==200){
				InputStream in = conn.getInputStream();
				BufferedReader bw = new BufferedReader(new InputStreamReader(in));
				String line;
				while((line=bw.readLine())!=null){
					 String[] values = line.split("\t");
//					 System.out.print("urlString: " + urlString);
					 for(String s: values){
						 timeTitleAndContent.add(s);
//						 System.out.print("CONTENT: " + s);
					 }
//					 System.out.println();

				}
				bw.close();
				conn.disconnect();
			}else{
				System.out.println("Get preview error!");
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}		
	

}
