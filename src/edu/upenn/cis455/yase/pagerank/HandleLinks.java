package edu.upenn.cis455.YASE.pagerank;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Handle links.txt from crawler
 * 1. merge the same source domain
 * 2. remove duplicate destination links
 * 3. remove sink nodes (without outlinks)
 * 4. handle uncrawled links
 * @author Mengli Li
 *
 */
public class HandleLinks {
	
	private static PrintWriter writer;

	/**merge duplicate source domain and 
	 * remove duplicate domain in destination links for each source link
	 * @param filePath -- the path of link.txt which is got from crawler
	 * @param newFilePath -- the path of new file after convert 
	 * @throws IOException
	 */
	public static void mergeAndRemoveDup(String filePath,String newFilePath) throws IOException{
		
		Process exec = Runtime.getRuntime().exec("sort "+filePath);
		BufferedReader br=new BufferedReader(new InputStreamReader(exec.getInputStream()));
		
		//create convertFile
		File file=new File(newFilePath);
		if(!file.exists()) file.createNewFile();
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		
		  	  
		String line="";
		String source="";
		StringBuffer sb=new StringBuffer();
		int no=0;
		int writeLine=0;
		Set<String> linkSet=new HashSet<String>();
		
		while((line=br.readLine())!=null){
			no++;
			String[] splits=line.split("	");
			if(splits.length<=2) {
				//System.out.println("****");
				continue;
			}
			
			if(source.equals("")) source=splits[0];
			if(splits[0].equals(source)){
				for(int i=2;i<splits.length;i++){
					linkSet.add(splits[i]);
				}
				source=splits[0];
			}else{

				Iterator iter=linkSet.iterator();
				bw.write(source+"	"+"1.0	");
				while(iter.hasNext()){
					//sb.append(iter.next()+"	");
					String next=(String) iter.next();
					if(!next.equals(source)) bw.write(next+"	");
				}			
				//writer.println(mergeLine);
				writeLine++;
				//bw.write(sb+"\n");
				bw.write("\n");
				//bw.write(sb.toString()+"\n");
				System.out.println("write:"+writeLine);
				
				linkSet=new HashSet<String>();
				//linkList=new LinkedList<String>();
				for(int i=2;i<splits.length;i++){
					linkSet.add(splits[i]);
					//linkList.add(splits[i]);
				}
				source=splits[0];
			}		
		}
		br.close();
		Iterator iter=linkSet.iterator();
		//Iterator iter=linkList.iterator();
		bw.write(source+"	"+"1.0	");
		//sb.append(source+"	"+"1.0	");
		//mergeLine=source+"	"+"1.0	";
		while(iter.hasNext()){
			String next=(String) iter.next();
			if(!next.equals(source)) bw.write(next+"	");
			//sb.append(iter.next()+"	");
			//mergeLine=mergeLine+iter.next()+"	";
		}			
		//writer.println(mergeLine);
		//bw.write(sb.toString()+"\n");
		bw.write("\n");
		//bw.write(mergeLine+"\n");		
		bw.close();
		
	}
	
	/**
	 * remove sink nodes (which didn't have any outlinks)
	 * @param newFilePath -- the path of the file after merge and remove from function mergeAndRemoveDup()
	 * @param convertFilePath -- the path of the file after handling sink nodes
	 * @throws IOException
	 */
	
	public static void removeSink(String newFilePath,String convertFilePath) throws IOException{
		//File file=new File(newFilePath);
		BufferedReader br=new BufferedReader(new FileReader(newFilePath));
		String line="";
		
		
		File file=new File(convertFilePath);
		if(!file.exists()) file.createNewFile();
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		
		
		while((line=br.readLine())!=null){
			String splits[] = line.split("	");
			if(splits.length<=2) continue;
			else{
				bw.write(line);
				bw.write("\n");
			}
		}
		bw.close();
	}
	
	/**
	 * Handle hangling links (uncrawled links):
	 * Randomly generate 200 destination links from crawled links for hangling links
	 * @param filePath -- the path of the file after removing sinks
	 * @throws IOException
	 */
	
	public static void handleDangling(String filePath) throws IOException{
		File file=new File(filePath);
		BufferedReader br=new BufferedReader(new FileReader(filePath));
		//DataInputStream dis=new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		String line="";
		Set<String> sourceSet=new HashSet<String>();
		
		//get all source link
		while((line=br.readLine())!=null){
			System.out.println(line);
			String split[]=line.split("	");
			sourceSet.add(split[0]);			
		}
		br.close();
		
		//check all destination link, find all new source link
		br=new BufferedReader(new FileReader(filePath));
		Set<String> newSourceSet=new HashSet<String>();
		while((line=br.readLine())!=null){
			String split[]=line.split("	");
			for(int i=2;i<split.length;i++){
				if(!sourceSet.contains(split[i])){
					newSourceSet.add(split[i]);
				}
			}			
		}
		br.close();
		
	
		List<String> sourceList=new ArrayList<String>(sourceSet);
		
	
		//append to file
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filePath, true)));

		    
		    Iterator iter2=newSourceSet.iterator();
		    String mergeLine="";
		    out.println("");
		    
			while(iter2.hasNext()){
				int randomNum = 0 + (int)(Math.random()*(sourceSet.size()-1));
				String dest=sourceList.get(randomNum);
				mergeLine=iter2.next()+"	1.0	"+dest;
				//System.out.println(mergeLine);
				out.println(mergeLine);
			}			

		    out.close();
		} catch (IOException e) {
		    //exception handling left as an exercise for the reader
		}		
	}
	
	
	public static void main(String[] args) throws IOException{
		if(args.length==3){
			String filePath=args[0];
			String newFilePath=args[1];
			String convertFilePath=args[2];
			mergeAndRemoveDup(filePath,newFilePath);
			removeSink(newFilePath,convertFilePath);
			handleDangling(convertFilePath);
		}else{
			System.out.println("args: <link.txt path> <convertfile path> <convertfile2 path>");
		}
	}

}
