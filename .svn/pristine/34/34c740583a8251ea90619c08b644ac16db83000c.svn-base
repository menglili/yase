package edu.upenn.cis455.YASE.crawler;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DebugCrawler {
	private static int numOfWorkers =5;
	
	public static void main(String args[]) throws MalformedURLException, NoSuchAlgorithmException{

			System.out.println(getWorkerID("www.dmoz.org"));
			System.out.println(getWorkerID("www.joeant.com"));
			System.out.println(getWorkerID("www.a1webdirectory.org"));
			System.out.println(getWorkerID("botw.org"));
			System.out.println(getWorkerID("dir.yahoo.com"));
			System.out.println(getWorkerID("en.wikipedia.org"));
			System.out.println(getWorkerID("www.jasminedirectory.com"));

//		domainExtension.add("com");
//		domainExtension.add("net");
//		domainExtension.add("org");
//		domainExtension.add("edu");
//		domainExtension.add("gov");
//		domainExtension.add("mil");
//		domainExtension.add("biz");
//		domainExtension.add("info");
//		
//		String url1 = "www.google.com";
//		String url2 = "google.net";
//		String url3 = "aaa.vvv.google.com";
//		String url4 = ".com";
//		String url6 = "com";
//		String url7 = ".";
//
//		System.out.println(new URL("httP://ddd.wWw.fAD.Csd.com////").getPath());
//		
		
	}
	
	private static String getTopHost(String host){
		String[] values = host.split("\\.");
		int len = values.length; 
		if(len<2){
			return null;
		}else if(values[len-2].equals("")){
			return null;
		}
		return values[len-2] + "." + values[len-1];	
	}
	
	private static int getWorkerID(String host) throws NoSuchAlgorithmException{
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.update(host.getBytes());
		byte byteData[] = messageDigest.digest();
		int hash = byteData[1]+128;
//		System.out.println(hash);
		return hash%numOfWorkers;
	}

}
