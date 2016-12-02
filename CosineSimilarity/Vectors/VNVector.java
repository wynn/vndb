package CosineSimilarity.Vectors;

import java.util.HashSet;

/**
 * Created by Aria on 2016/11/30.
 * Base cosine similarity vector
 * Each vector has a name, a set of components, and possibly an ID
 */
public abstract class VNVector {
    String name;
    HashSet<Integer> components;
    String id = "";

    public String toString(){
        if(id.length() > 0) {
            return name + " (" + id + ")";
        }
        return name;
    }

    public String getID() {
        return id;
    }

    public HashSet<Integer> getComponents() {
        return components;
    }
}
