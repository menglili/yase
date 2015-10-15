package edu.upenn.cis455.YASE.database;

import java.io.*;

public class IDFCleaner {

    public static void main(String[] args) throws Exception {

        File outputFile = new File("sortedIDF_cleaned.txt");
        File inputFile = new File("sortedIDF.txt");

        FileWriter fileWriter = new FileWriter(outputFile.getName(), true);
        BufferedWriter BufferWriter = new BufferedWriter(fileWriter);

        if (!outputFile.exists()) {
            if (!outputFile.createNewFile()) {
                System.err.println("Error: cannot create file!");
            }
        }


        //read every line
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        String line;
        while ((line = br.readLine()) != null) {

            String[] splitLine = line.split("\\t");

            //check format
            if (splitLine.length != 2) {
                System.err.println("format error:" + line);
                continue;
            }

            if (!hasAlpha(splitLine[0])) {
                continue;
            }

            BufferWriter.write(line + "\n");

        }
        BufferWriter.close();
    }

    private static boolean hasAlpha(final CharSequence cs) {
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
}