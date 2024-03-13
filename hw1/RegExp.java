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
                boolean judge1 = judgePalindrome(line);
                

                System.out.println(line);
                System.out.println(judge1);
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean judgePalindrome(String input_line){
        int length = input_line.length();
        for(int i = 0; i < length / 2; i++){
            if(input_line.charAt(i) != input_line.charAt(length -1 -i)){
                return false;
            }
        }
        return true;
    }
}