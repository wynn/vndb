package CosineSimilarity.Maps;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Aria on 2016/11/30.
 */
public class TagMap extends CosSimMap {
    private static TagMap instance;

    private TagMap() {
        this.filename = "tags.json";
    }

    public static TagMap getInstance() {
        if(instance == null) {
            instance = new TagMap();
        }
        return instance;
    }

    @Override
    protected void addToMap(JSONObject currentLoadedObject, HashMap<Integer, Integer> map) {
        int tagID = (int)currentLoadedObject.get("id");
        int numVNs = (int)currentLoadedObject.get("vns");
        if(numVNs == 0) return;
        map.put(tagID, numVNs);
    }
}
