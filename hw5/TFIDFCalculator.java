import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TFIDFCalculator{
    public static void main(String[] args) {
        String docs_path = args[0];
        String test_file = args[1];
        int[] docSizes = new int[60000];
        int docsNum = 0;
        //CustomHashMap hashMap = new CustomHashMap(60050);
        List<Trie> tfList = new ArrayList<Trie>();
        Trie idfTrie = new Trie();
        //build a hashmap read all the docs, and build each as a trie(calculate the word's TF)
        //build a trie, calculate all the words 
        try (BufferedReader reader = new BufferedReader(new FileReader(docs_path))) {
            String line;
            StringBuilder sb = new StringBuilder();
            int counter = 0;
            while ((line = reader.readLine()) != null) {
                // Process each line: convert to lower case and replace non-letter characters with space
                line = line.toLowerCase().replaceAll("[^a-z]", " ");
                line = line.trim() + " ";
                //System.out.println(line);
                // Append the processed line to the StringBuilder
                sb.append(line);
                counter++;
                // Check if we've read five lines
                if (sb.length() > 0 && counter == 5) {
                    //create a document
                    counter = 0;
                    tfList.add(new Trie()); //add new doc
                    List<String> solvedStrings = new ArrayList<String>();
                    solvedStrings.clear();
                    // Add the result to the list and reset the StringBuilder
                    String docString = sb.toString();
                    sb.setLength(0);
                    // Split the string by space
                    String[] words = docString.split(" +");
                    docSizes[docsNum] = words.length;
                    // if(docsNum == 0){
                    //     for (String word : words) System.out.println(":" + word);
                    //     System.out.println("docsSize: " + docSizes[docsNum]);
                    // }
                                 
                    
                    for(String word : words){
                        //System.out.println(word);
                        //tf(list of trie)
                        boolean inTrie = tfList.get(docsNum).search(word);
                        if(inTrie){
                            tfList.get(docsNum).search_increase(word, true);
                        }else{
                            tfList.get(docsNum).insert(word);
                        }
                        //idf(trie)
                        inTrie = idfTrie.search(word);
                        boolean isSolved = solvedStrings.contains(word); //already solved once in this document
                        if(inTrie){
                            if(isSolved == false) idfTrie.search_increase(word, true);
                        }else{
                            idfTrie.insert(word);
                        }
                        solvedStrings.add(word);
                        
                    }    
                    docsNum++;            
                }//end of if
            }//end of reader while
        } catch (IOException e) {
            //e.printStackTrace();
        }
        //read input and write
        try{
            String line;
            BufferedReader tcReader = new BufferedReader(new FileReader(test_file));
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("output.txt")));

            int inputNum = 0;
            String firstLine = tcReader.readLine();
            String secondLine = tcReader.readLine();
            String[] inpuStrings = firstLine.split(" ");
            inputNum = inpuStrings.length;
            int[] inputIdx = Arrays.stream(secondLine.split(" ")).mapToInt(Integer::parseInt).toArray();
            //for(int num : inputIdx) System.out.println(num);
            //System.out.println(tfList.size());
            for(int i = 0; i < inputNum; i++){
                Trie targetTrie = tfList.get(inputIdx[i]);
                //System.out.println(targetTrie.getWordCounter(inpuStrings[i]));
                double tfIdf = tfIdfCalculate(inpuStrings[i],docSizes,docsNum,inputIdx[i],targetTrie,idfTrie);
                //System.out.println(tfIdf);
                writer.write(String.format("%.5f", tfIdf) + " ");
            }
            writer.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        
    }
    public static double tf(int[] docSizes, String term, int docIdx, Trie doc) {
        int number_term_in_doc = doc.getWordCounter(term);
        //System.out.printf("tf: %d %d\n",number_term_in_doc,docSizes[docIdx]);
        return (double)number_term_in_doc / (docSizes[docIdx]);
    }
    public static double idf(int totalDocs, Trie idfTrie, String term) {
        int number_doc_contain_term = idfTrie.getWordCounter(term);
        //System.out.println("idf: " + number_doc_contain_term);
        return Math.log((double)totalDocs / number_doc_contain_term);
    }
    
    public static double tfIdfCalculate(String term, int[] docSizes, int totalDocs, int docIdx, Trie doc, Trie idfTrie) {
        return tf(docSizes,term,docIdx,doc) * idf(totalDocs, idfTrie, term);
    }
}

class TrieNode {
    TrieNode[] children = new TrieNode[26];
    boolean isEndOfWord = false;
    int counter = 0;

    public void increaseCounter(){
        this.counter ++;
    }
    public int getCounter(){
        return this.counter;
    }
}

class Trie {
    TrieNode root = new TrieNode();

    // inset a word to Trie
    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            if (node.children[c - 'a'] == null) {
                node.children[c - 'a'] = new TrieNode();
            }
            node = node.children[c - 'a'];
        }
        node.isEndOfWord = true;
        node.increaseCounter();
    }

    // search whether the word is in Trie
    public boolean search(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node = node.children[c - 'a'];
            if (node == null) {
                return false;
            }
        }
        return node.isEndOfWord;
    }
    public boolean search_increase(String word,  boolean increaseCase) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node = node.children[c - 'a'];
            if (node == null) {
                return false;
            }
        }
        //increase while found or not found
        if(increaseCase == true){
            //increase if found
            if(node.isEndOfWord == true) node.increaseCounter();
        }else{
            //increase if not found
            if(node.isEndOfWord == false) node.increaseCounter();
        }
        return node.isEndOfWord;
    }
    public int getWordCounter(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node = node.children[c - 'a'];
            if (node == null) {
                //System.out.println("isNull");
                return 0;
            }
        }
        //System.out.println("is Not Null");
        return node.getCounter();
    }
}