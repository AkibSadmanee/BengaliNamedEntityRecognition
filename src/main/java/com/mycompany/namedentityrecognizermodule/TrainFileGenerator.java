/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.namedentityrecognizermodule;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author adnan
 */
public class TrainFileGenerator {


    int i = 0;

    Map<String, Integer> wordCluster = new HashMap<String, Integer>();
    List stopwords = new ArrayList();

    public TrainFileGenerator() {
        loadStopWordMap();
        loadWordClusterMap();
    }

    public void loadStopWordMap() {
        /* This function pre-loads lebeled cluster map (word -> cluster) */

        try {
            File rtsnefileDir = new File("stopwords.txt");
            BufferedReader in = null;
            String str;

            in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(rtsnefileDir), "UTF8"));

            while ((str = in.readLine()) != null) {

                stopwords.add(str.toString());

                // System.out.println(word + ":" + clusterNo);
            }
            in.close();

        } catch (IOException ex) {
            Logger.getLogger(TrainFileGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadWordClusterMap() {
        /* This function pre-loads lebeled cluster map (word -> cluster) */

        try {
            File rtsnefileDir = new File("cluster_lebeled.csv");
            BufferedReader in = null;
            String str;

            in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(rtsnefileDir), "UTF8"));

            while ((str = in.readLine()) != null) {

                StringTokenizer st = new StringTokenizer(str, ",");

                while (st.hasMoreElements()) {
                    String word = (String) st.nextElement();
                    int clusterNo = Integer.parseInt(((String) st.nextElement()).trim());

                    wordCluster.put(word, clusterNo);

                   //  System.out.println(word + ":" + clusterNo);
                }

            }

            in.close();
        } catch (IOException ex) {
            Logger.getLogger(TrainFileGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String getFeaturesForTesting(String line) {
        String singleLineFeaturedOutputFilename = "document_output_single_line.txt";
        StringTokenizer st3;
        String pos = null;
        String root = null;

        Document doc = null;
        Elements para = null;

        int it = 0;
        BufferedWriter outCatWordList = null;
        try {
            outCatWordList = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(singleLineFeaturedOutputFilename), "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TrainFileGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TrainFileGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

      //  System.out.println(line);

        try {

            doc = Jsoup.connect("http://text-processing.com/demo/tag/")
                    .data("text", line)
                    .data("tagger", "bangla")
                    // and other hidden fields which are being passed in post request.
                    .userAgent("Mozilla")
                    .timeout(10 * 10000)
                    .post();

            para = doc.getElementsByTag("p");

            for (Element succ : para) {
                if (succ.hasClass("success")) {
                    String sentence = succ.text();
                    //   System.out.println(sentence); // POS tagged sentence
                    StringTokenizer st2 = new StringTokenizer(sentence, " ");
                    String word = null;

                    // System.out.println();
                    String steemmed = null;
                    // System.out.println("<s> " + "none " + "O " + "OT ");

                    outCatWordList.write("\n");
                    while (st2.hasMoreElements()) {

                        word = (String) st2.nextElement();

                        st3 = new StringTokenizer(word, "/");

                        if (st3.hasMoreElements()) {
                            root = (String) st3.nextElement();
                            pos = (String) st3.nextElement();
                        }

                        BengaliStemmerLight blSt = new BengaliStemmerLight();

                        steemmed = blSt.stem(root);
                        steemmed = StringUtils.difference(steemmed, root);
                        String entity = "";
                        entity = "OT";

                        Pattern p = Pattern.compile("[(][\u09E6-\u09EF]+[)]"); //RegEx for (২৩)
                        Matcher m = p.matcher(root);
                        if (m.find()) {
                            root = "AGE";
                        }

                        String cluster = null;
                        if (stopwords.contains(root)) {
                            cluster = "stop";
                        } else if (wordCluster.containsKey(root)) {
                            cluster = wordCluster.get(root).toString();
                        } else {
                            cluster = "unk";
                        }

                        if (steemmed != "") {

                           // System.out.println(root + " " + pos + " " + steemmed + " " + cluster + " " + entity);
                            outCatWordList.write(root + " " + pos + " " + steemmed + " " + cluster + " " + entity);
                            outCatWordList.write("\n");
                        } else {
                         //   System.out.println(root + " " + pos + " " + "O" + " " + cluster + " " + entity);
                            outCatWordList.write(root + " " + pos + " " + "O" + " " + cluster + " " + entity);
                            outCatWordList.write("\n");
                        }
                    }
                    //System.out.println("</s> " + "none " + "O " + "OT ");
                    // System.out.println("");

                    outCatWordList.write("। " + "SYM " + "O " + "SYM " + "OT");
                    outCatWordList.write("\n");
                    // System.out.println(i++);
                }
            }
        } catch (Exception e) {
            System.out.println("connection timed out");
        }
        try {
            outCatWordList.close();
        } catch (IOException ex) {
            Logger.getLogger(TrainFileGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

        return singleLineFeaturedOutputFilename;

    }

    public static void main(String[] args) throws IOException {

        TrainFileGenerator tfg = new TrainFileGenerator();

        String testfileName = tfg.getFeaturesForTesting("চট্টগ্রামের সাতকানিয়ায় গাছ কাটা নিয়ে বিরোধের জেরে প্রতিপক্ষের ছুরিকাঘাতে আবদুস ছবুর (৪০) নামের একজন নিহত হয়েছেন।");
        System.out.println(testfileName);

    }
}
