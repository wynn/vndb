package VNDB;

/**
 * Created by Aria on 2016/12/01.
 */
public class VNDBConstants {
    public final static byte END_OF_TRANSMISSION_CHAR = 4;
    public final static String VN_STORAGE_FOLDER_NAME = "Data", CHAR_STORAGE_FOLDER_NAME = "Chars";

    //Responses
    public final static String ERROR_RESPONSE = "error", RESULTS_RESPONSE = "results";

    //Array indices for the each tag associated with a given VN
    public final static int TAG_ID = 0, TAG_WEIGHT = 1, TAG_SPOILER = 2;
}
