Bengali Named Entity Recognition
==========================================================
This is a JAVA module for Bengali Named Entity Recognition, which used a C++ library called CRF++ as implimentation of learning algorithm.
Upon a given string, the module can return the person, place and organization names from the string.

### pre-requisites  ###

Need to download and install CRF++ first.
CRF++ must be installed in the machine. 

Download and install instructions : <https://taku910.github.io/crfpp/>

      % ./configure 
      % make
      % su
      # make install

### How to use ##

Module takes text string as input (with one or many lines) and returns output in a map containing named entities.

                 NERModule.java
                 ++++++++++++++++++++++++
 
                String news = "জামালপুরের দেওয়ানগঞ্জে দাম্পত্য কলহের জের ধরে স্ত্রী রিনা বেগমকে কুপিয়ে হত্যা করেছে স্বামী আব্দুল্লাহ।  শনিবার মধ্যরাতে                 উপজেলার চর গামারিয়া গ্রামে এ ঘটনা ঘটে। আব্দুল্লাহ চায়ের দোকানে কাজ করতেন। তাদের একটি কন্যা সন্তান রয়েছে।\n" +
                "রবিবার সকালে ঘটনাস্থল থেকে লাশ উদ্ধার করে ময়নাতদন্তের জন্য হাসপাতালে পাঠিয়েছে পুলিশ।\n" +
                "দেওয়ানগঞ্জ থানার পরিদর্শক (তদন্ত) আব্দুল লতিফ মিয়া জানান, দেওয়ানগঞ্জের চর গামারিয়া কুদ্দুস আলীর ছেলে আব্দুল্লাহর সাথে সাত বছর      আগে ইসলামপুরের কাঁচিহারা গ্রামের ইনছার আলীর মেয়ে রিনা বেগমের বিয়ে হয়। বিয়ের পর থেকেই তাদের দাম্পত্য কলহ চলে আসছিল। শনিবার মধ্যরাতে দাম্পত্য কলহ নিয়ে বাকবিতণ্ডার এক পর্যায়ে আব্দুল্লাহ তার স্ত্রীকে ধারালো অস্ত্র দিয়ে কুপিয়ে জখম করেন। অস্ত্রের আঘাতে রিনা বেগম মারা গেলে লাশ ঘরে রেখে রাতেই পালিয়ে যান আব্দুল্লাহ।\n" +
                "খবর পেয়ে রবিবার সকালে পুলিশ লাশ উদ্ধার করে। \n" +
                "দেওয়ানগঞ্জ থানায় একটি হত্যা মামলা হয়েছে বলে জানিয়েছেন পরিদর্শক (তদন্ত) আব্দুল লতিফ মিয়া। আব্দুল্লাহকে গ্রেপ্তারের চেষ্টা চলছে বলে তিনি জানান।\n";
                        
                        
                  NERModule nercrfapi = new NERModule();
                  Map nerMap = nercrfapi.getNERFromDocument(news);
                        
                  System.out.println("Persons : " +nerMap.get("person"));
                  System.out.println("Places: " + nerMap.get("loc"));
                  System.out.println("Organizations : " +nerMap.get("org"));
                        
                        

### methodes used ### 

Bengali Named entity recognition using Conditional Random Field is based on a paper titled "Semi-supervised Learning for Vietnamese Named Entity Recognition using Online Conditional Random Fields". Paper Link: <https://www.aclweb.org/anthology/W15-3907>
