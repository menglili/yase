package edu.upenn.cis455.YASE.indexer;

import edu.upenn.cis455.YASE.Util;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.io.IOException;
import java.util.*;

public class InvertIndex {


    public static class Map extends MapReduceBase implements
            Mapper<LongWritable, Text, Text, Text> {


        @Override
        public void map(LongWritable key, Text value,
                        OutputCollector<Text, Text> output, Reporter reporter)
                throws IOException {

            final Text word = new Text();
            final Text url_tf = new Text();

            String line = value.toString();
            String[] lineSplit = line.split(Util.uniqueSeparator);

            if (lineSplit.length != 3) {
                System.err.println("m:" + line);
                for (String s : lineSplit) {
                    System.out.println("s = " + s);
                }
                return;
            }

            // get params
            String urlString = lineSplit[0];
//            String allWords = lineSplit[1];
            String titleString = lineSplit[1];
            String allWords = lineSplit[2];


            // compute TF and count length
            double dataLength = 0;
            HashMap<String, Integer> urlTFMap = new HashMap<String, Integer>();
            for (String wordString : allWords.split(" ")) {

                //for each word, increment data length by one
                dataLength++;

                // update tf
                if (urlTFMap.keySet().contains(wordString)) {
                    int tf = urlTFMap.get(wordString);
                    tf++;
                    urlTFMap.put(wordString, tf);
                } else {
                    // if new
                    urlTFMap.put(wordString, 1);
                }
            }

            // square root tf
            for (String w : urlTFMap.keySet()) {
                double tf = (double) urlTFMap.get(w);
                tf = Math.sqrt(tf);

                //get square root of length
                double length = Math.sqrt(dataLength);

                //conform format url_TF_length_title
                String url_tf_length_title = urlString + Util.uniqueSeparator + tf + Util.uniqueSeparator + length + Util.uniqueSeparator + titleString;


                url_tf.set(url_tf_length_title);
                word.set(w);
                output.collect(word, url_tf);
            }

        }

    }

    public static class Reduce extends MapReduceBase implements
            Reducer<Text, Text, Text, Text> {

        @Override
        public void reduce(Text key, Iterator<Text> values,
                           OutputCollector<Text, Text> output, Reporter reporter)
                throws IOException {

            final Text invertedList = new Text();
            int queueMaxSize = 1000000;
            PriorityQueue<URLTF> urlTFQueue = new PriorityQueue<URLTF>(100,
                    new LowestTFComparator());

            // read all urls
            while (values.hasNext()) {

                // set up
                String urlString = values.next().toString();
                String[] strings = urlString.split(Util.uniqueSeparator);

                // check url TF format
                if (strings.length != 4) {
                    // System.err.println("r: \"" + urlString + "\"");
                    continue;
                }

                // get parameters
                String url = strings[0];
                double tf = Double.parseDouble(strings[1]);
                double length = Double.parseDouble(strings[2]);
                String title = strings[3];

                // add it to the queue and limit its size
                urlTFQueue.add(new URLTF(url, tf, length, title));
                if (urlTFQueue.size() > queueMaxSize) {
                    urlTFQueue.poll();
                }
            }

            // create a stack to store URLTF
            Stack<URLTF> stack = new Stack<URLTF>();
            while (!urlTFQueue.isEmpty()) {
                stack.add(urlTFQueue.poll());
            }

            // pop the stack to get reverse order of TF
            StringBuilder stringBuilder = new StringBuilder();
            while (!stack.empty()) {
                URLTF urltf = stack.pop();

                //conform the format
                String[] strings = new String[]{urltf.getUrl(), Double.toString(urltf.getTf()), Double.toString(urltf.getLength()), urltf.getTitle()};

                //insert separators
                String url_tf_length_title = Util.insertSeparators(strings, Util.uniqueSeparator);

                //build the string
                stringBuilder.append(url_tf_length_title).append("\t");
            }

            invertedList.set(stringBuilder.toString());
            output.collect(key, invertedList);
        }

        static class LowestTFComparator implements Comparator<URLTF> {
            @Override
            public int compare(URLTF o1, URLTF o2) {
                return Double.compare(o1.getTf(), o2.getTf());
            }
        }

        private class URLTF {

            private String url;
            private double tf;
            private double length;
            private String title;

            public URLTF(String url, double tf, double length, String title) {
                this.tf = tf;
                this.url = url;
                this.length = length;
                this.title = title;
            }

            public double getTf() {
                return tf;
            }

            public String getUrl() {
                return url;
            }

            @Override
            public String toString() {
                return String.format("{url=%s, tf=%s}", url, tf);
            }

            public double getLength() {
                return length;
            }

            public String getTitle() {
                return title;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        JobConf conf = new JobConf(InvertIndex.class);
        conf.setJobName("InvertIndex");
        long timeout = 1000 * 60 * 60;
        conf.setLong("mapred.task.timeout", timeout);

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(Map.class);
        conf.setReducerClass(Reduce.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(args[0]));
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));

        JobClient.runJob(conf);
    }
}