/*
 * tb
 * java RegExp tc0 abc b 3 > ans0
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RegExp {
    
    public static void main(String[] args) {
        String str1 = args[1];
        String str2 = args[2];
        int s2Count = Integer.parseInt(args[3]);
        //upper
        str1 = str1.toUpperCase();
        str2 = str2.toUpperCase();

        //For your testing of input correctness
        // System.out.println("The input file:"+args[0]);
        // System.out.println("str1="+str1);
        // System.out.println("str2="+str2);
        // System.out.println("num of repeated requests of str2 = "+s2Count);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.toUpperCase();
                //judge
                char judge1 = 'N' , judge2 = 'N', judge3 = 'N', judge4 = 'N';
                judge1 = judgePalindrome(line);
                judge2 = judgeStr1(line, str1);
                judge3 = judgeStr2(line, str2, s2Count);
                judge4 = judgeAXB(line);
                

                //System.out.println(line);
                System.out.printf("%c,%c,%c,%c\n", judge1,judge2,judge3,judge4);
            }
            reader.close();

        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    public static char judgePalindrome(String input_line){
        int length = input_line.length();
        for(int i = 0; i < length / 2; i++){
            if(input_line.charAt(i) != input_line.charAt(length -1 -i)){
                return 'N';
            }
        }
        return 'Y';
    }
    public static char judgeStr1(String input_line, String str1){
        int length_line = input_line.length();
        int length_str1 = str1.length();
        for(int i = 0; i < (length_line - (length_str1-1)); i++){
            //check every char of input if it's the first char of str1
            if(input_line.charAt(i) == str1.charAt(0)){
                boolean matched = true;
                for(int j = 0; j < length_str1; j++){
                    if(input_line.charAt(i+j) != str1.charAt(j)){
                        matched = false;
                        break;
                    }
                }
                if(matched) return 'Y';
            }
        }
        return 'N';
    }
    public static char judgeStr2(String input_line, String str2, int s2Counter){
        int length_line = input_line.length();
        int numCounter = 0;
        for(int i = 0; i < (length_line - (str2.length()-1)); i++){
            //check every char of input if it's the first char of str2
            if(input_line.charAt(i) == str2.charAt(0)){
                boolean matched = true;
                for(int j = 0; j < str2.length(); j++){
                    if(input_line.charAt(i+j) != str2.charAt(j)){
                        matched = false;
                        break;
                    }
                }
                if(matched) numCounter++;
            }
        }
        if(numCounter >= s2Counter) return 'Y';
        else return 'N';
    }
    public static char judgeAXB(String input_line){
        int length_line = input_line.length();
        for(int i = 0; i < length_line; i++){
            if(input_line.charAt(i) == 'A' && i != (length_line -1)){
                int counterB = 0;
                for(int j = 1; (i + j) < length_line; j++){
                    if(input_line.charAt(i+j) == 'B'){
                        counterB++;
                    }else{
                        counterB = 0;
                    }
                    if(counterB == 2){
                        return 'Y';
                    }
                }
            }
        }
        return 'N';
    }
}