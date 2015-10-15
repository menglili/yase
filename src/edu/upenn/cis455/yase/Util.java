package edu.upenn.cis455.YASE;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {

    public static final String uniqueSeparator = "@-!";

    public static int getWorkerIndex(String key, int numBucket)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {

        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(key.getBytes());
        byte byteData[] = md.digest();
        return (byteData[0] + 128) % numBucket;

    }

    public static String insertSeparators(String[] strings, String separator) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : strings) {
            stringBuilder.append(separator).append(string);
        }
        stringBuilder.delete(0, separator.length());
        return stringBuilder.toString();
    }

}
