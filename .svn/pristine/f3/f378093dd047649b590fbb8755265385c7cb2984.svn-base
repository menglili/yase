package edu.upenn.cis455.YASE.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class CrawlerHttpClient {
	private final  String ACCEPT = "text/html";

	private boolean redirect = false;
	public HttpURLConnection conn;
	private final int FILE_LIMIT = 1000000;
	private final int TIME_OUT = 1000;
	private String urlString;
	private int status;
	public  Logger logger = Logger.getLogger(CrawlerHttpClient.class);


	public CrawlerHttpClient(String urlString){

		this.urlString = urlString;
		logger.setLevel(Level.WARN);
		try {
			URL url = new URL(urlString);
			if(url.getProtocol().equals("http")){
				HttpURLConnection.setFollowRedirects(false);

				conn = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
				conn.setReadTimeout(TIME_OUT);
				conn.setConnectTimeout(TIME_OUT);
				conn.addRequestProperty("Accept-Language", "en-US,en;q=1.0");
				conn.addRequestProperty("User-Agent", CrawlerMain.USER_AGENT);
				conn.addRequestProperty("Accept", ACCEPT);
				conn.addRequestProperty("Connection", "close");
				conn.setUseCaches(false);	
				conn.setInstanceFollowRedirects(false);

			}else{
				logger.debug(urlString + " NOT HTTP");
				conn = null;
				status = 0;
			}
		} catch (IOException e) {
			conn = null;
			status = 0;
			//e.printStackTrace();
		}
		

	}

	public String redirectTo() {

		
		if(conn==null){
			return null;
		}else{
			try {
				status = conn.getResponseCode();
			} catch (IOException e) {
				logger.info(e);
			}
		}
		if (status != HttpURLConnection.HTTP_OK) {
			if (status == HttpURLConnection.HTTP_MOVED_TEMP
					|| status == HttpURLConnection.HTTP_MOVED_PERM
					|| status == HttpURLConnection.HTTP_SEE_OTHER)
			{
				String location = conn.getHeaderField("Location");
				conn.disconnect();
				return location;
			}
			return null;
		}else{
			return null;
		}
	}

	public Document getDoc() throws IOException {
		if (status != HttpURLConnection.HTTP_OK) {
			return null;
		}
		String contentLanguage = conn.getHeaderField("Content-Language");
		String contentLength = conn.getHeaderField("Content-Length");
		String contentType = conn.getHeaderField("Content-Type");

		if (contentLanguage != null) {
			if (!contentLanguage.contains("en")) {
				logger.debug(urlString + " is Not en language! " + contentLanguage);
				return null;
			}
		}

		if (contentLength != null) {
			if (Integer.valueOf(contentLength) > FILE_LIMIT) {
				logger.debug(urlString + " File Limit excess");
				return null;
			}
		}
		if (contentType != null) {
			if (!contentType.contains(ACCEPT)) {
				logger.debug(urlString + contentType + " Not acceptable type!");
				return null;
			}
		}
		Document document = null;
		String contentString = getContentString();
		if (contentString != null){
			document = Jsoup.parse(contentString, urlString);
		}
		else{
			return null;
		}

		Elements langeElements = document.select("html");
		if (langeElements != null) {
			Element langElement = langeElements.first();
			if (langElement != null) {
				String langString = langElement.attr("lang");
				if (!langString.equals("")) {
					if (!langString.contains("en")) {
						logger.debug(urlString + " langString: " + langString);
						return null;
					}
				}
			}
		}
		return document;
	}

	public String getContentString() {
		if(conn==null){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		InputStream is = null;
		try {
			is = conn.getInputStream();

			int i;
			int c = 0;

			while ((i = is.read()) != -1) {
				c++;
				sb.append((char) i);
				if (c > FILE_LIMIT) {
					//System.out.println(urlString+ " File limit excess!");
					return null;
				}
			}
		} catch (IOException e) {
			logger.debug(e);
			//			e.printStackTrace();
			return null;
		}finally{
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			conn.disconnect();
		}
		return sb.toString();
	}

	public int getStatus() {
		return status;
	}
}
