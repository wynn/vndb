package CosineSimilarity.Maps;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Aria on 2016/11/30.
 */
public class TraitsMap extends CosSimMap {
    private static TraitsMap instance;

    private TraitsMap() {
        this.filename = "traits.json";
    }

    public static TraitsMap getInstance() {
        if(instance == null) {
            instance = new TraitsMap();
        }
        return instance;
    }

    @Override
    protected void addToMap(JSONObject currentLoadedObject, HashMap<Integer, Integer> map) {
        int traitID = (int)currentLoadedObject.get("id");
        int numChars = (int)currentLoadedObject.get("chars");
        if(numChars == 0) return;
        map.put(traitID, numChars);
    }
}
