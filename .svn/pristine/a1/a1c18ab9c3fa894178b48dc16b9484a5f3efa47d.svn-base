package edu.upenn.cis455.YASE.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;

import org.jsoup.nodes.Document;

public class HttpClient {
	Socket s;
	OutputStream output;
	InputStream inputStream;
	BufferedReader bufferedReader;
	private final  String ACCEPT = "text/html";
	private boolean redirect = false;

	public HttpClient(String urlsString) throws IOException{
		URL url = new URL(urlsString);
		int port = url.getPort();
		String urlPath = url.getPath();
		
		s = new Socket(url.getHost(), port==-1?80:port);
		output = s.getOutputStream();
		PrintWriter pw = new PrintWriter(output, false);
		pw.println("HEAD " + urlPath + " HTTP/1.1");
		pw.println("Host: " + url.getHost());
		pw.println("User-Agent: " + CrawlerMain.USER_AGENT);
		pw.println("Accept-Language: en-US,en;q=1.0");
		pw.println("Accept: " + ACCEPT);
		pw.println("Connection: close");
		pw.println("");
		pw.flush();
		
		inputStream = s.getInputStream();
		bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		String firstLine = bufferedReader.readLine();
		//System.out.println(thisURL + "firstLine: " + firstLine);
		
		String statusCode = firstLine.split(" ")[1];

		if (statusCode.equals("301") || statusCode.equals("302")){
			redirect = true;
		}
		String inputLine;
		StringBuilder sb = new StringBuilder();
		while ((inputLine = bufferedReader.readLine()) != null) {
			sb.append(inputLine);
			sb.append(System.lineSeparator());
		}
		
	}
	public String redirectTo() {
		
		return null;
	}
	
	public Document getDoc() throws IOException {
		
		return null;
	}
	
	public InputStream getInputStream(){
		
		return null;
	}
	
	
	private HashMap<String, String> getHeaderMap(String headerStr) {

		HashMap<String, String> headersMap = new HashMap<String, String>();
		// Some headers, such as Accept-Language can be sent by clients as
		// several
		// headers each with a different value rather than sending the header as
		// a
		// comma separated list.

		String headers[] = headerStr.split(System.lineSeparator());
		for (String header : headers) {

			// System.out.println("header line: " + line);
			String[] headerPair = header.split(":[\\s]");

			if (headerPair.length != 2) {
				System.err.println("Header does not have two tokens!");
				continue;
			}

			String headerName = headerPair[0].toLowerCase();
			String headerValue = headerPair[1];

			switch (headerName) {
			case "content-type": {
				headersMap.put("content-type", headerValue);
				// System.out.println("Host is: " + headers.get("host"));
				break;
			}
			case "content-length": {
				headersMap.put("content-length", headerValue);
				// System.out.println("expect is: " +
				break;
			}

			case "location": {
				headersMap.put("location", headerValue);
				// System.out.println("expect is: " +
				break;
			}

			}
		}

		return headersMap;

	}
	


}
