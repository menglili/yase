package edu.upenn.cis455.YASE.storage;

import edu.upenn.cis455.YASE.Util;
import edu.upenn.cis455.YASE.database.YASEDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;

public class IndexSplitter {

    private static int numWorkers = 5;
    private static String inputFolderString;
    private static String outputFolderString;
    private static String idfFileString;
    private static HashMap<String, YASEDatabase> outputFolders = new HashMap<>();
    private static int lineCount = 0;
    private static int errorCount = 0;
    private static final long startTime = System.currentTimeMillis();
    private static HashSet<String> idfWords = new HashSet<>();

    public static void main(String[] args) throws Exception {

        inputFolderString = args[0];
        outputFolderString = args[1] + "/";
        idfFileString = args[2];


        //load idf file into memory
        loadIDFFile(idfFileString);

        //initialize all DBs
        for (int i = 0; i < numWorkers; i++) {
            String dbNumber = outputFolderString + String.format("%s", i);
            YASEDatabase db = new YASEDatabase(dbNumber);
            outputFolders.put(dbNumber, db);
        }

        //read every file in the input folder
        File inputFolder = new File(inputFolderString);
        File[] inputFiles = inputFolder.listFiles();
        if (inputFiles != null) {
            for (File file : inputFiles) {
                Path path = Paths.get(file.getAbsolutePath());
                BufferedReader br = Files.newBufferedReader(path, Charset.defaultCharset());
                String line;

                //read every line of a file
                while ((line = br.readLine()) != null) {

                    lineCount++;

                    if (lineCount % 50000 == 0) {

                        printProgress();

                        //sync DB
                        for (YASEDatabase db : outputFolders.values()) {
                            db.sync();
                        }
                    }

                    String[] splitLine = line.split("\\t");

                    //check format
                    if (splitLine.length < 2) {
//                        System.err.println("format error: " + line);
                        errorCount++;
                        continue;
                    }

                    //get parameters
                    String word = splitLine[0];
                    String urls = line.substring(word.length() + 1);

                    //discard the keyword if idf file does not contain it
                    if (!idfWords.contains(word)) {
                        errorCount++;
                        continue;
                    }

                    //store to DB
                    storeToDB(word, urls);

                }
                br.close();
            }

            //close all DBs when done
            for (YASEDatabase yaseDatabase : outputFolders.values()) {
                yaseDatabase.shut();
            }
        }
    }

    private static void loadIDFFile(String idfFileString) throws IOException {
        File idfFile = new File(idfFileString);

        //read every line
        BufferedReader br = new BufferedReader(new FileReader(idfFile));
        String line;
        while ((line = br.readLine()) != null) {
            String[] lineSplit = line.split("\\t");
            idfWords.add(lineSplit[0]);
        }
        br.close();

    }

    private static void storeToDB(String key, String value) throws Exception {
        int workerIndex = Util.getWorkerIndex(key, numWorkers);

        //write to DB file
        String dbNumber = outputFolderString + String.format("%s", workerIndex);

        outputFolders.get(dbNumber).addWordIndexPair(key, value);
    }


    private static void printProgress() {
        double minutesElapsed = (double) (System.currentTimeMillis() - startTime) / 1000 / 60;
        System.out.println("line: " + lineCount + "; error: " + errorCount + "; minutes elapsed: " + minutesElapsed);
    }
}
