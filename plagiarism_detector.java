import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class plagiarism_detector {

    int splitSize = 2;
    static Map<String, HashSet<String>> synonymsMap = null;

    public static void main(String[] args) throws IOException
    {
        plagiarism_detector plagiarismDetector = new plagiarism_detector();
        File fileONE = new File(args[0]);
        File fileTWO = new File(args[1]);

        String mappingText = readFile(fileONE);
        String checkTextONE = readFile(fileONE);
        String checkTextTWO = readFile(fileTWO);

        if (mappingText == null || mappingText.length() == 0)
        {
            synonymsMap = new HashMap<String, HashSet<String>>();
        }else
        {
            synonymsMap = plagiarismDetector.getSynonymsMap(mappingText);
        }


        System.out.println(plagiarismDetector.plagiarism(checkTextONE,checkTextTWO));


    }

    public static String readFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder stringBuilder = new StringBuilder();
        char[] buffer = new char[10];
        while (reader.read(buffer) != -1) {
            stringBuilder.append(new String(buffer));
            buffer = new char[10];
        }
        reader.close();

        return stringBuilder.toString();
    }

    /**
     * Method which reads 2 input strings  and returns the plagiarism found or not
     * @param  checkTextONE,
     * @param  checkTextTWO
     * @return integer  : plagiarism found - 1 , else - 0
     * @throws IllegalArgumentException
     */
    public int  plagiarism(String checkTextONE,String checkTextTWO) throws IllegalArgumentException
    {

        WordCollection originalCorpus = new WordCollection(checkTextONE, splitSize);
        WordCollection suspectCorpus = new WordCollection(checkTextTWO, splitSize);

        int matchPercentage = getPlagiarismPercentage(originalCorpus, suspectCorpus, synonymsMap);
        //System.out.println(">>Percentage>>"+matchPercentage);
        if(matchPercentage <= 45)
            return 0;
        else
            return 1;
    }


    public Map<String, HashSet<String>> getSynonymsMap(String inputString)
    {
        List<String> synonymGroupList =  Arrays.stream(inputString.split("\\|")).map(String::trim).collect(Collectors.toList());
        Map<String, HashSet<String>> synonymsMap = new HashMap<String, HashSet<String>>();

        for(String synonymGroup : synonymGroupList) {
            String[] splitWords = synonymGroup.split(" ");
            HashSet<String> synonymSet = (HashSet<String>) Arrays.stream(splitWords).map(String::trim).collect(Collectors.toSet());
            for (String synonymKey : synonymSet) {
                if (synonymsMap.containsKey(synonymKey)) {
                    HashSet<String> exSynonymSet = synonymsMap.get(synonymKey);
                    exSynonymSet.addAll(synonymSet);
                    synonymsMap.put(synonymKey, exSynonymSet);
                } else {
                    synonymsMap.put(synonymKey, synonymSet);
                }
            }
        }

        return synonymsMap;
    }

    /**
     * Method to calculate plagiarism percentage: percent of splitWords in suspectText which appear in originalText
     * @param originalCorpus
     * @param suspectCorpus
     * @param synonymsMap
     * @return
     */
    private int getPlagiarismPercentage(WordCollection originalCorpus, WordCollection suspectCorpus, Map<String, HashSet<String>> synonymsMap) {
        List<SplitWords> originalSplitWordsList = originalCorpus.getSplitWordsList();
        List<SplitWords> suspectSplitWordsList = suspectCorpus.getSplitWordsList();

        if (originalSplitWordsList.size() == 0 || suspectSplitWordsList.size() == 0) {
            return 0;
        }
        double similarityCount = 0d;
        for (SplitWords suspectSplitWords : suspectSplitWordsList) {
            for (SplitWords originalSplitWords : originalSplitWordsList) {
                if (suspectSplitWords.equals(originalSplitWords, synonymsMap)) {
                    similarityCount += 1;
                    break;
                }
            }
        }
        return (int) Math.ceil((similarityCount / suspectSplitWordsList.size()) * 100);
    }


    public class SplitWords {

        private List<String> wordsList= new ArrayList<>();
        private int splitWordsSize;

        public SplitWords()
        {
            this.splitWordsSize = 0;
        }

        public SplitWords(int size)
        {
            this.splitWordsSize = size;
        }

        public SplitWords(List<String> wList, int swSize)
        {
            this.wordsList = wList;
            this.splitWordsSize = swSize;
        }

        public void addSplitWord(String word) {
            this.wordsList.add(word);
        }

        public List<String> getSplitWords() {
            return this.wordsList;
        }

        public void setWords(List<String> words) {
            this.wordsList = words;
        }

        public int getSplitWordsSize() {
            return this.splitWordsSize;
        }

        public void setSplitWordsSize(int splitWordsSize) {
            this.splitWordsSize = splitWordsSize;
        }

        @Override
        public String toString() {
            return String.join(", ", this.wordsList);
        }

        public Boolean equals(SplitWords splitWordsObj, Map<String, HashSet<String>> synonymsMap) {
            List<String> orgWordList = this.getSplitWords();
            List<String> spWordList = splitWordsObj.getSplitWords();

            for (int k = 0; k < this.splitWordsSize; k++)
            {
                String spWord = spWordList.get(k);
                String orgWord = orgWordList.get(k);

                if (!orgWord.equals(spWord))
                {
                    if(!synonymsMap.containsKey(orgWord))
                        return Boolean.FALSE;

                    if (!synonymsMap.get(orgWord).contains(spWord))
                        return Boolean.FALSE;

                }
            }
            return Boolean.TRUE;
        }
    }


    public class WordCollection {

        private String data;
        private List<SplitWords> splitWordsList= new ArrayList<>();

        public WordCollection() {
            this.data = null;
        }

        public WordCollection(String text, Integer splitWordsSize) {
            this.data = text;
            this.splitWordsList = extractSplitWordsListsFromData(text, splitWordsSize);
        }

        public WordCollection(String text, List<SplitWords> splitWordsList) {
            this.data = text;
            this.splitWordsList = splitWordsList;
        }

        public String getData() {
            return data;
        }

        public void setData(String text) {
            this.data = text;
        }

        public List<SplitWords> getSplitWordsList() {
            return splitWordsList;
        }

        public void setSplitWordsList(List<SplitWords> splitWordsList) {
            this.splitWordsList = splitWordsList;
        }

        /**
         *
         * @param txtData
         * @param splitWordsSize
         * @return
         */
        public List<SplitWords> extractSplitWordsListsFromData(String txtData, int splitWordsSize) {
            List<SplitWords> splitWordsList = new ArrayList<SplitWords>();
            txtData = txtData.replaceAll("[^a-zA-Z0-9 ]", "").trim().replaceAll("\\s+", " ").toLowerCase();
            String[] splitWordArray = txtData.split(" ");

            if (splitWordArray.length < splitWordsSize) {
                return splitWordsList;
            }

            for (int i=0; i<= splitWordArray.length - splitWordsSize; i++) {
                SplitWords splitWords = new SplitWords(splitWordsSize);
                for (int j=i; j<i+splitWordsSize; j++) {
                    splitWords.addSplitWord(splitWordArray[j]);
                }
                splitWordsList.add(splitWords);
            }
            return splitWordsList;
        }

        @Override
        public String toString() {
            return this.data;
        }
    }

}

