package edu.upenn.cis455.YASE.searcher;

import javax.servlet.ServletConfig;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 
 * @author SuperE
 *
 */
public class SearcherServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private String[] indexerAddressList;
	private HashMap<String, Float> idfMap;
	private HashMap<String, Float> prMap;
	private final int numberOfPagesToDisplay = 100;
	private final int priorityQueueSize = 3000;
	private Analyzer analyzer;

	public void init(ServletConfig config) {
		indexerAddressList = new String[5];
		indexerAddressList[0] = config.getInitParameter("indexer1");
		indexerAddressList[1] = config.getInitParameter("indexer2");
		indexerAddressList[2] = config.getInitParameter("indexer3");
		indexerAddressList[3] = config.getInitParameter("indexer4");
		indexerAddressList[4] = config.getInitParameter("indexer5");

		// load pagerank and idf file
		String idf_pr_dir = config.getInitParameter("idf_pr_dir");
		try {
			idfMap = getMapFromFile(idf_pr_dir + "/idf.txt");
			prMap = getMapFromFile(idf_pr_dir + "/pagerank.txt");
			System.out.println("Map load successfully!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		analyzer = new EnglishAnalyzer(Version.LUCENE_46);

	}

	public HashMap<String, Float> getMapFromFile(String filePath)
			throws IOException {
		File file = new File(filePath);
		HashMap<String, Float> map = new HashMap<String, Float>();

		BufferedReader bw = new BufferedReader(new FileReader(file));
		String line;

		while ((line = bw.readLine()) != null) {
			String[] values = line.split("\t");
			if (values.length != 2) {
				continue;
			}
			map.put(values[0], Float.valueOf(values[1]));
		}
		bw.close();
		return map;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String keywordsString = req.getParameter("s");
		PrintWriter pw = resp.getWriter();
		HashSet<String> KeyWordsHashSet = new HashSet<String>();
		String[] oldKeywords = keywordsString.split(" ");
		HashSet<String> untokenKeyWords = new HashSet<String>();

		for(String s:oldKeywords){
			untokenKeyWords.add(s.toLowerCase());
		}

		ArrayList<String> keywordsStrings = new ArrayList<String>();

		TokenStream stream = analyzer.tokenStream(null, keywordsString);
		stream.reset();
		while (stream.incrementToken()) {
			String wordString = stream.getAttribute(CharTermAttribute.class)
					.toString();
			keywordsStrings.add(wordString);
			KeyWordsHashSet.add(keywordsString);

		}
		stream.close();

		int numberOfKeyWords = keywordsStrings.size();

		final ArrayList<Float> query = new ArrayList<Float>();

		PriorityQueue<URLVector> pq = new PriorityQueue<URLVector>(
				priorityQueueSize, new Comparator<URLVector>() {
					public int compare(URLVector u1, URLVector u2) {
						// System.out.println(u1.getURL() + "getPR: " +
						// getPR(u1.getURL()));
						Float f1 = u1.cossimilarity(query) * getPR(u1.getURL());
						// Float f1 = u1.cossimilarity(query);

						// System.out.println(u2.getURL() + "getPR: " +
						// getPR(u2.getURL()));

						Float f2 = u2.cossimilarity(query) * getPR(u2.getURL());
						// Float f2 = u2.cossimilarity(query);

						if (f1 > f2) {
							return -1;
						} else if (f1 == f2) {
							return 0;
						} else {
							return 1;
						}
					}
				});

		ArrayList<LinkedList<String>> keywordsAndURLTF = new ArrayList<LinkedList<String>>();

		LinkedList<Thread> threadLinkedList = new LinkedList<Thread>();

		for (int i = 0; i < keywordsStrings.size(); i++) {
			LinkedList<String> currentList = new LinkedList<String>();
			keywordsAndURLTF.add(currentList);
			Thread t;
			try {
				t = new Thread(new GetURLByWordThread(keywordsStrings.get(i),
						getIndexerAddress(keywordsStrings.get(i),
								indexerAddressList.length), currentList),
						keywordsStrings.get(i));
				query.add(getIDF(keywordsStrings.get(i)));
				t.start();
				threadLinkedList.add(t);

			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		// add one dimension for length
		query.add(0f);

		for (Thread t : threadLinkedList) {
			try {
				t.join();
				System.out.println(t.getName() + " look up finished!");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		HashMap<String, URLVector> urlMap = new HashMap<String, URLVector>();
		for (int i = 0; i < keywordsAndURLTF.size(); i++) {
			
			LinkedList<String> l = keywordsAndURLTF.get(i);
			// System.out.println("l: " + l);
			for (String s : l) {
				
				
				String[] values = s.split("@-!");
				
				if (values.length != 4) {
//					System.out.println(s+ " Inverted index not 4! ");
					continue;
				}
				String urlsString = values[0];
				Float tf = Float.valueOf(values[1]);
				
				URLVector urlVector;
				if (urlMap.containsKey(urlsString)) {
					urlVector = urlMap.get(urlsString);
				} else {
					urlVector = new URLVector(urlsString, numberOfKeyWords+1);
					Float length = Float.valueOf(values[2]);
					String title = values[3];
					urlVector.setTitle(title);
					urlVector.setLengthVector(length);
					urlMap.put(urlsString, urlVector);
				}
				// System.out.println("keyword: " + values[0] + "i: " + i +
				// "values: "
				// +Float.valueOf(values[1])*getIDF(keywordsStrings[i]));

				urlVector.setVector(i,(tf * getIDF(keywordsStrings.get(i))));
			}
		}

		for (URLVector u : urlMap.values()) {
			String urlsString = u.getURL();
			
			stream = analyzer.tokenStream(null, urlsString+ " " + u.getTitle());
			stream.reset();
			while (stream.incrementToken()) {
				
				String wordString = stream
						.getAttribute(CharTermAttribute.class).toString();

				if (KeyWordsHashSet.contains(wordString)) {
					u.boost *= 1+ 10*getIDF(wordString);
					// System.out.println("url mathch!");
				}
			}
			String[] domains = urlsString.split("[\\./]");
				
			if(domains.length==5 &&domains[2].toLowerCase().equals("www") &&untokenKeyWords.contains(domains[3].toLowerCase())){
				u.boost *=10000;
//				System.out.println("homepage!");
			}
			
			stream.close();
			pq.add(u);
		}
		LinkedList<LinkedList<String>> result = new LinkedList<LinkedList<String>>();
		LinkedList<Thread> threadList = new LinkedList<Thread>();
		int i = 0;
		while (!pq.isEmpty()) {
			LinkedList<String> titleAndContent = new LinkedList<String>();
			String urlString = pq.poll().getURL();
			titleAndContent.add(urlString);
			result.add(titleAndContent);
			String indexerAddress = null;
			try {
				indexerAddress = getIndexerAddress(urlString, 5);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			Thread t = new Thread(new GetPreviewByURLThread(keywordsString,
					urlString, indexerAddress, titleAndContent), urlString);
			t.start();
			threadList.add(t);
			i++;
			if (i >= numberOfPagesToDisplay) {
				break;
			}
		}

		for (Thread t : threadList) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(t.getName()
					+ " GetPreviewByURLThread is finished!");
		}
		// System.out.println("Result: " + result);
		for (LinkedList<String> l : result) {
			if (l.size() < 4) {
				System.out.println("Size is: " + l.size());
				continue;
			} else {
				// System.out.println("FINAL: " + l);
				pw.println(l.get(0) + "\t" + l.get(1) + "\t" + l.get(2) + "\t"
						+ l.get(3));
			}
		}
		pw.flush();
		pw.close();
	}

	private Float getIDF(String word) {
		if (idfMap.containsKey(word)) {
			return idfMap.get(word);
		} else {
			return 10f;
		}
	}

	private Float getPR(String url) {
		String domain;
		try {
			domain = getDomainAndExtension(new URL(url).getHost());

			if (prMap.containsKey(domain)) {
				return prMap.get(domain);
			} else {
				return 1f;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 1f;
		}
	}

	private String getDomainAndExtension(String url) {

		String[] values = url.split("\\.");
		int len = values.length;
		if (len < 2) {
			return null;
		} else if (values[len - 2].equals("")) {
			return null;
		}
		return (values[len - 2] + "." + values[len - 1]).toLowerCase();

	}

	private String getTopDomainName(String url) {

		String domain = null;
		try {
			domain = getDomainAndExtension(new URL(url).getHost());
		} catch (MalformedURLException e) {
			return null;
		}
		String[] valuesStrings = domain.split("\\.");
		if (valuesStrings.length == 2) {
			return valuesStrings[0];
		} else {
			return null;
		}

	}

	private String getIndexerAddress(String s, int numIndexer)
			throws NoSuchAlgorithmException {

		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(s.getBytes());
		byte byteData[] = md.digest();
		int indexID = (byteData[0] + 128) % numIndexer;
		// System.out.println("keywordString: " + keywordString + " indexID: " +
		// indexID);
		return indexerAddressList[indexID];

	}

}
