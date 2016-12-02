package CosineSimilarity;

import CosineSimilarity.Maps.TagMap;
import CosineSimilarity.Maps.TraitsMap;
import CosineSimilarity.Vectors.VNCharacter;
import CosineSimilarity.Vectors.VNVector;
import CosineSimilarity.Vectors.VisualNovel;
import Utility.UtilityMethods;
import VNDB.VNObjects;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Aria on 2016/11/30.
 */
public class Calculations {

    //Arguments for nearest neighbor
    public final static int IGNORE_ENGLISH_VNs = 1;
    private final static int NUM_DEFAULT_RESULTS = 10;

    private double cachedDenominator = -1; //should save a couple hundred thousand operations
    //remember to set it to -1 after each time CS is calculated with a new item as the first argument

    private void resetCachedDenominator() {
        cachedDenominator = -1;
    }

    /**
     * Calculates the cosine similarity of two vectors based on their tags.
     * Any calls to this method should call resetCachedDenominator()
     * if the value of vector1 differs from the value of vector1 the last time this method was called
     *
     * @param   vector1 1st vector being compared
     * @param   vector2 2nd vector being compared
     * @param   sampleSize Number of entries in the database
     * @return  the cosine similarity of vector1 and vector2
     */
    private double calculateCosineSimilarity(VNVector vector1, VNVector vector2, int sampleSize) {
        double cosineSimilarity;
        double numerator = 0;
        double denominator1 = 0;
        double denominator2 = 0;

        HashMap<Integer, Integer> selectedMap;

        if(!vector1.getClass().equals(vector2.getClass())) {
            System.out.println("Attempting to compare two different types of vectors!");
            throw new IllegalArgumentException();
        }

        if(vector1 instanceof VisualNovel) {
            selectedMap = TagMap.getInstance().getMap();
        }
        else if(vector1 instanceof VNCharacter) {
            selectedMap = TraitsMap.getInstance().getMap();
        }
        else {
            System.out.println("Unknown vector type!");
            throw new IllegalArgumentException();
        }

        //idf = log(num of vns with tag/total number of VNs)
        //tfidf = 1 (since each tag will appear only once in every VN anyway)  * idf

        //We need to check whether the tagMap contains the tag, since the tag might have been removed
        //or we're using an older version of the database
        //Checking if a tag has 0 VNs associated with it also has been added
        if(cachedDenominator == -1) {
            for(int x: vector1.getComponents()) {
                if(selectedMap.get(x) == null) continue;
                denominator1 += Math.pow(Math.log10(sampleSize/selectedMap.get(x)), 2);
            }
            cachedDenominator = denominator1;
        } else {
            denominator1 = cachedDenominator;
        }

        for (int x: vector1.getComponents()) {
            if(selectedMap.get(x) == null) continue;
            if(vector2.getComponents().contains(x)) {
                numerator += Math.log10(sampleSize / selectedMap.get(x)) * Math.log10(sampleSize / selectedMap.get(x));
            }
        }
        for(int x: vector2.getComponents()) {
            if(selectedMap.get(x) == null) continue;
            denominator2 += Math.pow(Math.log10(sampleSize/selectedMap.get(x)), 2);
        }

        //one VN has no or only invalid tags
        if(denominator1 == 0 || denominator2 == 0) return 0;

        cosineSimilarity = numerator / (Math.sqrt(denominator1) * Math.sqrt(denominator2));
        return cosineSimilarity;
    }

    //Simply calculates the similarity of two VN characters
    public double calculateCosineSimilarity(VNCharacter vnCharacter1, VNCharacter vnCharacter2, String storageFolderName) {
        resetCachedDenominator();
        File folder = new File(storageFolderName);
        File[] files = folder.listFiles();
        int sampleSize = 45000; //45000's about the number of characters I originally cached
        if(files != null) {
            sampleSize = files.length;
        }
        return calculateCosineSimilarity(vnCharacter1, vnCharacter2, sampleSize);
    }

    //Simply calculates the similarity of two visual novels
    public double calculateCosineSimilarity(VisualNovel vn1, VisualNovel vn2, String storageFolderName) {
        resetCachedDenominator();
        File folder = new File(storageFolderName);
        File[] files = folder.listFiles();
        int sampleSize = 18000; //18000's about the number of VNs I originally cached
        if(files != null) {
            sampleSize = files.length;
        }
        return calculateCosineSimilarity(vn1, vn2, sampleSize);
    }

    public void calculateMostSimilarVNs(int numToFind, VisualNovel visualNovel, String storageFolderName, int... args) {
        resetCachedDenominator();
        boolean ignoreEVNs = false;
        for(int arg: args) {
            if(arg == IGNORE_ENGLISH_VNs) {
                ignoreEVNs = true;
            }
        }
        String vnData;
        ArrayList<NameSimilarityPair> nameSimilarityPairs = new ArrayList<>();

        String vnID = visualNovel.getID();
        File folder = new File(storageFolderName);
        File[] files = folder.listFiles();

        if(files != null) {
            int sampleSize = files.length;
            for (File file : files) {
                if (file.isFile()) {
                    //checking the id adds a small amount to the runtime
                    //if I added id to namesimilaritypair I could check at the end
                    //I could also just skip the first element since it'd be the VN itself
                    String id = file.getName().replace(".txt", "");
                    if(id.equals(vnID)) continue;

                    try {
                        vnData = UtilityMethods.loadDataFile(file);
                        JSONObject jsonObject = new JSONObject(vnData);
                        JSONArray o = (JSONArray) jsonObject.get("items");
                        JSONObject vnTraitsMap = (JSONObject) o.get(0);

                        if(ignoreEVNs) {
                            JSONArray origLanguages = (JSONArray) vnTraitsMap.get("orig_lang");
                            boolean flag = false;
                            for (int x = 0; x < origLanguages.length(); x++) {
                                if (origLanguages.getString(x).equals("en")) {
                                    flag = true;
                                    break;
                                }
                            }
                            if (flag) continue;
                        }

                        VisualNovel visualNovel1 = VNObjects.getVisualNovelFromString(vnData, id);
                        double cosineSimilarity = calculateCosineSimilarity(visualNovel, visualNovel1, sampleSize);
                        nameSimilarityPairs.add(new NameSimilarityPair(visualNovel1.toString(), cosineSimilarity));
                    } catch (JSONException | NullPointerException e) {
                        //e.printStackTrace();
                    }
                }
            }
        }
        else {
            System.out.println("Error occurred when trying to browse directory, exiting.");
            return;
        }

        Collections.sort(nameSimilarityPairs);

        System.out.println("Searched : " + nameSimilarityPairs.size() + " visual novels.");
        System.out.println("Most similar visual novels to: " + visualNovel.toString());
        for(int i = 0; i < (nameSimilarityPairs.size() < numToFind ? nameSimilarityPairs.size(): numToFind); i++) {
            System.out.println(nameSimilarityPairs.get(i).name + ": " + nameSimilarityPairs.get(i).similarityValue);
        }
    }

    public void calculateMostSimilarVNs(VisualNovel visualNovel, String storageFolderName, int... args) {
        calculateMostSimilarVNs(NUM_DEFAULT_RESULTS, visualNovel, storageFolderName, args);
    }

    public void calculateMostSimilarCharacters(int numToFind, VNCharacter vnCharacter, String storageFolderName) {
        resetCachedDenominator();
        String charData;
        ArrayList<NameSimilarityPair> nameSimilarityPairs = new ArrayList<>();

        String charID = vnCharacter.getID();
        File folder = new File(storageFolderName);
        File[] files = folder.listFiles();

        if(files != null) {
            int sampleSize = files.length;
            for (File file : files) {
                if (file.isFile()) {
                    String id = file.getName().replace(".txt", "");;
                    if(id.equals(charID)) continue;

                    try {
                        charData = UtilityMethods.loadDataFile(file);
                        VNCharacter vnCharacter1 = VNObjects.getVNCharacterFromString(charData, id);
                        double cosineSimilarity = calculateCosineSimilarity(vnCharacter, vnCharacter1, sampleSize);
                        nameSimilarityPairs.add(new NameSimilarityPair(vnCharacter1.toString(), cosineSimilarity));
                    } catch (JSONException | NullPointerException e) {
                        //e.printStackTrace();
                    }
                }
            }
        }
        else {
            System.out.println("Error occurred when trying to browse directory, exiting.");
            return;
        }

        Collections.sort(nameSimilarityPairs);

        System.out.println("Searched : " + nameSimilarityPairs.size() + " characters.");
        System.out.println("Most similar characters to: " + vnCharacter.toString());
        for(int i = 0; i < (nameSimilarityPairs.size() < numToFind ? nameSimilarityPairs.size(): numToFind); i++) {
            System.out.println(nameSimilarityPairs.get(i).name + ": " + nameSimilarityPairs.get(i).similarityValue);
        }
    }

    public void calculateMostSimilarCharacters(VNCharacter vnCharacter, String storageFolderName) {
        calculateMostSimilarCharacters(NUM_DEFAULT_RESULTS, vnCharacter, storageFolderName);
    }
}
