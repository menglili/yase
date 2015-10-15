package edu.upenn.cis455.YASE.pagerank;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Merge all Page Rank value for all nodes, remove all outlinks for each line, just keep source link and page rank
 * @author Mengli Li
 *
 */

public class SavePR {
	
	
	/**
	 * do normalization of page rank and store page rank in a txt file
	 * @param folder -- the path of the folder store all the output of reduce for all nodes
	 * @return -- the path of the merge file 
	 * @throws IOException
	 */
	public static String mergeFile(String folder) throws IOException {
		  File dir=new File(folder);
		  File[] files=dir.listFiles();
		  double sum = 0;
		  //create a merge file
		  String mergeFileName=dir.getPath()+"/"+"PRStore"+".txt";
		  File mergedFile=new File(mergeFileName);
		  mergedFile.createNewFile();
		  
		  //merge all files in spool-in dir
		  FileWriter fw=null;
		  BufferedWriter bw=null;
		  try{
			  fw=new FileWriter(mergedFile,true);
			  bw=new BufferedWriter(fw);
		  }catch(Exception e){
			  System.out.println(e);
		  }
		  
		  for(File f:files){
			  //System.out.println("name:"+f.getName());
			  if(f.getName().equals("_SUCCESS")||f.getName().equals(".DS_Store")) {
				  //System.out.println("hi");
				  continue;
			  }
			  //System.out.println("merging: "+f.getName());
			  FileInputStream fis;
			  
			  try{
				  fis=new FileInputStream(f);
				  BufferedReader br=new BufferedReader(new InputStreamReader(fis));
				  
				  String line;
				  String newLine;
				  while((line=br.readLine())!=null){
					  String split[] = line.split("\t");
					 // System.out.println("***"+split[0]);
					  if(split[0].equals("")) {
						  //System.out.println("******************"+line);
						  continue;
					  }
					  Double score=Double.parseDouble(split[1]);
					  if(score>=5000) score = Math.log(Double.parseDouble(split[1]))/Math.log(2.4);
					  else if(score>=1000 && score<5000) score = Math.log(Double.parseDouble(split[1]))/Math.log(2.2);
					  else if(score>=500 && score <1000) score = Math.log(Double.parseDouble(split[1]))/Math.log(2.1);
					  else if(score>=300 && score <500) score= Math.log(Double.parseDouble(split[1]))/Math.log(2.0);
					  else if(score>=200 && score< 300) score = Math.log(Double.parseDouble(split[1]))/Math.log(1.9);
					  else if(score>=100 && score< 200) score = Math.log(Double.parseDouble(split[1]))/Math.log(1.8);
					  else if(score>=50 && score< 100) score = Math.log(Double.parseDouble(split[1]))/Math.log(1.7);
					  else if(score>=10 && score< 50) score = Math.log(Double.parseDouble(split[1]))/Math.log(1.6);
					  else if(score>=5 && score< 10) score = Math.log(Double.parseDouble(split[1]))/Math.log(1.5);
					  else if(score>=1 && score< 5) score = Math.log(Double.parseDouble(split[1]))/Math.log(1.4);
					  
					  if(score<=1) score=1.0;
					  //Double score=Math.log(Double.parseDouble(split[1]))/Math.log(2);
					 // score=score*10/13;
					  //if(score<1.0) score=1.0;  
					  					  
					  newLine = split[0]+"\t"+score.toString();
					  //if(Double.parseDouble(split[1])>1.0) 
					  System.out.println(newLine);
					  sum=sum+Double.parseDouble(split[1]);
					  bw.write(newLine);
					 // System.out.println(newLine);
					  bw.newLine();
				  }
				  br.close();
			  }catch(Exception e){
				  System.out.println(e);
			  }
		  }	  
		  System.out.println("sum"+sum);
		  bw.close();
		  
		  return mergeFileName;
	  }

	
	public static void main(String[] args) throws IOException{
		mergeFile(args[0]);
	}
}
