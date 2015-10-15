package edu.upenn.cis455.YASE.storage;

import edu.upenn.cis455.YASE.Util;
import edu.upenn.cis455.YASE.database.YASEDatabase;

public class ContentDBTester {
    public static void main(String[] args) throws Exception {

        String string = "good";
        int dbIndex = Util.getWorkerIndex(string, 5);


        String dbLocation = "DB/" + dbIndex;
        System.out.println("dbLocation = " + dbLocation);
        YASEDatabase yaseDatabase = new YASEDatabase(dbLocation);

        //store to DB
//        yaseDatabase.addURLContent(string, "test");
//        yaseDatabase.addWordIndexPair(string, "invertIndex");


//        String content = yaseDatabase.getURLContent(string);
//        System.out.println("content = " + content);
        String invertIndex = yaseDatabase.getInvertedList(string);
        System.out.println("invertIndex = " + invertIndex);

        yaseDatabase.shut();
    }
}
