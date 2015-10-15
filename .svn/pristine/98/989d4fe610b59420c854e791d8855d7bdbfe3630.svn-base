package edu.upenn.cis455.YASE.pagerank;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.io.*;

/**
 * page rank driver
 * @author Mengli Li
 *
 */
public class PRDriver {

	/**
	 * start map-reduce to calculate page rank
	 * @param args
	 * -- args[0] : input path of map-reduce
	 * -- args[1] : output path of map-reduce
	 * -- args[2] : number of iterations for pagerank
	 * @throws Exception
	 */
    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            printUsage();
            System.exit(0);
        }

        String input = args[0];
        String output = args[1];
        int numIterations = Integer.parseInt(args[2]);

        for (int i = 0; i < numIterations; i++) {
            if (i == 0)
                runJob(input, output + (i + 1));// read data from input dir when
                // PR is initialized
            else {
                runJob(output + i, output + (i + 1));
            }

        }

    }
    
    /**
     * start run the job of mapreduce
     * @param input -- input path of map-reduce
     * @param output -- output path of map-reduce
     * @throws IOException
     */

    public static void runJob(String input, String output) throws IOException {
        JobConf conf1 = new JobConf(PRDriver.class);
        conf1.setJobName("pagerank");

        conf1.setOutputKeyClass(Text.class);
        conf1.setOutputValueClass(Text.class);

        conf1.setMapperClass(PRMapper.class);
        conf1.setReducerClass(PRReducer.class);

        conf1.setInputFormat(TextInputFormat.class);
        conf1.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf1, new Path(input));
        FileOutputFormat.setOutputPath(conf1, new Path(output));

        System.out.println("Running PageRank job, damping="
                + PRReducer.DAMPING_FACTOR + ", normalize=" + 1.0
                + "  with input>>" + input + "  output>>" + output);

        JobClient.runJob(conf1);
    }

    public static void printUsage() {
        System.out
                .println("Usage: <input path> <output path> [num iterations]");
    }

}