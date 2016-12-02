package CosineSimilarity;

/**
 * Created by Aria on 1/1/2016.
 */
public class NameSimilarityPair implements Comparable<NameSimilarityPair> {

    public String name;
    public Double similarityValue;

    public NameSimilarityPair(String name, double similarityValue) {
        this.name = name;
        this.similarityValue = similarityValue;
    }

    @Override
    public int compareTo(NameSimilarityPair o) {
        return this.similarityValue.compareTo(o.similarityValue) * -1;
    }
}
