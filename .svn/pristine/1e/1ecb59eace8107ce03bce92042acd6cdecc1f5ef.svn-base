package edu.upenn.cis455.YASE.test;

import java.io.IOException;
import java.util.Comparator;
import java.util.PriorityQueue;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

import edu.upenn.cis455.YASE.searcher.URLVector;

public class TestMain {
	private static Analyzer analyzer;

	public static void main(String args[]) throws IOException{
		analyzer = new EnglishAnalyzer(Version.LUCENE_46);
		String[] domains = "http://www.piazza.com/class".split("[\\./]");
	
		for(String s: domains){
			System.out.println(s);
		}
	}
	
	
}
