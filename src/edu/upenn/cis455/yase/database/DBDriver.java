package edu.upenn.cis455.YASE.database;

public class DBDriver {
    public static void main(String[] args) throws Exception {

        String dblocation = "DB";
        String inputFolderString = "proutput";
        YASEDatabase yaseDatabase = new YASEDatabase(dblocation);

//	        File inputFolder = new File(inputFolderString);
//
//	        File[] inputFiles = inputFolder.listFiles();
//	        if (inputFiles != null) {
//	            for (File file : inputFiles) {
//	                BufferedReader br = new BufferedReader(new FileReader(file));
//	                String line;
//	                while ((line = br.readLine()) != null) {
//	                    System.out.println("line: " + line);
//	                    String[] splitLine = line.split("\t");
//	                    String page = splitLine[0];
//	                    String score = splitLine[1];
//	                    yaseDatabase.addWordIndexPair(page, score);
//	                }
//	                br.close();
//	            }
//	        }

        yaseDatabase.addWordIndexPair("penn", "www.upenn.edu");

        yaseDatabase.sync();
        yaseDatabase.shut();
    }
}
