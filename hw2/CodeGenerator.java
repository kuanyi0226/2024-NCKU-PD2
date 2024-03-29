public class CodeGenerator {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Please enter the name of mermaid file");           
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
class FieldComponent{
    public String access;
    public String fieldType;
    public String fieldName;
    FieldComponent(String access, String fieldType, String fieldName){
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.access = access;
    }
    public void printField(){
        System.out.println(access + " " + fieldType + " " + fieldName + ";");
    }
}
class FunctionComponent{
    public String access;
    public String functionType;
    FunctionComponent(String access, String functionType){
        this.functionType = functionType;
        this.access = access;
    }
    public void printFunction(){
        String returnValue;
        if(functionType.equals("int")) returnValue = "0";
        else if(functionType.equals("String")) returnValue = "\"\"";
        else if(functionType.equals("boolean")) returnValue = "false";
        else returnValue = "0";
        String functionName = functionType + functionType.toLowerCase() + "Function";
        System.out.println(access + " " + functionName + "() {return " + returnValue + ";}");
    }
}
class GetterSetterComponent{
    public String access;
    public String functionType;
    public boolean isGetter = true; //0: setter, 1: getter
    public String functionName;
    GetterSetterComponent(String access, String functionType, boolean isGetter, String functionName){
        this.functionType = functionType;
        this.access = access;
        this.isGetter = isGetter;
        this.functionName = functionName;
    }
    public void printFunction(){
        String preString = isGetter ? "get" : "set";
        String nameString = preString + functionName + "()";
        if(isGetter){
            //getter
            System.out.println(access + " " + functionType + " " + nameString + " {");
            System.out.println("    return" + functionName.toLowerCase() + ";");
            System.out.println("}");
        }else{
            //setter
            System.out.println(access + " " + functionType + " " + nameString + " {");
            System.out.println("    this." + functionName.toLowerCase() + ";");
            System.out.println("}");
        }
    }
}