/**
 * This class is used to track keywords.
 * @author Miao Cai
 * @since 15/12/2019 9:32 PM
 */
package Utils;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class KeyWordChecker {
    // Variables declaration
    private JsonArray keyWordArray;

    /* Initialization */
    public KeyWordChecker (){
        InputStream fis = KeyWordChecker.class.getResourceAsStream("/Database/keywords.json");
        JsonReader reader = Json.createReader(fis);
        keyWordArray = reader.readArray();
    }


    /**
     * This method is used to get the keyword list.
     * @return return a JsonArray as keyword list.
     */
    public JsonArray getKeyWordArray(){
        return keyWordArray;
    }
}
