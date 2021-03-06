package edu.upenn.cis455.YASE.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

public class IDFCaculator {
	public static void main(String[] args) throws IOException {

		if (args.length != 2) {
			System.err.println("Usage: inputdirPath outputfile");
			System.exit(0);
		}
		String dirPath = args[0];
		String outputPath = args[1];
		File dir = new File(dirPath);
		int totalpages = 0;

		Analyzer analyzer = new EnglishAnalyzer(Version.LUCENE_46);
		HashMap<String, Float> idfMap = new HashMap<String, Float>();
		File res = new File(outputPath);
		BufferedWriter resbw = new BufferedWriter(new FileWriter(res));
		int Errcount = 0;
		String line;
		String[] values;
		for (File f : dir.listFiles()) {
			BufferedReader bw = new BufferedReader(new FileReader(f));
			
			while ((line = bw.readLine()) != null) {
				totalpages++;
				if(totalpages%10000==0){
					System.out.println(totalpages);
				}
				values = line.split("\t");
				if (values.length < 4) {
					Errcount++;
					continue;
				}
				TokenStream stream = analyzer.tokenStream(null, values[3]);
				stream.reset();
				HashSet<String> set = new HashSet<String>();
				while (stream.incrementToken()) {
					String wordString = stream.getAttribute(
							CharTermAttribute.class).toString();
					set.add(wordString);

				}
				stream.close();
				for (String s : set) {
					if (idfMap.containsKey(s)) {
						idfMap.put(s, idfMap.get(s) + 1);
					} else {
						idfMap.put(s, 1f);
					}
					//System.out.println(s + ":"+idfMap.get(s));
				}
				set.clear();
			}
			bw.close();
		}
		
		for(String s: idfMap.keySet()){
			Float count = idfMap.get(s);
			if(count<5){
				continue;
			}
			float idf = (float) (1+Math.log(totalpages/(count+1)));
			resbw.write(s + "\t" + idf);
			resbw.write(System.lineSeparator());
		}
		System.err.println("Not 4 columns: " + Errcount);

		resbw.close();
		analyzer.close();
	}
}
