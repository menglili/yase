package edu.upenn.cis455.YASE.pagerank;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
/**
 * Page rank reducer 
 * @author Mengli Li
 *
 */
public class PRReducer extends MapReduceBase implements
        Reducer<Text, Text, Text, Text> {
    private Text reduceVal = new Text();
    static Double DAMPING_FACTOR = 0.85;

    /**
     * @param key, key got from mapper after shuffle
     * @param values, values got from mapper after shuffle
     * @param output, reduce output 
     */

    public void reduce(Text key, Iterator<Text> values,
                       OutputCollector<Text, Text> output, Reporter reporter)
            throws IOException {

        String allOutlinks = "";
        double PR = 0.0;
        double currentPageRank = 0.0;
        List<String> allInlinks = new LinkedList<String>();

        boolean hasChangedPageRank = false;

        // System.out.println("reduceKey: "+key);
        while (values.hasNext()) {
            Text value = values.next();
            System.out.println("reduce: " + value.toString());

            String[] parts = value.toString().split(" ");

            try {
                String inbound = parts[0].trim();

                // if inbound is a pagerank value, then 'value' represents
                // outbound links
                try {
                    double d = Double.parseDouble(inbound);
                    currentPageRank = d;
                    allOutlinks = value.toString();
                    continue;
                } catch (NumberFormatException e) {
                     System.out.println(e);
                }

                // if inbound is not a pagerank value, then 'key' is one of
                // inlink, 'value' represents '<current link> <current PR>
                // <number of outlinks>'
                allInlinks.add(inbound);
                double inboundPagerank = Double.parseDouble(parts[1]);
                double numOutlinks = Double.parseDouble(parts[2]);

                PR += (DAMPING_FACTOR * (inboundPagerank / numOutlinks));

            } catch (Exception e) {
                System.out.println(e);
            }
        } 

        // note that allOutlinks[0] is still the old pagerank value, replace it
        // with new one
        String[] outs = allOutlinks.split(" ");
        StringBuffer links = new StringBuffer();

       // add all outlinks to corresponding source link
       for (int i = 1; i < outs.length; i++) {
            links.append(outs[i] + "\t");
       }

        PR += (1.0 - DAMPING_FACTOR) / 1.0;

        String str = String.valueOf(PR) + "\t" + links.toString().trim();

        System.out.println("ReduceOutput: " + key.toString() + " " + str);

        reduceVal.set(str);

        output.collect(key, reduceVal);
    }
}