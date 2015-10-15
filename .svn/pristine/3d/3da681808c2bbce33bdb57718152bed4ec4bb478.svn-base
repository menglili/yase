package edu.upenn.cis455.YASE.storage;

import edu.upenn.cis455.YASE.Util;
import edu.upenn.cis455.YASE.database.YASEDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class ContentDBSplitter {

    private static int numWorkers = 5;
    private static String inputFolderString;
    private static String outputFolderString;

    private static HashMap<String, YASEDatabase> outputFolders = new HashMap<>();
    private static int lineCount = 0;
    private static int errorCount = 0;
    private static long startTime = System.currentTimeMillis();


    public static void main(String[] args) throws Exception {
        inputFolderString = args[0];
        outputFolderString = args[1] + "/";

        //create DB objects
        for (int i = 0; i < numWorkers; i++) {
            String dbNumber = outputFolderString + String.format("%s", i);
            YASEDatabase db = new YASEDatabase(dbNumber);
            outputFolders.put(dbNumber, db);
        }

        //read every file in the folder
        File inputFolder = new File(inputFolderString);
        File[] inputFiles = inputFolder.listFiles();
        if (inputFiles != null) {
            for (File file : inputFiles) {
                Path path = Paths.get(file.getAbsolutePath());
                BufferedReader br = Files.newBufferedReader(path, Charset.defaultCharset());
                String line;

                //read every line in the file
                while ((line = br.readLine()) != null) {

                    lineCount++;

                    //for every 1% of lines
                    if (lineCount % 10000 == 0) {
                        //print progress
                        printProgress();
                        //sync db
                        for (YASEDatabase db : outputFolders.values()) {
                            db.sync();
                        }
                    }

                    String[] splitLine = line.split("\\t");

                    //check format
                    if (splitLine.length < 4) {
//                        System.err.println("format error: " + line);
                        errorCount++;
                        continue;
                    }

                    //read parameters
                    String url = splitLine[0];
                    String content = line.substring(url.length() + 1);

                    //store to DB
                    storeToContentDB(url, content);

                }


                br.close();
            }

            //close all DBs when done
            for (YASEDatabase yaseDatabase : outputFolders.values()) {
                yaseDatabase.shut();
            }
        }
    }


    private static void storeToContentDB(String key, String value) throws Exception {

        //get db index
        int workerIndex = Util.getWorkerIndex(key, numWorkers);

        //write to DB file
        String dbNumber = outputFolderString + workerIndex;
        outputFolders.get(dbNumber).addURLContent(key, value);
    }

    private static void printProgress() {
        double minutesElapsed = (double) (System.currentTimeMillis() - startTime) / 1000 / 60;
        System.out.println("line: " + lineCount + "; error: " + errorCount + "; minutes elapsed: " + minutesElapsed);
    }


}
