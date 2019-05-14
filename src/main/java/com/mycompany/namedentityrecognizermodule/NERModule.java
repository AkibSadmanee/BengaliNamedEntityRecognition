package com.mycompany.namedentityrecognizermodule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adnan
 */

public class NERModule {
    String path;
    Set<String> persons;
    Set<String> organizaion;
    Set<String> locations;
    Set<String> others;
    
    
    // constructor, initialize entity sets
    public NERModule(){

        persons = new HashSet<String>();
        organizaion = new HashSet<String>();
        locations = new HashSet<String>();
        others = new HashSet<String>();

    }
    
    // getters for the entities
    public Set<String> getPersons(){
        return persons;

    }
    public Set<String> getOrganiztion(){
        return organizaion;
    }

    public Set<String> getLocations() {
        return locations;
    }

    public Set<String> getOthers() {
        return others;
    }

    
    // this method takes a test file name for CRF++ as input 
    // generates predicted outputs(named entities) in a file by running the CRF++ model.
    // read the output file, marge multi word entities,  append the entities in appropriate sets
    public void getNER(String singleLineDocumentFilename) throws IOException, InterruptedException {
        
        // command for CRF++ test
        String[] command = { "/bin/bash", "-c", " crf_test -m model "+singleLineDocumentFilename+" "};
        Process proc = new ProcessBuilder(command).start();
           
        // reads the output file generated from CRF++ model using the given sentence
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));
        
        
        // marge the consiqutive individual words in a single named entity from output
        // appends in appropriate entity sets using NER tags
        String string = stdInput.readLine();
        String [] tempArray = string.split("\t");
        String prevString = tempArray[0];
        String prevTag = tempArray[5];
        int mergeCount = 0;
        
        while ((string = stdInput.readLine()) != null) {
            String []nERArray = string.split("\t");
            if(nERArray.length != 6)
                continue;
            String currentString = nERArray[0];
            String currentTag = nERArray[5];
            //System.out.println(currentTag);
            
            if(prevTag.equals(currentTag)){
                //System.out.println("current tag "+currentTag);
                prevString +=" "+ currentString;
                mergeCount++;
                
            } else {
                //System.out.println("prev tag "+prevTag);
                if(prevTag.equals("B-PER")){
                    persons.add(prevString);

                }
                else if (prevTag.equals("B-LOC")){
                    locations.add(prevString);

                }
                else if (prevTag.equals("B-ORG")){
                    organizaion.add(prevString);

                } else
                    others.add(prevString);
                    prevString = currentString;
                    prevTag = currentTag;
            }
        }
        proc.destroy();
    }
    

    // this mathod takes a full document string as input and returns the map of detected entities
    // first generates train file from input documents using features (POS, Stemmer) using TrainFileGenerator()
    public Map getNERFromDocument(String news){
        
        NERModule nercrfapi = new NERModule();
        Map<String, Set<String>> nerMap = new HashMap();

        TrainFileGenerator tfg = new TrainFileGenerator();

        // break documents into lines, generates train file, predict NER for each line using getNER() using train file
        StringTokenizer st = new StringTokenizer(news, "\\।\\?\\!");
       
        while (st.hasMoreElements()) {
            
            String line = st.nextElement().toString();
            String singleLineDocumentFilename = tfg.getFeaturesForTesting(line);

            try {
                nercrfapi.getNER(singleLineDocumentFilename);
                
            } catch (IOException ex) {
                Logger.getLogger(NERModule.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(NERModule.class.getName()).log(Level.SEVERE, null, ex);
            }

            Set<String> list;

            list = nercrfapi.getPersons();
            for (String string : list) {
              // System.out.println("person : " + string);
                nerMap.put("person", list);
            }
            list = nercrfapi.getLocations();
            for (String string : list) {
              //  System.out.println("loc : " + string);
                nerMap.put("loc", list);
            }
            list = nercrfapi.getOrganiztion();
            for (String string : list) {
             //   System.out.println("org : " + string);
                nerMap.put("org", list);
            }
        }
        
        return nerMap;
    }
    
     public static void main(String[] args) throws IOException, InterruptedException {
        
        String news = "জামালপুরের দেওয়ানগঞ্জে দাম্পত্য কলহের জের ধরে স্ত্রী রিনা বেগমকে কুপিয়ে হত্যা করেছে স্বামী আব্দুল্লাহ।  শনিবার মধ্যরাতে উপজেলার চর গামারিয়া গ্রামে এ ঘটনা ঘটে। আব্দুল্লাহ চায়ের দোকানে কাজ করতেন। তাদের একটি কন্যা সন্তান রয়েছে।\n" +
"\n" +
"রবিবার সকালে ঘটনাস্থল থেকে লাশ উদ্ধার করে ময়নাতদন্তের জন্য হাসপাতালে পাঠিয়েছে পুলিশ।\n" +
"\n" +
"দেওয়ানগঞ্জ থানার পরিদর্শক (তদন্ত) আব্দুল লতিফ মিয়া জানান, দেওয়ানগঞ্জের চর গামারিয়া কুদ্দুস আলীর ছেলে আব্দুল্লাহর সাথে সাত বছর আগে ইসলামপুরের কাঁচিহারা গ্রামের ইনছার আলীর মেয়ে রিনা বেগমের বিয়ে হয়। বিয়ের পর থেকেই তাদের দাম্পত্য কলহ চলে আসছিল। শনিবার মধ্যরাতে দাম্পত্য কলহ নিয়ে বাকবিতণ্ডার এক পর্যায়ে আব্দুল্লাহ তার স্ত্রীকে ধারালো অস্ত্র দিয়ে কুপিয়ে জখম করেন। অস্ত্রের আঘাতে রিনা বেগম মারা গেলে লাশ ঘরে রেখে রাতেই পালিয়ে যান আব্দুল্লাহ।\n" +
"\n" +
"খবর পেয়ে রবিবার সকালে পুলিশ লাশ উদ্ধার করে। \n" +
"\n" +
"দেওয়ানগঞ্জ থানায় একটি হত্যা মামলা হয়েছে বলে জানিয়েছেন পরিদর্শক (তদন্ত) আব্দুল লতিফ মিয়া। আব্দুল্লাহকে গ্রেপ্তারের চেষ্টা চলছে বলে তিনি জানান।\n" +
"\n" +
"(ঢাকাটাইমস/১১ফেব্রুয়ারি/প্রতিনিধি/ওআর/এলএ)";
         
        NERModule nercrfapi = new NERModule();
        Map nerMap = nercrfapi.getNERFromDocument(news);
        
        System.out.println("Persons : " +nerMap.get("person"));
        System.out.println("Places: " + nerMap.get("loc"));
        System.out.println("Organizations : " +nerMap.get("org"));
        

    }

}
