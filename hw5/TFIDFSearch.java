import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TFIDFSearch {
    private static final String Set = null;

    public static void main(String[] args) {
        String fileName = args[0];
        String test_path = args[1];
        //DeIndex
        try {
            FileInputStream fis = new FileInputStream(fileName + ".ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            Indexer deserializedIdx = (Indexer) ois.readObject();
            ois.close();
            fis.close();

            //read input and write
            String line;
            BufferedReader tcReader = new BufferedReader(new FileReader(test_path));
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("output.txt")));

            //int inputNum = 0;
            int n = Integer.parseInt(tcReader.readLine());//read first line
            List<String> inputWords = new ArrayList<String>();
            int[] docSizes = deserializedIdx.docSizes;
            int docsNum = deserializedIdx.docsNum;
            boolean isAND = true;
            List<Pair> idPairList = new ArrayList<Pair>();
            //System.out.println(deserializedIdx.idfTrie.getWordCounter("preferable"));
            while((line = tcReader.readLine()) != null){
                isAND = initTestStrings(line, inputWords);
                //init id list
                Set<Integer> idSet = new HashSet<>();
                idSet.clear();
                idPairList.clear();
                for(int i = 0; i < inputWords.size(); i++){
                    Set<Integer> currWordSet = new HashSet<>();
                    currWordSet.clear();
                    if(deserializedIdx.idfTrie.search(inputWords.get(i))){
                        currWordSet = deserializedIdx.idfTrie.getNode(inputWords.get(i)).idList.keySet();
                    }
                    if(i == 0){
                        idSet.addAll(currWordSet);
                    }else{
                        if(isAND){ //AND
                            idSet.retainAll(currWordSet);
                        }else{ //OR
                            idSet.addAll(currWordSet);
                        }
                    }
                }

                for(Integer curr_id : idSet){
                    int idIdx = curr_id.intValue();
                    Pair tempPair = new Pair(curr_id, 0);
                    for(int i = 0; i < inputWords.size(); i++){
                        tempPair.addSecond(tfIdfCalculate(inputWords.get(i), docSizes,docsNum,idIdx,deserializedIdx.idfTrie));
                    }
                    idPairList.add(tempPair);

                }
                //sort idPairList
                Collections.sort(idPairList, new Comparator<Pair>(){
                    @Override
                    public int compare(Pair p1, Pair p2){
                        int valueCompare = Double.compare(p2.second, p1.second);
                        if (valueCompare != 0) {
                            return valueCompare;
                        } else {
                            return Integer.compare(p1.first, p2.first);
                        }
                    }
                });
                //write results
                int writeCounter = n;
                for(int i = 0; (i < idPairList.size()) && writeCounter > 0;i++){
                    writeCounter--;
                    writer.write(String.format("%d",idPairList.get(i).first));
                    if(writeCounter > 0) writer.write(" ");
                }
                while(writeCounter > 0){
                    writeCounter--;
                    writer.write("-1");
                    if(writeCounter > 0) writer.write(" ");
                }
                writer.write("\n");
            }
            // String[] inpuStrings = firstLine.split(" ");
            // inputNum = inpuStrings.length;
            // int[] inputIdx = Arrays.stream(secondLine.split(" ")).mapToInt(Integer::parseInt).toArray();
            // //for(int num : inputIdx) System.out.println(num);
            // //System.out.println(tfList.size());
            // for(int i = 0; i < inputNum; i++){
            //     Trie targetTrie = tfList.get(inputIdx[i]);
            //     //System.out.println(targetTrie.getWordCounter(inpuStrings[i]));
            //     double tfIdf = tfIdfCalculate(inpuStrings[i],docSizes,docsNum,inputIdx[i],targetTrie,idfTrie);
            //     //System.out.println(tfIdf);
            //     writer.write(String.format("%.5f", tfIdf) + " ");
            // }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        }
    
    }

    public static double tf(int[] docSizes, String term, int docIdx, Trie idfTrie) {
        int number_term_in_doc = idfTrie.map_getTF(term, docIdx);
        //System.out.printf("tf: %d %d\n",number_term_in_doc,docSizes[docIdx]);
        return (double)number_term_in_doc / (docSizes[docIdx]);
    }
    public static double idf(int totalDocs, Trie idfTrie, String term) {
        int number_doc_contain_term = idfTrie.getWordCounter(term);
        //System.out.println("idf: " + number_doc_contain_term);
        if(number_doc_contain_term == 0) return 0;
        else return Math.log((double)totalDocs / number_doc_contain_term);
    }
    
    public static double tfIdfCalculate(String term, int[] docSizes, int totalDocs, int docIdx, Trie idfTrie) {
        return tf(docSizes,term,docIdx,idfTrie) * idf(totalDocs, idfTrie, term);
    }

    public static boolean initTestStrings(String inputLine, List<String> inputStrings){
        boolean isAND = inputLine.contains("AND");
        String[] parts;
        inputStrings.clear();
        if(isAND){
            parts = inputLine.split("AND");
        }else{
            parts = inputLine.split("OR");
        }

        for (String part : parts){
            inputStrings.add(part.trim());
        }

        return isAND;
    }

    // public static void initIdList(List<String> inputWords,List<Pair> idList){
    //     idList.clear();
    //     Set<Integer> idSet = 
        
    // }
}


class Pair{
    public int first = 0; //id
    public double second = 0; //tf-idf value

    Pair(int first, double second){
        this.first = first;
        this.second = second;
    }

    public void setFirst(int num){
        this.first = num;
    }
    public void setSecond(double num){
        this.second = num;
    }
    public void addSecond(double num){
        this.second = this.second + num;
    }
}