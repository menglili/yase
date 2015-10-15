package edu.upenn.cis455.YASE.crawler;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class ContentExtractor {
	private Document htmlocument;
	public ContentExtractor(Document htmlocument){
		this.htmlocument = htmlocument;
	}
	public String getTitle(){
		Element title = htmlocument.select("title").first();
		if(title!=null){
			return title.ownText().replaceAll("[\\n\\r]", " ");
		}else{
			return "No Title";
		}
	}
	public String getContent() {
		Element body = htmlocument.body();
		if(body!=null){
			String htmlText = body.text().replaceAll("[\\n\\r]", " ");
			return htmlText;
		
		}else{
			return "";
		}
		//System.out.println(htmlText);
		
	}
}
