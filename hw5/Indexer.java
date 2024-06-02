import java.io.Serializable;

public class Indexer implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
    private transient int counter;
    // Indexer(String name){
    //     this.name = name;
    // }
}
