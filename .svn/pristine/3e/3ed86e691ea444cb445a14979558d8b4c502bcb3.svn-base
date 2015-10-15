package edu.upenn.cis455.YASE.test;

import java.io.*;


public class TestMaker {
    public static void main(String[] args) throws IOException {
        String inputFileString = args[0];
        String outputFileString = args[1];
        int numLinesToRead = Integer.parseInt(args[2]);

        File inputFile = new File(inputFileString);
        File outputFile = new File(outputFileString);
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        FileWriter fileWriter = new FileWriter(outputFile.getName(), true);
        final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        int lineCount = 0;
        String line;
        while (lineCount < numLinesToRead && (line = br.readLine()) != null) {


            bufferedWriter.write(line + "\n");

            lineCount++;
        }

        bufferedWriter.close();

    }
}
