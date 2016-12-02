package CosineSimilarity.Vectors;

import java.util.HashSet;

/**
 * Created by Aria on 12/27/2015.
 */
public class VisualNovel extends VNVector{

    public VisualNovel(String title, HashSet<Integer> tags) {
        this.name = title;
        this.components = tags;
    }

    public VisualNovel(String title, HashSet<Integer> tags, String id) {
        this.name = title;
        this.components = tags;
        this.id = id;
    }
}
