package VNDB;

import CosineSimilarity.Vectors.VNCharacter;
import CosineSimilarity.Vectors.VisualNovel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.HashSet;

import static VNDB.VNDBConstants.*;
import Utility.UtilityMethods;

/**
 * Created by Aria on 2016/12/01.
 * Generates objects with the base type of VNVector, VNCharacter and VisualNovel
 */
public class VNObjects {

    VNDBSession vndbSession;

    public VNObjects(VNDBSession session) {
        vndbSession = session;
    }

    /**
     * First, the VN is loaded from cache if possible
     * if refreshCache is true or the character is not cached then the VN's data is obtained from the vndb servers
     *
     * @param   id id of the visual novel on vndb
     * @param   refreshCache if true, will download new data from vndb for that VN
     */
    public VisualNovel getVisualNovel(String id, boolean refreshCache) throws VNDBThrottleException {
        VisualNovel result;
        if(!refreshCache && (result = getVisualNovelFromFile(id)) != null) {
            return result;
        }
        return getVisualNovelFromServer(id);
    }

    /**
     * First, the VN character loaded from cache if possible
     * if refreshCache is true or the character is not cached then the character's data is obtained from the vndb servers
     *
     * @param   id id of the character on vndb
     * @param   refreshCache if true, will download new data from vndb for that character
     */
    public VNCharacter getVNCharacter(String id, boolean refreshCache) throws VNDBThrottleException {
        VNCharacter result;
        if(!refreshCache && (result = getVNCharacterFromFile(id)) != null) {
            return result;
        }
        return getVNCharacterFromServer(id);
    }

    public VNCharacter getVNCharacterFromServer(String id) throws VNDBThrottleException {
        File file = new File(CHAR_STORAGE_FOLDER_NAME + "/" + id + ".txt");
        String response = vndbSession.sendMessage("get character basic,traits (id = " + id + ")").replace("results", "");
        if(response.startsWith(ERROR_RESPONSE) || response.length() == 0) {
            System.out.println("vndb's API returned an error!");
            throw new VNDBThrottleException();
        }
        try {
            if(!new File(CHAR_STORAGE_FOLDER_NAME).exists()) {
                if(!new File(CHAR_STORAGE_FOLDER_NAME).mkdir()) {
                    System.out.println("Failed to create directory " + CHAR_STORAGE_FOLDER_NAME);
                }
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.write(response);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            return getVNCharacterFromString(response, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Loads and returns a VNCharacter from saved data in the default storage directory, CHAR_STORAGE_FOLDER_NAME
     *
     * @param   id id of the character on vndb
     */
    public static VNCharacter getVNCharacterFromFile(String id) {
        File file = new File(CHAR_STORAGE_FOLDER_NAME + "/" + id + ".txt");
        if (file.exists()) {
            System.out.println("Using cached data for: " + id);
            String data = UtilityMethods.loadDataFile(file);
            if(data != null) {
                return getVNCharacterFromString(data, id);
            }
        }
        return null;
    }

    public static VNCharacter getVNCharacterFromString(String characterData, String id) {
        JSONObject characterResponse = new JSONObject(characterData);
        //Items = a length 1 array of the character's traits
        JSONArray items = (JSONArray) characterResponse.get("items");
        //array[0] = a map of the character's traits
        JSONObject charTraitsMap = (JSONObject) items.get(0);
        String charName = charTraitsMap.getString("name");
        JSONArray charTraits = (JSONArray) charTraitsMap.get("traits");

        HashSet<Integer> charTraitsSet = new HashSet<>();
        for(int i = 0; i < charTraits.length(); i++) {
            JSONArray currTag = charTraits.getJSONArray(i);
            charTraitsSet.add((Integer)currTag.get(TAG_ID));
        }

        return new VNCharacter(charName, charTraitsSet, id);
    }

    public VisualNovel getVisualNovelFromServer(String id) throws VNDBThrottleException {
        File file = new File(VN_STORAGE_FOLDER_NAME + "/" + id + ".txt");
        String response = vndbSession.sendMessage("get vn basic,tags (id = " + id + ")").replace("results", "");
        if(response.startsWith(ERROR_RESPONSE) || response.length() == 0) {
            System.out.println("vndb's API returned an error!");
            throw new VNDBThrottleException();
        }
        try {
            if(!new File(VN_STORAGE_FOLDER_NAME).exists()) {
                if(!new File(VN_STORAGE_FOLDER_NAME).mkdir()) {
                    System.out.println("Failed to create directory " + VN_STORAGE_FOLDER_NAME);
                }
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
            writer.write(response);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            return getVisualNovelFromString(response, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Loads and returns a VisualNovel from saved data in the default storage directory, VN_STORAGE_FOLDER_NAME
     *
     * @param   id id of the VN on vndb
     */
    public static VisualNovel getVisualNovelFromFile(String id) {
        File file = new File(VN_STORAGE_FOLDER_NAME + "/" + id + ".txt");
        if (file.exists()) {
            System.out.println("Using cached data for: " + id);
            String data = UtilityMethods.loadDataFile(file);
            if(data != null) {
                return getVisualNovelFromString(data, id);
            }
        }
        return null;
    }

    public static VisualNovel getVisualNovelFromString(String vnData, String id) {
        JSONObject visualNovelResponse = new JSONObject(vnData);
        //Items = a length 1 array of the VN's traits
        JSONArray items = (JSONArray) visualNovelResponse.get("items");
        //array[0] = a map of the VN's traits
        JSONObject vnTraitsMap = (JSONObject) items.get(0);
        String title = vnTraitsMap.getString("title");
        JSONArray tags = (JSONArray) vnTraitsMap.get("tags");

        HashSet<Integer> vnTags = new HashSet<>();
        for(int i = 0; i < tags.length(); i++) {
            JSONArray currTag = tags.getJSONArray(i);
            vnTags.add((Integer)currTag.get(TAG_ID));
        }
        return new VisualNovel(title, vnTags, id);
    }
}
