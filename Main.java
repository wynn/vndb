import CosineSimilarity.Calculations;
import CosineSimilarity.Vectors.VNCharacter;
import CosineSimilarity.Vectors.VisualNovel;
import VNDB.VNDBConstants;
import VNDB.VNDBSession;
import VNDB.VNDBThrottleException;
import VNDB.VNObjects;

import java.io.IOException;

/**
 * Created by Aria on 2016/12/01.
 * Example usage of project
 */
public class Main {

    public static void main(String... args) throws IOException, InterruptedException, VNDBThrottleException {
        VNDBSession v = new VNDBSession();

        if (v.login()) {
            VNObjects vnObjects = new VNObjects(v);
            VisualNovel vn1, vn2;
            VNCharacter eustia;

            try {
                vn1 = vnObjects.getVisualNovel("5834", false); //Irotoridori no Sekai
                vn2 = vnObjects.getVisualNovel("10028", false); //Irotoridori no Hikari
                eustia = vnObjects.getVNCharacter("514", false); //Eustia Astraea from Aiyoku no Eustia
            } catch (VNDBThrottleException e) {
                System.out.println("The server has throttled us.");
                return;
            }

            Calculations c = new Calculations();
            long start = System.currentTimeMillis();

            //add arguments to take like ignore EVNs, ignore VNs that have an English translation
            //currently 1 argument has been added
            //1: Skip English original language VNs because most of the time they're low quality
            //Note that will result in slightly incorrect values for similarity since the tag count might include EVNs
            c.calculateMostSimilarVNs(vn1, VNDBConstants.VN_STORAGE_FOLDER_NAME, Calculations.IGNORE_ENGLISH_VNs);
            System.out.println("Time elapsed: " + (System.currentTimeMillis() - start) + "ms");
            System.out.println();
            start = System.currentTimeMillis();

            c.calculateMostSimilarCharacters(eustia, VNDBConstants.CHAR_STORAGE_FOLDER_NAME);
            System.out.println("Time elapsed: " + (System.currentTimeMillis() - start) + "ms");
            System.out.println();
            start = System.currentTimeMillis();

            //As expected, since the two Irotoridori games are from the same series, they will have a high similarity
            double cosineSimilarity;
            cosineSimilarity = c.calculateCosineSimilarity(vn1, vn2, VNDBConstants.VN_STORAGE_FOLDER_NAME);
            System.out.println(vn1.toString() + " vs " + vn2.toString() + ": " + cosineSimilarity);
            System.out.println("Time elapsed: " + (System.currentTimeMillis() - start) + "ms");
        }
    }
}
