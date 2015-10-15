package edu.upenn.cis455.YASE.indexer;

import edu.upenn.cis455.YASE.Util;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class Stemmer {


    private static final Analyzer analyzer = new EnglishAnalyzer(Version.LUCENE_47);

    private static int lineCount = 0;
    private static int errorCount = 0;
    private static final long startTime = System.currentTimeMillis();


    public static void main(String[] args) throws IOException {

        //init
        final File inputFolder = new File(args[0]);
        final File outputFile = new File(args[1]);

        //check if output file already exists
        if (outputFile.exists()) {
            System.err.println("File already exists!");
            return;
        }

        FileWriter fileWriter = new FileWriter(outputFile.getName(), true);
        final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);


        File[] inputFiles = inputFolder.listFiles();

        //check input folder has files
        if (inputFiles != null) {

            //read every file in the folder
            for (File file : inputFiles) {
                BufferedReader br = new BufferedReader(new FileReader(file));

                //read every line of a file
                String line;
                while ((line = br.readLine()) != null) {

                    lineCount++;


                    //print progress every 1%
                    if (lineCount % 10000 == 0) {
                        printProgress();
                    }


                    //split line
                    String[] splitLine = line.split("\t");

                    //check format
                    if (splitLine.length < 4) {
//                        System.err.println("format error:" + line);
                        errorCount++;
                        continue;
                    }

                    //get parameters
                    String urlString = splitLine[0];
                    String titleString = splitLine[2];
                    String wordsString = splitLine[3];

                    //split urls
                    String urlKeywordsString = splitURLKeywords(urlString);

                    //stem all of them
                    String stemmedUrlKeywords = stem(urlKeywordsString, true);
                    String stemmedTitle = stem(titleString, true);
                    String stemmedWords = stem(wordsString, true);

                    //repeat url keywords and title multiple times
//                    String repeatUrlKeywords = repeatString(stemmedUrlKeywords, 5);
//                    String repeatTitles = repeatString(stemmedTitle, 5);

                    //put them all together
//                    String allWords = repeatUrlKeywords + " " + repeatTitles + " " + stemmedWords;
                    String allWords = stemmedUrlKeywords + " " + stemmedTitle + " " + stemmedWords;

                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(urlString).append(Util.uniqueSeparator).append(stemmedTitle).append(Util.uniqueSeparator).append(allWords).append("\n");


                    //write to file. Format: url \t word1 word2 word3 ....
//                    bufferedWriter.write(urlString + "\t" + allWords + "\n");
                    bufferedWriter.write(stringBuilder.toString());
                }
            }
        }


        printProgress();
        analyzer.close();
        bufferedWriter.close();


    }

    private static void printProgress() {
        double minutesElapsed = (double) (System.currentTimeMillis() - startTime) / 1000 / 60;
        System.out.println("line: " + lineCount + "; error: " + errorCount + "; minutes elapsed: " + minutesElapsed);
    }

    private static String stem(String words, boolean allowsPureNumber) throws IOException {
        TokenStream wordStream = analyzer.tokenStream(null, words);
        wordStream.reset();
        StringBuilder stringBuilder = new StringBuilder();
        while (wordStream.incrementToken()) {
            //get stemmed word
            String stemmedWord = wordStream.getAttribute(CharTermAttribute.class).toString();


            //discard words that don't have letters
            if (!allowsPureNumber && !hasLetter(stemmedWord)) {
                continue;
            }

            //append it to string builder
            stringBuilder.append(stemmedWord).append(" ");
        }
        wordStream.close();

        //trim it before return
        return stringBuilder.toString().trim();
    }

    private static boolean hasLetter(final CharSequence cs) {
        if (cs == null) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isLetter(cs.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private static String splitURLKeywords(String urlString) {

        String[] stringArray;
        try {
            //check valid url
            URL url = new URL(urlString);

            //get only the host part of url
            String hostString = url.getHost();

            //get rid of http:// and www
            hostString = hostString.replaceFirst("http://", "");
            if (hostString.startsWith("www.")) {
                hostString = hostString.substring(4);
            }

            //get rid of .com ,.org, etc.
            int index = hostString.lastIndexOf(".");
            hostString = hostString.substring(0, index);

            //split the keywords in url
            stringArray = hostString.split("\\.");
        } catch (MalformedURLException e) {

            //if invalid url, return null
            System.err.println("URL format error: " + urlString);
            return "";
        }

        //append all keywords into one string
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : stringArray) {
            stringBuilder.append(" ").append(s);
        }

        //trim the space before return
        return stringBuilder.toString().trim();
    }


    private static String repeatString(String s, int numRepeats) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < numRepeats; i++) {
            result.append(s).append(" ");
        }
        return result.toString().trim();
    }
}
