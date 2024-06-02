import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class BuildIndex{
    Indexer idx;
    BuildIndex(){
        idx = new Indexer();
        buildIndex();
    }

    private void buildIndex(){
        try {
            FileOutputStream fos = new FileOutputStream("example.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(idx);
            
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();	
        }
    }

}