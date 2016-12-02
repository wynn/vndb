package CosineSimilarity.Vectors;

import java.util.HashSet;

/**
 * Created by Aria on 1/2/2016.
 */
public class VNCharacter extends VNVector{

    public VNCharacter(String name, HashSet<Integer> traits) {
        this.name = name;
        this.components = traits;
    }

    public VNCharacter(String name, HashSet<Integer> traits, String id) {
        this.name = name;
        this.components = traits;
        this.id = id;
    }
}
