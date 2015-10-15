package edu.upenn.cis455.YASE.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import edu.upenn.cis455.YASE.search.SpellCheck;

public class TestSpellCheck {
	
	
	
	
	@Test
	public void wordDistance() throws IOException{
		SpellCheck sc= new SpellCheck("/home/cis455/workspace/YASE/resource/dictionary.txt");
		//test add a new char
		int num1=sc.computeDistance("apple", "applef");
		assertEquals(num1,1);
		//test order
		int num2=sc.computeDistance("apple","appel");
		assertEquals(num2,2);
		//test remove a char
		int num3=sc.computeDistance("apple","appl");
		assertEquals(num3,1);	
	}	
	
	@Test
	public void testSuggestWord() throws IOException{	
		SpellCheck sc= new SpellCheck("/home/cis455/workspace/YASE/resource/dictionary.txt");
		String suggestWord1=sc.wordSuggester("apple");
		assertEquals(suggestWord1,"apple");
		String suggestWord2=sc.wordSuggester("appl");
		assertEquals(suggestWord2,"apply");
	}
}
