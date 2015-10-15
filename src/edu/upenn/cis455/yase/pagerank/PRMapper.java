package edu.upenn.cis455.YASE.pagerank;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;


/**
 * pager rank mapper
 * @author Mengli Li
 *
 */
public class PRMapper extends MapReduceBase implements
        Mapper<LongWritable, Text, Text, Text> {
    private Text outlink = new Text();
    private Text mapVal = new Text();
    private Text extraKey = new Text();
    private Text extraVal = new Text();
    
    
    
    /**
     * @param value -- links.txt (got from crawler: contains source link, initial PR, some destination links for each line)
     * 					at iteration 1, improved links.txt from iteration 2
     * @param output -- map output, emit to reduce  
     * 
     */
    public void map(LongWritable k, Text value,
                    OutputCollector<Text, Text> output, Reporter reporter)
            throws IOException {
        String line = value.toString();
        String[] parts = line.split("\t");
        double PR = 0.0;// page rank

        String source = parts[0].trim();// get source link

        System.out.println("source link: " + source);
        System.out.println("map line: " + line);

        try {
            PR = Double.parseDouble(parts[1]);
            System.out.println("curret page rank:" + PR);
        } catch (Exception e) {
            System.out.println(e);
            return;
        }

        int numOutlinks = parts.length - 2;// number of outlinks

        // emit for each outlink
        for (int i = 2; i < parts.length; i++) {
            String dest = parts[i];
            if (!dest.equals(source)) {
                outlink.set(dest.trim());
                String outlinkValue = source.toString() + " "
                        + String.valueOf(PR) + " "
                        + String.valueOf(numOutlinks).trim();
                System.out.println("outlinkValue" + outlinkValue);
                mapVal.set(outlinkValue);
                // System.out.println("outlink"+outlink);
                // System.out.println("word"+word);
                output.collect(outlink, mapVal);// map output
            }
        }

        // also emit all the outlinks
        // String str="";
        StringBuffer sb = new StringBuffer();
        for (int i = 1; i < parts.length; i++) {
            // str=str+parts[i]+" ";
            sb.append(parts[i] + " ");
        }
        extraVal.set(sb.toString());
        extraKey.set(source);
        output.collect(new Text(source), extraVal);
    }
}
