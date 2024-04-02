import java.util.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

public class CodeGenerator {
    public static void main(String[] args) {
        if (args.length == 0) {
            //System.err.println("Please enter the name of mermaid file");           
        }
        else {
            // get input
            String fileName = args[0];
            String mermaidCode = "";

            FileReader mermaidCodeReader = new FileReader();
            mermaidCode = mermaidCodeReader.read(fileName);
            Parser.splitByClass(mermaidCode);
       }
    }
}
class FileReader {
    public String read(String fileName) {
        try {
            return Files.readString(Paths.get(fileName));
        }
        catch (IOException e) {
            //System.err.println("Cant read file " + fileName);
            //e.printStackTrace();
            return "";
        }
    }
}
class Parser {
    public static String splitByClass(String input) {
        List<ClassComponent> classList = new ArrayList<>();
        String[] lines = input.split("\n");
        boolean paranthesesMode = false;
        String currClass = ""; //used in paranthesesMode
        //prepare class component data
        for (String line : lines) {
            //System.out.println(line);
            String trim_line = line.trim();
            if(trim_line.equals("\n") || trim_line.equals("")) continue;

            if(trim_line.startsWith("classDiagram")){
                //do nothing
            }else if(trim_line.startsWith("class")){
                //class name
                String[] split_line = trim_line.split(" +");
                // for (String currLine : split_line) {
                //     System.out.print(currLine + " ");
                // }
                // System.out.println();
                String className = split_line[1];
                //add the class to list
                boolean exist = false;
                for(ClassComponent classComponent : classList){
                    if(classComponent.className.equals(className)) exist = true;
                }
                if(!exist) classList.add(new ClassComponent(className));
                //change the paranthesesMode or not
                if(!(trim_line.endsWith("{"))){
                    // not {}
                    paranthesesMode = false;
                }else{
                    // {}
                    paranthesesMode = true;
                    currClass = className;
                }

            }else if(trim_line.startsWith("}")){
                if(paranthesesMode) paranthesesMode = false;
            }else{
                //judge
                boolean isMethod = false;
                if(trim_line.contains("(") && trim_line.contains(")")) isMethod = true;
                String[] split_line = trim_line.split(" +");
                // for (String currLine : split_line) {
                //     System.out.print(currLine + " ");
                // }
                // System.out.println();
                boolean isGetter = false;
                boolean isSetter = false;
                if(isMethod && trim_line.contains("get")) isGetter = true;
                else if(isMethod && trim_line.contains("set")) isSetter = true;
                String Name = "";
                String access = "private";
                String Type = "int";
                String className = "";

                String methodName = "";
                String methodType = "void";
                String methodAccess = "public";
                //cut for method
                if(isMethod){
                    if(trim_line.contains("+")){
                        methodAccess = "public";
                        methodName = (trim_line.substring(trim_line.indexOf("+")+1, trim_line.indexOf(")")+1)).trim();
                    }else{
                        methodAccess = "private";
                        methodName = (trim_line.substring(trim_line.indexOf("-")+1, trim_line.indexOf(")")+1)).trim();
                    }
                    methodType = (trim_line.substring(trim_line.indexOf(")")+1)).trim();
                    if(methodType == "" || methodType == null) methodType = "void";

                }else{

                }
                
                if(paranthesesMode){
                    //System.out.println("{} mode");
                    className = currClass;
                    if(!isMethod){   
                        Name = split_line[1];
                        if(split_line[0].charAt(0) == '+') access = "public";
                        Type = split_line[0].substring(1);
                    }else{
                        Type = methodType;
                        access = methodAccess;
                        Name = methodName;
                    }

                }else{
                    className = (trim_line.substring(0, trim_line.indexOf(":"))).trim();
                    if(!isMethod){
                        Name = split_line[3];
                        if(split_line[2].charAt(0) == '+') access = "public";
                        Type = split_line[2].substring(1);
                    }else{         
                        Type = methodType;
                        access = methodAccess;
                        Name = methodName;         
                    }

                }
                for(ClassComponent classComponent : classList){
                    if(classComponent.className.equals(className)){
                        if(!isMethod){
                            classComponent.memberList.add(new AttriComponent(access, Type, Name));
                            //System.out.println("Is Attribute");
                        }else{
                            if(isGetter){
                                classComponent.memberList.add(new GetterSetterComponent(access,Type,isGetter,Name));
                            }
                                
                            else if(isSetter)
                                classComponent.memberList.add(new GetterSetterComponent(access,Type,isGetter,Name));
                            else{
                                classComponent.memberList.add(new FunctionComponent(access,Type,Name));
                            }


                        }                    
                        break;
                    }
                }           
            }

        }
        //print result
        for(ClassComponent classComponent : classList){   
            String output = classComponent.className + ".java";;
            File file = new File(output);  
            try {
                if (!file.exists()) {         
                    file.createNewFile();
                }
                String content = "public class " + classComponent.className + " {\n";
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                bw.write(content);

                for(Component component : classComponent.memberList){
                    content = component.printMember();
                    try {
                        bw.append("    ");
                        bw.append(content);
                    } catch(Exception e){}
                }
                bw.append("}");
                bw.close();
            } catch (Exception e) {
                // TODO: handle exception
            } 
        }
        return "";
    }
}
class Component{
    public String printMember(){
        return "";
    }
}
class ClassComponent{
    String className;
    List<Component> memberList = new ArrayList<>();
    ClassComponent(String className){
        this.className = className;
    }
    public void addMember(Component member){
        this.memberList.add(member);
    }
    public void writeClass(){
        int currIndent = 0;
    }
}
class AttriComponent extends Component{
    public String access;
    public String fieldType;
    public String fieldName;
    AttriComponent(String access, String fieldType, String fieldName){
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.access = access;
    }
    @Override
    public String printMember(){
        return (access + " " + fieldType + " " + fieldName + ";\n");   
    }
}
class FunctionComponent extends Component{
    public String access;
    public String functionType;
    public String functionName;
    FunctionComponent(String access, String functionType, String functionName){
        this.functionType = functionType;
        this.access = access;
        this.functionName = functionName;
    }
    @Override
    public String printMember(){
        String returnValue;
        if(functionType.equals("int")) returnValue = "return 0";
        else if(functionType.equals("String")) returnValue = "return \"\"";
        else if(functionType.equals("boolean")) returnValue = "return false";
        else returnValue = "";
        String outputName = functionType + " " + functionName;
        return (access + " " + outputName + " {" + returnValue + ";}\n");
    }
}
class GetterSetterComponent extends Component{
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
    @Override
    public String printMember(){
        //String preString = isGetter ? "get" : "set";
        String nameString = "";
        String modeString = "";
        if(isGetter) modeString = "get";
        else modeString = "set";
        nameString = functionName.substring(functionName.indexOf(modeString)+3, functionName.indexOf("("));
        nameString = nameString.substring(0,1).toLowerCase() + nameString.substring(1);
        
        if(isGetter){
            //getter
            return (access + " " + functionType + " " + functionName + " {\n") +
            ("        return " + nameString + ";\n") +
            ("    }\n");
        }else{
            //setter
            return (access + " void " + functionName + " {\n") +
            ("        this." + nameString + " = " + nameString + ";\n") +
            ("    }\n");
        }
    }
}