package edu.upenn.cis455.YASE.search;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Got word suggestion if a word is spelled wrong
 * @author Mengli Li
 *
 */
public class SpellCheck {

	private static ArrayList<String> dictionary = null;
	private static HashMap <String, Integer>newlist = new HashMap<String, Integer>();
	private static String DICTIONARY_PATH="/Users/mengli/Documents/workspace3/TestServlet/resource/dictionary.txt";
	private static List<Entry<String, Integer>> entries = new ArrayList<Entry<String, Integer>>();
	//Map<String, Integer> orderedMap=new LinkedHashMap<String, Integer>();
	
	/**
	 * import dictionary.txt 
	 * @param dictionaryPath -- path of dictionary
	 */
	public static void initSpellChecker(String dictionaryPath){
		dictionary = new ArrayList<String>();
		try{
			BufferedReader in = new BufferedReader(new FileReader(dictionaryPath));
			String temp="";
			while((temp = in.readLine())!=null){
				dictionary.add(temp.toLowerCase());
			}
			in.close();
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * initialize the spell check
	 * @param dictionaryPath  -- the path of the dictionary
	 * @throws IOException
	 */
	public SpellCheck(String dictionaryPath) throws IOException {
		if(dictionary == null)
			initSpellChecker(dictionaryPath);
	}

	/**
	 * whether the dictionary contains the query word, 
	 * @param queryWord -- the word be searched 
	 * @return  -- if return true, no suggestion
	 * 				else the query word is not in the dictionary
	 */
	public boolean isCorrect(String queryWord) {
		return dictionary.contains(queryWord.toLowerCase());
	}
	
	/**
	 * 
	 * Returns the edit distance needed to convert string s1 to s2      
	 * @param s1, s2 two words you want to find the distance between them
	 * @return -- the distance of two words
	 */
	public int computeDistance(String s1, String s2) {
	      
	    s1 = s1.toLowerCase();
	    s2 = s2.toLowerCase();
	    int[] costs = new int[s2.length() + 1];
	    for (int i = 0; i <= s1.length(); i++) {
	      int lastValue = i;
	      for (int j = 0; j <= s2.length(); j++) {
	        if (i == 0)
	          costs[j] = j;
	        else {
	          if (j > 0) {
	            int newValue = costs[j - 1];
	            if (s1.charAt(i - 1) != s2.charAt(j - 1))
	              newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
	            costs[j - 1] = lastValue;
	            lastValue = newValue;
	          }
	        }
	      }
	      if (i > 0)
	        costs[s2.length()] = lastValue;
	    }
	    return costs[s2.length()];
	}
	
	
	/**
	 * return the suggest word when given one word
	 * @param queryWord -- word be searched
	 * @return -- suggest word
	 */
    public String wordSuggester(String queryWord){
        int i=0;
        boolean haveSuggestion=false;
        
        
        //compare the query word with all the words in dictionary
        for(String s : dictionary){
            i = computeDistance(s, queryWord);
            if(i<3){
                // Adjust i. The lesser, the more precise number of options
                newlist.put(s, i); // store a list of sugggest words which distance less than 3
                haveSuggestion=true;
            }
        }
        
        if(!haveSuggestion) return "null";

        
        entries = new ArrayList<Entry<String, Integer>>(newlist.entrySet());
        Collections.sort(entries, new Comparator<Entry<String, Integer>>() {
            public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2) {
                return e1.getValue().compareTo(e2.getValue());
            }
        });
    
               
        String suggestWord=entries.get(0).getKey();// just pick the first suggest word in queue
        System.out.println("suggest word:"+suggestWord);
        entries.clear();
        newlist.clear();
        
        return suggestWord;
        

       
        }
    


}