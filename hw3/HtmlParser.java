import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;

//import javax.swing.text.html.parser.Element;

public class HtmlParser {
    public static void main(String[] args) {
        String mode = args[0];
        if(mode.equals("0")){
            //mode == "0"
            List<String> stockTitles = new ArrayList<>();
            List<String> stockValues = new ArrayList<>();
            int currDay = 1;
            //read datas from website
            try {
                Document doc = Jsoup.connect("https://pd2-hw3.netdb.csie.ncku.edu.tw/").get();
                String dayTitle = doc.title();
                currDay = Integer.parseInt(dayTitle.substring(3));
                //System.out.println(currDay);              
                Element stockTable = doc.select("table").get(0);
                Elements titles = stockTable.select("tr").get(0).select("th");
                Elements values = stockTable.select("tr").get(1).select("td");
                for(Element title : titles){
                    stockTitles.add(title.text());
                }              
                for(Element value : values){
                    stockValues.add(value.text());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //write csv
            //title
            String fileName = "data.csv";
            File file = new File(fileName);
            try {
                if (!file.exists()) {         
                    file.createNewFile();
                }
                
                BufferedReader br = new BufferedReader(new FileReader(file));
                StringBuilder sb = new StringBuilder();
                String line;
                int currLine = 0;
                //read
                while (currLine < 31) {
                    line = br.readLine();
                    // System.out.println("currLine: " + currLine);
                    // System.out.println(line);
                    if(currLine == 0){
                        //title
                        for(int i = 0; i < stockTitles.size(); i++){
                            sb.append(stockTitles.get(i));
                            if(i < (stockTitles.size() - 1)){
                                sb.append(",");
                            }else{
                                if(currLine != 30){
                                    sb.append("\n");
                                }                              
                            }
                        }
                    }
                    else if (currLine == currDay) {
                        //value
                        for(int i = 0; i < stockValues.size(); i++){
                            sb.append(stockValues.get(i));
                            if(i < (stockValues.size() - 1)){
                                sb.append(",");
                            }else{
                                if(currLine != 30){
                                    sb.append("\n");
                                }
                            }
                        }
                    } else {
                        if(line != null){
                            sb.append(line);
                            if(currLine != 30){
                                sb.append("\n");
                            }
                        }else{
                            if(currLine != 30){
                                sb.append("\n");
                            }
                        }             
                    }
                    currLine++;
                }
                br.close();
                //write
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                bw.write(sb.toString());
                bw.close();
            } catch (Exception e) {
                // TODO: handle exception
            } 
        }else{
            //mode == "1"
            int task = Integer.parseInt(args[1]);
            String inputFileName = "data.csv";
            String outputFileName = "output.csv";
            File inputFile = new File(inputFileName);
            File outputFile = new File(outputFileName);
            List<String> stockNameList = new ArrayList<>();
            List<double[]> stockPriceList = new ArrayList<>();
            if(task > 0){
                try{
                    if (!outputFile.exists()) {         
                        outputFile.createNewFile();
                    }
                    BufferedReader bReader = new BufferedReader(new FileReader(inputFile));
                    String line;
                    int lineNum = 0;
                    while((line = bReader.readLine()) != null){
                        String[] parts = line.split(",");
                        if(lineNum == 0){
                            for(String part : parts){
                                stockNameList.add(part);
                                stockPriceList.add(new double[30]);
                            }
                        }else{
                            int counter = 0; //iteration of diff stock 
                            for(int i = 0; i < stockNameList.size();i++){
                                double[] modifyArray = stockPriceList.get(counter); // first stock's price
                                modifyArray[lineNum - 1] = Double.parseDouble(parts[i]);
                                counter++;
                            }
                        }
                        lineNum ++;
                    }
                    bReader.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            switch (task) {
                case 1:
                case 2:
                case 4:
                    {
                    String stock = args[2];
                    stock = stock.toUpperCase();
                    int start = Integer.parseInt(args[3]);
                    int end = Integer.parseInt(args[4]);
                    int stockNameIndex = 0;
                    //find the index of stock in the list
                    for(int i = 0; i < stockNameList.size(); i++){
                        if(stockNameList.get(i).equals(stock)){
                            stockNameIndex = i;
                            break;
                        }
                    }
                    double[] currValueList = stockPriceList.get(stockNameIndex);
                    try{
                        //read previous outputs
                        BufferedReader bReader = new BufferedReader(new FileReader(outputFile));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while((line = bReader.readLine()) != null){
                            sb.append(line).append("\n");
                        }
                        bReader.close();
                        BufferedWriter bWriter = new BufferedWriter(new FileWriter(outputFile));
                        sb.append(stock + "," + args[3] + "," + args[4] + "\n");
                        if(task == 1){
                            int outputNum = end - start - 3; // # of values we should output
                            for(int outputIndex = 0; outputIndex < outputNum; outputIndex++){                          
                                //sum
                                double sum = 0;
                                for(int i = 0; i < 5; i++){
                                    sum += currValueList[start - 1 + i + outputIndex];
                                }
                                double avg = sum / 5;
                                // Round to two decimal places
                                DecimalFormat df = new DecimalFormat("#.##");
                                String averageString = df.format(avg);
                                // Eliminate "0" if the string ends with 0
                                if (averageString.endsWith("0")) {
                                    averageString = averageString.substring(0, averageString.length() - 1);
                                }
                                
                                sb.append(averageString);
                                //System.out.println(averageString);
                                if(outputIndex < outputNum -1){
                                    sb.append(",");
                                }         
                            }
                        }else if(task == 2){
                            //mean
                            double sum = 0;
                            for(int i = start - 1; i < end; i++){
                                sum += currValueList[i];
                            }
                            double avg = sum / (end - start + 1);      
                            //variance
                            double variance = 0;
                            for(int i = start - 1; i < end; i++){
                                double value = currValueList[i];
                                double tempDiff = value - avg;
                                variance += (tempDiff * tempDiff);
                            }
                            variance = variance / (end - start);                         
                            double stv = squareRoot(variance);
                            // Round to two decimal places
                            DecimalFormat df = new DecimalFormat("#.##");
                            String stvString = df.format(stv);
                            // Eliminate "0" if the string ends with 0
                            if (stvString.endsWith("0")) {
                                stvString = stvString.substring(0, stvString.length() - 1);
                            }                            
                            sb.append(stvString);                        
                        }else if(task == 4){
                            //b1 and b0
                            double priceSum = 0;
                            for(int i = start - 1; i < end; i++){
                                priceSum += currValueList[i];
                            }
                            double Y_bar = priceSum / (end - start + 1);
                            double daySum = 0;
                            for(int i = start - 1; i < end; i++){
                                daySum += i + 1;
                            }
                            double t_bar = daySum / (end - start + 1);
                            double tempSum1 = 0;
                            double tempSum2 = 0;
                            for(int i = start - 1; i < end; i++){
                                tempSum1 += (i + 1 - t_bar) * (currValueList[i] - Y_bar);
                            }
                            for(int i = start - 1; i < end; i++){
                                tempSum2 += (i + 1 - t_bar) * (i + 1 - t_bar);
                            }
                            double b1 = tempSum1 / tempSum2;
                            double b0 = Y_bar - (b1 * t_bar);
                            // Round to two decimal places
                            DecimalFormat df = new DecimalFormat("#.##");
                            String b0String = df.format(b0);
                            String b1String = df.format(b1);
                            if (b0String.endsWith("0")) {
                                b0String = b0String.substring(0, b0String.length() - 1);
                            }   
                            if (b1String.endsWith("0")) {
                                b1String = b1String.substring(0, b1String.length() - 1);
                            }                          
                            sb.append(b1String + "," + b0String + "\n");
                        }                        
                        bWriter.write(sb.toString());
                        bWriter.close();         
                    }catch(IOException e){e.printStackTrace();}
                    }   
                    break;
                case 3:
                    {
                    int start = Integer.parseInt(args[3]);
                    int end = Integer.parseInt(args[4]);
                    //calculate standard deviation
                    List<Double> stvList = new ArrayList<>();
                    for(double[] stockPrice : stockPriceList){
                        double mean = calculateMean(stockPrice, start, end);
                        double variance = calculateVariance(stockPrice, mean, start, end);
                        double stv = squareRoot(variance);
                        stvList.add(stv); 
                    }
                    //find top 3
                    List<String> top3Name = new ArrayList<>();
                    List<Double> top3Value = new ArrayList<>();
                    for(int i = 0; i < 3; i++){
                        double max = 0;
                        int maxIndex = -1;
                        for(int j = 0; j < stvList.size(); j++){
                            double currStv = stvList.get(j);
                            if(currStv > max){
                                max = currStv;
                                maxIndex = j;
                            }
                        }
                        if(maxIndex != -1){
                            top3Name.add(stockNameList.get(maxIndex));
                            top3Value.add(max);
                            stvList.set(maxIndex, 0.0);
                        }
                    }
                    try{
                        //read previous outputs
                        BufferedReader bReader = new BufferedReader(new FileReader(outputFile));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while((line = bReader.readLine()) != null){
                            sb.append(line).append("\n");
                        }
                        bReader.close();
                        BufferedWriter bWriter = new BufferedWriter(new FileWriter(outputFile));
                        sb.append(top3Name.get(0) + "," + top3Name.get(1) + "," + top3Name.get(2) + ",");
                        sb.append(args[3] + "," + args[4] + "\n");    
                        // Round to two decimal places
                        for(int i = 0; i < 3; i++){
                            DecimalFormat df = new DecimalFormat("#.##");
                            String stvString = df.format(top3Value.get(i));
                            if (stvString.endsWith("0")) {
                                stvString = stvString.substring(0, stvString.length() - 1);
                            }                            
                            sb.append(stvString); 
                            if(i < 2) sb.append(",");
                        }                                           
                        sb.append("\n");  
                        bWriter.write(sb.toString());
                        bWriter.close();         
                    }catch(IOException e){e.printStackTrace();}
                    }
                    break;
                default:
                    //task == 0                     
                    try {
                        if (!outputFile.exists()) {         
                            outputFile.createNewFile();
                        }
                        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
                        String line;
                        int counter = 0;
                        while ((line = reader.readLine()) != null) {
                            writer.write(line);
                            if(counter < 30){
                                writer.newLine();
                            } 
                            counter ++;         
                        }
                        reader.close();
                        writer.close();
                    } catch (IOException e) {
                        //e.printStackTrace();
                    }
                    break;
            }
        }
        
    }
    public static double squareRoot(double number) {
        double guess = number / 2; // Initial guess 
        double precision = 0.000000001; // precision
        double diff = 1; // Initialize the difference

        while (diff > precision) {
            double newGuess = (guess + number / guess) / 2;
            diff = newGuess - guess;
            //Math.abs(diff)
            if (diff < 0) {
                diff = -diff;
            }
            guess = newGuess;
        }

        return guess;
    }
    public static double calculateMean(double[] values, int start, int end) {
        double sum = 0;
        for (int i = start - 1; i < end && i < values.length; i++) {
            sum += values[i];
        }
        return sum / (end - start + 1);
    }

    public static double calculateVariance(double[] values, double mean, int start, int end) {
        double sumOfSquares = 0;
        for (int i = start - 1; i < end && i < values.length; i++) {
            sumOfSquares += (values[i] - mean) * (values[i] - mean);
        }
        return sumOfSquares / (end - start);
    }
}
