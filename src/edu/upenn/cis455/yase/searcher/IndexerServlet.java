package edu.upenn.cis455.YASE.searcher;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.upenn.cis455.YASE.database.YASEDatabase;

public class IndexerServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private YASEDatabase db;

	public void init(ServletConfig config){
		db = (YASEDatabase) config.getServletContext().getAttribute("db");
		if(db == null){
			try {
				db = new YASEDatabase(config.getInitParameter("bdbdir"));
				config.getServletContext().setAttribute("db", db);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		PrintWriter pw = resp.getWriter();

		if(pathInfo==null){
			pw.println("either be inverted index or forward index");
		}else if(pathInfo.startsWith("/inverted")){
			String word = req.getParameter("word");
//			System.out.println("word: " + word);
			// test data
//			try {
//				db.addWordIndexPair("hello", "http://www.hello.com\t2\t100\tthis is title@-!http://www.a.com\t4\t10\tthis is title");
//				db.addWordIndexPair("world", "http://www.hello.com/abc\t3\t120\tthis is title@-!http://www.google.com\t5\t100\tthis is title");
//				db.sync();
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}

			String docsString = null;
			try {
				docsString = db.getInvertedList(word);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(docsString!=null){
				String[] docsStrings = docsString.split("\t");
				for(String url: docsStrings){
					pw.println(url);
				}
			}else{
				System.out.println("Cannot find: " + word);
			}
		}else if(pathInfo.startsWith("/forward")){
			String url = req.getParameter("url");
			String[] keywords = req.getParameter("keywords").split(" ");
			String titleContent = null;
			try {
				titleContent = db.getURLContent(url);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// test input
//			try {
//				db.addURLContent(url, "123 \t title \t content1 content2 hello big like lile look big world world word4 word5");
//				db.sync();
//				
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			if(titleContent!=null){
//				System.out.println("titleContent: " + titleContent);
				String[] values = titleContent.split("\t");
				if(values.length>2){
					String preview = getPreviewFromContent(keywords,values[2]);
//					System.out.println("preview: " + preview);

					pw.println(values[0] + "\t" + values[1] + "\t" + preview);
				}else{
					System.out.println("title and content size error!");
				}
			}else{
				System.out.println("Can't find: " +url);
			}

		}
		pw.flush();
		pw.close();
	}
	
	private String getPreviewFromContent(String[] keywords, String content){
		if(content.length()>200){
		 return content.substring(0,200);
		}else{
			return content;
		}
	}
//	private String getPreviewFromContent(String[] keywords, String content){
//		String[] tokens = content.split(" ");
//		HashSet<String> set = new HashSet<String>();
////		System.out.println("content: " + content);
//		for(String keyword: keywords){
////			System.out.println("keyword: " + keyword);
//			set.add(keyword);
//		}
//		StringBuilder sb =new StringBuilder();
//		for(int i =0;i<tokens.length; i++){
//			if(set.contains(tokens[i])){
//				for(int j = i-3; j<i+3; j++){
//					if(j<0||j>=tokens.length){
//						continue;
//					}
//					if(set.contains(tokens[j])){
//						set.remove(tokens[j]);
//					}
//					sb.append(tokens[j]);
//					sb.append(" ");
//				}
//			}
//		}
//		return sb.toString();
//	}

}
