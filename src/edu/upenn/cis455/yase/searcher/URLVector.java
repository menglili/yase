package edu.upenn.cis455.YASE.searcher;

import java.util.ArrayList;

/**
 * 
 * @author SuperE
 *
 */
public class URLVector {
	private String url;
	private ArrayList<Float> vector;
	public int boost = 1;
	private String title;
	
	/**
	 * 
	 * @param url
	 * @param VectorDim
	 */

	public URLVector(String url, int VectorDim){
		this.url = url;
		this.vector = new ArrayList<Float>(VectorDim);
		for(int i =0;i<VectorDim;i++){
			vector.add(0f);
		}
	}
	public String getURL(){
		return url;
	}
	public void setVector(int index, Float tfidf){
		vector.set(index, tfidf);
	}
	public void setLengthVector(Float length){
		vector.set(vector.size()-1, length);
	}
	public void setTitle(String title){
		this.title = title;
	}
	public String getTitle(){
		return title;
	}
	public float cossimilarity(ArrayList<Float> query){
		float cNorm = 0;
		float res = 0;
		for(int i=0;i<query.size();i++){
			res += query.get(i)*vector.get(i);
			cNorm += vector.get(i)*vector.get(i);
		}
		if(cNorm!=0){
			res = (float) (res/Math.sqrt(cNorm));
//			System.out.println("url: "+ url +" res: " + res + " cNorm: " + cNorm);
//			return res*boost;
			return (float) (res*boost/(url.length()+1));
		}else{
			return 0;
		}
//		return res;
//		System.out.println(url + " cos: " + res);
	}

}
