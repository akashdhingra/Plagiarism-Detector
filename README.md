Used Levenshtein Distance Algorithm with SIM check - It measures the similarity between the source and target text.

Step 1: Get the Synonyms Map by splitting the source text into words grouped line by line. construct the map with a group of words. Used Map<String, HashSet<String>> to maintain word lists in processing orders.  

Step 2:  Get the collection of words from the source text by splitting the text by defined split word size (2). and constructing the Object of the source words list. Created 'WordCollection' internal class to hold complete text and collection of split words data.

Step 3:  Get the collection of words from the target text by splitting the text by defined split word size (2). and constructing the Object of the source words list. used the 'WordCollection' internal class to hold complete text and collection of split words data.

Step 4:  Check the Source word collection and target word collection with Synonyms Map and calculate the similarity count mapped by the equal method by looping the target words list.

Step 5: Calculate percentage  = (totalSimilarityCount / target words. size) *100.

Step 6: Check percentage <= 45 then assume it is not Plagiarism. else it is Plagiarism.