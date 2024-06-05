import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Indexer implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
    private transient String txtPath;
    private transient int counter;
    int[] docSizes = new int[60000];
    int docsNum = 0;
    //transient List<Trie> tfList = new ArrayList<Trie>();
    Trie idfTrie = new Trie();

    Indexer(String txtPath){
        this.txtPath = txtPath;
        initTrie();
    }

    private void initTrie(){
        try (BufferedReader reader = new BufferedReader(new FileReader(txtPath))) {
            String line;
            StringBuilder sb = new StringBuilder();
            counter = 0;
            while ((line = reader.readLine()) != null) {
                // Process each line: convert to lower case and replace non-letter characters with space
                line = line.replaceAll("[^a-zA-Z]+", " ");
                line = line.toLowerCase();
                line = line.trim() + " ";
                //System.out.println(line);
                // Append the processed line to the StringBuilder
                sb.append(line);
                counter++;
                // Check if we've read five lines
                if (sb.length() > 0 && counter == 5) {
                    //create a document
                    counter = 0;
                    //tfList.add(new Trie()); //add new doc
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
                        
                        //if(docsNum == 0) System.out.println(word);
                        //tf(list of trie)
                        // boolean inTrie = tfList.get(docsNum).search(word);
                        // if(inTrie){
                        //     tfList.get(docsNum).search_increase(word, true);
                        // }else{
                        //     tfList.get(docsNum).insert(word);
                        // }
                        //idf(trie)
                        boolean inTrie = idfTrie.search(word);
                        boolean isSolved = solvedStrings.contains(word); //already solved once in this document
                        if(inTrie){
                            if(isSolved == false){
                                idfTrie.search_increase(word, true);
                            } 
                            idfTrie.map_increase(word, docsNum);
                        }else{
                            idfTrie.insert(word,docsNum);
                        }
                        solvedStrings.add(word);
                        
                    }    
                    docsNum++;            
                }//end of if
            }//end of reader while
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }
}

class TrieNode implements Serializable{
    TrieNode[] children = new TrieNode[26];
    boolean isEndOfWord = false;
    int counter = 0;
    Map<Integer,Integer> idList = new HashMap<>();
    //List<Integer> idList = new ArrayList<>();

    public void increaseCounter(){
        this.counter ++;
    }
    public int getCounter(){
        return this.counter;
    }
    public void increaseMapValue(int id){
        if(this.idList.containsKey(id)){
            Integer value = this.idList.get(id) + 1;
            //if(id == 0) System.out.printf("id:%d, value:%d\n", id, value);
            //int updataValue = 100;
            //if(value != null) updataValue = value.intValue() + 1;
            this.idList.put(id, value); //update the value
        }else{
            this.idList.put(id, 1);
        }
        
    }
}

class Trie implements Serializable{
    TrieNode root = new TrieNode();

    // inset a word to Trie
    public void insert(String word, int id) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            if (node.children[c - 'a'] == null) {
                node.children[c - 'a'] = new TrieNode();
            }
            node = node.children[c - 'a'];
        }
        node.isEndOfWord = true;
        node.increaseCounter();
        node.idList.put(id, 1);
        //if(id == 0) System.out.println("insert " + word);
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

    public void map_increase(String word, int id){
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node = node.children[c - 'a'];
        }
        node.increaseMapValue(id);
    } 

    public int map_getTF(String word, int id){
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node = node.children[c - 'a'];
            if (node == null) {
                return 0;
            }
        }
        if(node.idList.get(id) == null) return 0;
        else return node.idList.get(id);
    } 
    public TrieNode getNode(String word){
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node = node.children[c - 'a'];
            if (node == null) {
                //System.out.println("isNull");
                return null;
            }
        }
        //System.out.println("is Not Null");
        return node;
    }
}