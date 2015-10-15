package edu.upenn.cis455.YASE.recycle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


public class Master extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private ArrayList<String> workers;

    public void init() {

        workers = new ArrayList<String>();
        System.out.println("Master up and running!");


    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       //doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        System.out.println("master received request: " + requestURI);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><head><title>Master</title></head>");
        out.println("<body>Hi, I am the master!</body></html>");

        if (requestURI.equalsIgnoreCase("/master/join")) {
            handleJoin(request);

        }
        if (requestURI.equalsIgnoreCase("/master/search")) {
            handleSearch(request);
        }
        if (requestURI.equalsIgnoreCase("/master/push")) {
            handlePush(request);
        }
        if (requestURI.equalsIgnoreCase("/master/testget")) {
            testGet(request);
        }


    }

    private void testGet(HttpServletRequest request) {

//        sendGet(request.getParameter("key"));
        String workerURL = workers.get(0);
        String[] keys = (String[]) request.getParameterMap().get("key");
        System.out.println("testGet sent: " + Arrays.toString(keys));

        sendGet(keys, workerURL);
    }


    private void handlePush(HttpServletRequest request) {
        Map keyValuePairs = request.getParameterMap();

        System.out.println(keyValuePairs.toString());

        //TODO handle keyValuePairs
    }

    //TODO
    private void handleSearch(HttpServletRequest request) {
        String query = request.getParameter("q");
//        sendGet(query);

    }

    private void sendGet(String[] keys, String workerURL) {
        String parameters = String.format("db=%s", "DB");
        for (String key : keys) {
            parameters += String.format("&key=%s", key);
        }
        workerURL = "http://" + workerURL + "/worker/get?" + parameters;
        try {
            URL url = new URL(workerURL);
            HttpURLConnection connection;

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("charset", "utf-8");
            connection.getResponseCode();
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleJoin(HttpServletRequest request) {
        String remoteAddress = request.getRemoteAddr();
        //IPv6
        if (!remoteAddress.contains(".")) {
            remoteAddress = "[" + remoteAddress + "]";
        }
        String port = request.getParameter("port");
        String addressPort = remoteAddress + ":" + port;

        if (!workers.contains(addressPort)) {
            workers.add(addressPort);
            System.out.println("worker added: " + addressPort);
        }
    }

    private static int getWorkerIndex(String key, int numBucket)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {

        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(key.getBytes());
        byte byteData[] = md.digest();
        return (byteData[0] + 128) % numBucket;

    }
}
