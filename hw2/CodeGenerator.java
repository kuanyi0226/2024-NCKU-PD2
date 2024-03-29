public class CodeGenerator {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("請輸入mermaid檔案名稱");           
        }
        else {
            // get input
            String fileName = args[0];
            String mermaidCode = "";
            String temp;

            FileReader mermaidCodeReader = new FileReader();
            mermaidCodeReader.read(fileName);
            temp = Parser.splitByClass(mermaidCode);
       }
    }
}
class FileReader {
    public String read(String fileName) {
		return "";		
    }
    
}
class Parser {
		public static String splitByClass(String input) {
			return "";
		}
}