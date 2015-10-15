package edu.upenn.cis455.YASE.test;

import java.io.IOException;

import org.junit.Test;

import edu.upenn.cis455.YASE.pagerank.HandleLinks;

public class TestPageRankConvertFile {
	
	@Test
	public void testMergeAndDuplicate() throws IOException{
		HandleLinks hl=new HandleLinks();
		hl.mergeAndRemoveDup("/home/workspace/links.txt","/home/workspace/links2.txt");
	}
	@Test
	public void testRemoveSinks() throws IOException{
		HandleLinks hl=new HandleLinks();
		hl.mergeAndRemoveDup("/home/workspace/links2.txt","/home/workspace/links3.txt");
	}
	@Test
	public void testHandleDangling() throws IOException{
		HandleLinks hl=new HandleLinks();
		hl.handleDangling("/home/workspace/links3.txt");
	}
	
	

}
