package edu.upenn.cis455.YASE.crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class URLFilter {
	
	private LinkedHashMap<BitSet,Long> urlSet;
	
	public URLFilter(final int MAX_ENTRIES){
		
		urlSet = new LinkedHashMap<BitSet,Long>(MAX_ENTRIES+1, .75F, true) {
			private static final long serialVersionUID = 1L;
		    public boolean removeEldestEntry(Map.Entry<BitSet,Long> eldest) {
		        return size() > MAX_ENTRIES;
		    }
		};
	}
	
	private synchronized BitSet getHash(String url) throws NoSuchAlgorithmException{
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");;
		messageDigest.update(url.getBytes());
	    byte byteData[] = messageDigest.digest();
        return BitSet.valueOf(byteData);
	}
	
	public synchronized boolean isVisted(String url) throws MalformedURLException, NoSuchAlgorithmException{
		BitSet urlHash = getHash(url);
		boolean visited = urlSet.containsKey(urlHash);
		setVisited(url);
		return visited;
				
				
	}

	
//	public synchronized long lastURLVisitedTime(String url) throws NoSuchAlgorithmException{
//		BitSet urlHash = getHash(url);
//		Long lastURLVisitedTime = urlSet.get(urlHash);
//		if(lastURLVisitedTime!=null){
//			return lastURLVisitedTime;
//		}
//		else{
//			return 0;
//		}
//	}
	
	public synchronized long lastHostVisitedTime(String urlHost) throws NoSuchAlgorithmException{
		BitSet hostHash = getHash(urlHost);
		Long lastHostVisitedTime = urlSet.get(hostHash);
		if(lastHostVisitedTime!=null){
			return lastHostVisitedTime;
		}
		else{
			return 0;
		}
	}
	
	
	public synchronized void setHostTime(String urlHost, long time) throws MalformedURLException, NoSuchAlgorithmException{
		BitSet hostHash = getHash(urlHost);
		urlSet.put(hostHash, time);
	}
	
	private synchronized void setVisited(String url) throws MalformedURLException, NoSuchAlgorithmException{
		BitSet urlHash = getHash(url);
		urlSet.put(urlHash, null);
	}

}
