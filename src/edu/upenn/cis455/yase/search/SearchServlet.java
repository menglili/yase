package edu.upenn.cis455.YASE.search;


import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;


public class SearchServlet extends HttpServlet {

	static final long serialVersionUID = 455555002;
	//private static String DICTIONARY_PATH="/home/ec2-user/jetty/yase/webapps/dictionary.txt";
	private static String DICTIONARY_PATH="/home/cis455/workspace/YASE/resource/dictionary.txt";
	public static String searchContent;
	public static boolean ifInitialized;
	public static String masterIP="54.86.147.206";
	public static int masterPort=8080;
	public static int numResults;
	
	public static long startTime = 0;
	public static long endTime = 0;
	public static long totalTime = 0;
	
	
	/**
	 * initialize servlet, got master ip and port from web.xml
	 */
	public void init(ServletConfig config){
		String[] masterIPandPort = config.getInitParameter("master").split(":");
		masterIP = masterIPandPort[0];		
		masterPort = Integer.valueOf(masterIPandPort[1]);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
	   throws java.io.IOException, ServletException
	{
		String suggestion="";
		System.out.println("connect to master: " + masterIP + " " + masterPort);
		//String path = request.getRequestURI().toLowerCase().trim(); //get path
		String path=request.getServletPath().toLowerCase().trim();
		System.out.println("path: "+path);
		response.setContentType("text/html");
	    response.setCharacterEncoding("UTF-8");    
	    PrintWriter out = response.getWriter();
	    boolean nullQuery=false;
	   
		if(path.startsWith("/searchquery")) {
			String query = request.getQueryString().trim();
			if(query.equals("") || query==null) return;
			System.out.println("**query is " + query);
			
			
			//get keywords
			String searchContent = request.getParameter("search_content");
			if(searchContent.equals("")) return;
			//get page
			String p = request.getParameter("page");
			int page;
			if(p == null)
				page = 0;
			else
				page = Integer.parseInt(p);
			
			//spell check
			boolean isSuggested=false;
			String splitSearch[] = searchContent.split(" ");
			SpellCheck sc=new SpellCheck(DICTIONARY_PATH);
			
			for(int i=0;i<splitSearch.length;i++){
				System.out.println("orginal: "+splitSearch[i]);
				if(sc.isCorrect(splitSearch[i])){
					suggestion=suggestion+splitSearch[i]+" ";
					System.out.println("correct! no suggestion");
				}
				else{
					String correctWord=sc.wordSuggester(splitSearch[i]);
					isSuggested=true;
					if(correctWord.equals("null")) suggestion=suggestion+splitSearch[i]+" ";
					else suggestion=suggestion+correctWord+" ";
					System.out.println("suggestion: "+suggestion);
					
				}
			}
			String suggestQuery=suggestion.replace(' ','+');
			if(suggestQuery.endsWith("+")) suggestQuery=suggestQuery.substring(0,suggestQuery.length()-1);
			String suggestURL = "/searchquery?search_content=" + suggestQuery;	
			
			
			
			String keys=searchContent.replace(' ', '+');
		    System.out.println("keys : " + keys);
		    
		    File file = new File(keys);
		    
		    
		    
		    //write cache
			if (!file.exists()) {
				System.out.println("create new file!");
				file.createNewFile();
				//contact master and wait for its result; write to this file
				startTime = System.currentTimeMillis(); //start time!
				System.out.println("start time " + startTime);
				
				 //contact with master using the query string
			    String url = "http://" + masterIP + ":" + masterPort + "/searcherservlet?s=" + keys;
			    System.out.println("url " + url);
				
				URL obj = new URL(url);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
				con.setRequestMethod("GET");
				int responseCode = con.getResponseCode();
				
				System.out.println("\nSending 'GET' request to URL : " + url);
				System.out.println("Response Code : " + responseCode);
		   
				numResults = 0;
				if(responseCode == 200) {
					BufferedReader in = new BufferedReader(
					        new InputStreamReader(con.getInputStream()));
					String inputLine;
					
		        	//write to file
					FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
					BufferedWriter bw = new BufferedWriter(fw);
					
					while ((inputLine = in.readLine()) != null) {
						
						if(inputLine.split("\t").length != 4) {
							System.err.println("wrong format");
							continue;
						}
						String splits[]=inputLine.split("	");
						if(splits[2].contains("???")) continue;
						if(splits[2].equals("")) inputLine=splits[0]+"	"+splits[1]+"	UNTITLED	"+splits[3];
						
						bw.write(inputLine);
						bw.write(System.lineSeparator());
						numResults++;
					}
					bw.flush();
		        	bw.close();
					in.close();	
				}
				else {
					System.err.println("response code is not 200");
				}	
				
				endTime = System.currentTimeMillis(); //end time!    
	            totalTime = endTime - startTime;
	            System.out.println("total time is : " + totalTime);
			}
		
			
			//get result now of exist result (read from cache)
			LineNumberReader  lnr = new LineNumberReader(new FileReader(new File(keys)));
			lnr.skip(Long.MAX_VALUE);
			System.out.println(lnr.getLineNumber());
			numResults=lnr.getLineNumber();
			// Finally, the LineNumberReader object should be closed to prevent resource leak
			lnr.close();
			
			out.println("<!DOCTYPE html>");
			out.println("<html><head>");
			out.println("<meta charset=\"utf-8\"/>");				
			out.println("<title>Result</title>");
			out.println("<meta name=\"keywords\" content=\"\" />");
			out.println("<meta name=\"description\" content=\"\" />");
			out.println("<link href=\"style.css\" rel=\"stylesheet\">");
			out.println("<link href=\"http://ajax.googleapis.com/ajax/libs/jqueryui/1/themes/ui-lightness/jquery-ui.css\" type=\"text/css\" rel=\"stylesheet\"/>");
			out.println("<link rel=\"stylesheet\" href=\"http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css\"/>");
			out.println("<style>");
			out.println("input{font-size:1.3em;}");
			out.println("#log{position: absolute; top: 10px; right: 10px;}");
			out.println("span{color:blue; text-decoration: underline; cursor: pointer;}");
		    out.println(".search_field { display: inline-block;border: 1px inset #ccc;width: 1000px;}");
		    out.println(".search_field input {width: 888px;border: none;padding: 0;}");
		    out.println(".search_field button {border: none;background: blue;}");
			out.println("</style>");
			
			out.println("</head>");
			
			out.println("<body>");
			out.println("<div id=\"inputs\"></div>");
			out.println("<script src=\"http://ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js\" type=\"text/javascript\"></script>");
			out.println("<script src=\"http://ajax.googleapis.com/ajax/libs/jqueryui/1/jquery-ui.min.js\" type=\"text/javascript\"></script>");
			out.println("<script src=\"jquery.googleSuggest.js\" type=\"text/javascript\"></script>");
			out.println("<div class=\"wrapper\">");
			out.println("<header class=\"header\">");	
			out.println("<div class=\"uk-width-medium-2-2\">");          
			out.println("<a href=\"search_interface.html\"><img class=\"uk-margin-bottom\" width=\"300\" height=\"200\" src=\"title.png\" alt=\"\"></a>");
			out.println("<form class=\"uk-form uk-form-stacked\" action=\"/searchquery\" method=\"get\">");
			//out.println("<div class=\"search_field\">");
			
			out.println("<div id=\"searchInput\">");			
			out.println("<input id=\"searchBox\" class=\"uk-width-medium-2-3\" type=\"text\" value=\""+searchContent+"\" placeholder=\"Search Content...\" name=\"search_content\">");		   	
			out.println("<button id=\"searchButton\" class=\"uk-button uk-button-primary\" type=\"submit\">YASE search</button>");
			out.println("</div>");
			
			
			
			out.println("<script>");
			out.println("$.each(\"web\".split(\" \"), function(i, v){var searchdiv = $(\"<div>\").appendTo(\"#searchInput\"), input = $('#searchBox').appendTo(searchdiv), searchBtn= $('#searchButton').appendTo(searchdiv);");
			out.println("input.googleSuggest({ service: v });");			
			out.println("});");				
			out.println("</script>");

			out.println("</form>");        	
			out.println("</div>");
			out.println("<p>");
			out.println("</p>");
			out.println("</header>");
			
			out.println("<div class=\"middle\">");
			out.println("<div class=\"container\">");
			out.println("<main class=\"content\">");

				
		 	out.println("<div class=\"uk-width-medium-4-5\">");
			out.println("<div class=\"uk-panel uk-panel-header\">");
			out.println("<h3 class=\"uk-panel-title\">About " + numResults + " results (" + totalTime + " ms) </h3>");
			//out.println("<p class=\"each-result\">");
			if(isSuggested){
				out.println("<div class=\"uk-form-row\">");
	        	out.println("<h3><a class=\"result-suggest\" href=\"" + suggestURL + "\">");
	            out.println("Did you mean: "+suggestion+" ?");
	            out.println("</a></h3></div>");
			}
            out.println("</div>");
            suggestion="";
            isSuggested=false;
            
            
            if(numResults!=0 && !nullQuery){
			
	            
	            System.out.println(file.getName());
	            BufferedReader br = new BufferedReader(new FileReader(file));
	            String line;
	            String values[];
	            int lineNo = 1;
	            
	            while((line = br.readLine()) != null){
	            	//read from line page to line page + 10
	            	if(lineNo <= page){
	            		lineNo++;
	            		continue;
	            	} 
	            	else if (lineNo > page + 10){
	            		break;
	            	}
	            	lineNo++;
	            	
	            	values = line.split("\t");
	            	String URL = values[0];
	            	String time = values[1];
	            	Date date = new Date(Long.parseLong(time));
	            	String title = values[2];
	            	String preview =values[3];
	            	
	            	//title
	            	out.println("<p class=\"each-result\">");
	            	out.println("<h2>");
	            	out.println("<a class =\"result-title\" href=\"" + URL + "\">");
	                out.println(title);
	                out.println("</a>");
	                out.println("</h2>");
	                
	                //link and preview
	                out.println("<div class=\"uk-form-row\">");
	                out.println("<p class=\"result-link\">");
	                out.println(URL);
	                out.println("</p>");
	                out.println("<p class=\"result-date\">");
	                out.println(date.toString());
	                out.println("</p>");
	                
	                //check if it is a youtube link
	                /*
	                if(getDomainName(URL).equals("youtube.com")){
	                	out.println("<iframe width=\"200\" height=\"150\" src=\""+URL+"\"></iframe>");
	                }
	                */
	                
	                out.println("<p class=\"result-info\">");
	                out.println(preview + "...");
	                out.println("</p>");
	                out.println("</p>");
	                out.println("</div>");
	                out.println("<p></p>");
	            }
	            
	            
	            boolean firstPage=false;
	            //handle page number 
	            if(page ==0 && numResults>0 && numResults<=10){
	            	out.println("<p class=\"each-result\">");
	            	out.println("<div class=\"uk-width-medium-4-5\">");
	                out.println("<ul class=\"uk-pagination\">");
	                out.println("<li class=\"uk-disabled\"><span> << </span></li>");
	                out.println("<li class=\"uk-disabled\"><span> >> </span></li>");
	                out.println("</ul>");
	                out.println("</div>");
	                out.println("</p>");
	                firstPage=true;
	            }
	            else if(page == 0) {
	            	out.println("<p class=\"each-result\">");
	            	out.println("<div class=\"uk-width-medium-4-5\">");
	                out.println("<ul class=\"uk-pagination\">");
	                out.println("<li class=\"uk-disabled\"><span> << </span></li>");
	                page += 10;
	                out.println("<li><a href=searchquery?search_content=" + keys + "&page=" + page + "><span> >> </span></a></li>");
	                out.println("</ul>");
	                out.println("</div>");
	                out.println("</p>");
	                firstPage=true;
	            }
	            else if (page == (Math.ceil(1.0 * numResults / 10) - 1) * 10) {
	            	out.println("<p class=\"each-result\">");
	            	out.println("<div class=\"uk-width-medium-4-5\">");
	                out.println("<ul class=\"uk-pagination\">");
	                out.println("<li><a href=searchquery?search_content=" + keys + "&page=" + (page-10) + "><span> << </span></a></li>");
	                out.println("<li class=\"uk-disabled\"><span> >> </span></li>");
	                out.println("</ul>");
	                out.println("</div>");
	                out.println("</p>");       
	            }
	            else {
	            	out.println("<p class=\"each-result\">");
	            	out.println("<div class=\"uk-width-medium-4-5\">");
	                out.println("<ul class=\"uk-pagination\">");
	                out.println("<li><a href=searchquery?search_content=" + keys + "&page=" + (page-10) + "><span> << </span></a></li>");
	                out.println("<li><a href=searchquery?search_content=" + keys + "&page=" + (page+10) + "><span> >> </span></a></li>");
	                out.println("</ul>");
	                out.println("</div>");
	                out.println("</p>");
	            } 
	            
	            out.println("</main>");
	            out.println("</div>");
	           
	            
	            if(firstPage){
	            	//weather
	            	out.println("<aside class=\"right-sidebar\">");
	            	out.println("<p class=\"sidebar-content\">");
	            	out.println("<a href=\"http://www.accuweather.com/en/us/philadelphia-pa/19107/current-weather/350540\" class=\"aw-widget-legal\">");
	            	out.println("</a><div id=\"awcc1399269024108\" class=\"aw-widget-current\"  data-locationkey=\"350540\" data-unit=\"f\" data-language=\"en-us\" data-useip=\"false\" data-uid=\"awcc1399269024108\"></div>");
	            	out.println("<script type=\"text/javascript\" src=\"http://oap.accuweather.com/launch.js\"></script>");
		            out.println("</p>");
	            	//amazon result
		            out.println("<p class=\"sidebar-content\">");
		            out.println("<script charset=\"utf-8\" type=\"text/javascript\">");
					out.println("amzn_assoc_ad_type = \"responsive_search_widget\"");
					out.println("amzn_assoc_tracking_id = \"widgetsamazon-20\"");
					out.println("amzn_assoc_link_id = \"K3SRW46FGA7TUAO4\"");
					out.println("amzn_assoc_marketplace = \"amazon\"");
					out.println("amzn_assoc_region = \"US\"");
					out.println("amzn_assoc_placement = \"\"");
					out.println("amzn_assoc_search_type = \"search_widget\"");
					out.println("amzn_assoc_width = \"auto\"");
					out.println("amzn_assoc_height = \"auto\"");
					out.println("amzn_assoc_default_search_category = \"\"");
					out.println("amzn_assoc_default_search_key = \""+searchContent+"\"");
					out.println("amzn_assoc_theme = \"light\"");
					out.println("amzn_assoc_bg_color = \"FFFFFF\"");
					out.println("</script>");
					out.println("<script src=\"//z-na.amazon-adsystem.com/widgets/q?ServiceVersion=20070822&Operation=GetScript&ID=OneJS&WS=1&MarketPlace=US\"></script>");
					out.println("</p>");
					
					//ebay result
					out.println("<p class=\"sidebar-content\">");
					out.println("<object width=\"355\" height=\"300\">");
					out.println("<param name=\"movie\" value=\"http://togo.ebay.com/togo/togo.swf?2008013100\" />");
					out.println("<param name=\"flashvars\" value=\"base=http://togo.ebay.com/togo/&lang=en-us&mode=search&query="+searchContent+"\" />");
					out.println("<embed src=\"http://togo.ebay.com/togo/togo.swf?2008013100\" type=\"application/x-shockwave-flash\" width=\"355\" height=\"300\" flashvars=\"base=http://togo.ebay.com/togo/&lang=en-us&mode=search&query="+searchContent+"\">");
					out.println("</embed>");
					out.println("</object>");
					out.println("</p>");
					out.println("</aside>");
	            }
			
            }else{
            	out.println("<div class=\"uk-form-row\">");
	        	out.println("<h3><p class=\"result-suggest\">");
	            out.println("Sorry! No matching results! Please try again!");
	            out.println("</p></h3></div>");
            }
			
            out.println("</div>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
            
        }	
		
		
		return;
	}
	
	
}
  
