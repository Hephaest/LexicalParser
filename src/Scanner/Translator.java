/**
 * This class is used to achieve the Recognizer.
 * This class will add the tokens into its ArrayList.
 * @author Miao Cai
 * @since 15/12/2019 9:32 PM
 */
package Scanner;

import Utils.Index;

import javax.swing.*;
import java.util.ArrayList;

public class Translator {
    // Variables declaration
    private int id;
    private ArrayList<Index> orders = new ArrayList<Index>();

    /* Initialization. */
    public Translator () {
        id = 0;
    }

    /**
     * This method is used to add a token into its ArrayList.
     * @param type The type of the token.
     * @param value The value of the token.
     * @param tArea The object of JTextArea class.
     */
    public void addToken(String type, String value, JTextArea tArea){
        /* If the current token exists, do not create a new object. */

        if (!isExist(value, tArea)) {
            Index index = new Index(type, value, ++id);
            tArea.append("< " + type + ", " + value + ", " + id + " >" + "\n");
            orders.add(index);
        }

    }

    /**
     * This method is used to check whether the current token exists or not.
     * @param value The value of the token.
     * @param tArea The object of JTextArea class.
     * @return A boolean checking result.
     */
    private boolean isExist(String value, JTextArea tArea){
        for (Index index : orders) {
            if (index.equals(value)) {
                tArea.append(index.getInfo());
                orders.add(index);
                return true;
            }
        }
        return false;
    }

}
