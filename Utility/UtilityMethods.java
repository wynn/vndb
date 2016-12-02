package Utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by Aria on 2016/12/01.
 * misc utility methods
 */
public class UtilityMethods {
    public static String loadDataFile(File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            return br.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
