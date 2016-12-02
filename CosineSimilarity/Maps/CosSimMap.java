package CosineSimilarity.Maps;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Aria on 2016/11/30.
 */
public abstract class CosSimMap {
    protected HashMap<Integer, Integer> idFrequencyMap;
    boolean hasLoadedData = false;
    String filename;

    public HashMap<Integer, Integer> getMap() {
        if(load(filename)) {
            return idFrequencyMap;
        }
        return null;
    }

    public boolean load(String filename) {
        if (hasLoadedData) return true;

        try {
            String jsonData = "";
            BufferedReader br = null;
            try {
                String line;
                br = new BufferedReader(new FileReader(filename));
                while ((line = br.readLine()) != null) {
                    jsonData += line + "\n";
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (br != null)
                        br.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            JSONArray loadedTags = new JSONArray(jsonData);

            idFrequencyMap = new HashMap<>();
            for(int i = 0; i < loadedTags.length(); i++) {
                JSONObject currentLoadedTag = loadedTags.getJSONObject(i); //a HashMap
                addToMap(currentLoadedTag, idFrequencyMap);
            }
            hasLoadedData = true;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    protected abstract void addToMap(JSONObject currentLoadedObject, HashMap<Integer, Integer> map);
}
