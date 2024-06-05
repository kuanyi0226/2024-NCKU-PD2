import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class BuildIndex{
    public static void main(String[] args) {
        String txtPath = args[0];
        //build index
        try {
            Indexer idx = new Indexer(txtPath);
            String fileName = txtPath.substring(txtPath.lastIndexOf('/') + 1);
            fileName = fileName.substring(0, fileName.lastIndexOf('.'));
            fileName = fileName + ".ser";

            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(idx);
            
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();	
        }   
    }
}

