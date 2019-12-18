/**
 * This class is used to track bracket symbols.
 * @author Miao Cai
 * @since 15/12/2019 9:32 PM
 */
package Utils;

import javax.json.*;
import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SymbolTracker {

    // Variables declaration
    private final int MAX_INTEGER = 2147483647;

    private JsonArray symbolArray;
    private JTextArea tArea;
    private int redundantParRow;
    private int redundantParCol;
    private int redundantBracketRow;
    private int redundantBracketCol;
    private int redundantCurlyBracketRow;
    private int redundantCurlyBracketCol;
    private int hasNotYetFinishPar;
    private int hasNotYetFinishBracket;
    private int hasNotYetFinishCurlyBracket;

    /* Initialization. */
    public SymbolTracker (int status, JTextArea tArea){
        this.tArea = tArea;
        this.hasNotYetFinishPar = status;
        this.hasNotYetFinishBracket = status;
        this.hasNotYetFinishCurlyBracket = status;

        InputStream fis = SymbolTracker.class.getResourceAsStream("/Database/symbolTable.json");
        JsonReader reader = Json.createReader(fis);
        symbolArray = reader.readArray();
    }

    /**
     * This method is used to update the tracking position of the parenthesis.
     * @param row The new row.
     * @param col the new column.
     */
    public void updateUnclosedParPosition(int row, int col){
        redundantParRow = row;
        redundantParCol = col;
    }

    /**
     * This method is used to update the tracking position of the bracket.
     * @param row The new row.
     * @param col the new column.
     */
    public void updateUnclosedBracketPosition(int row, int col){
        redundantBracketRow = row;
        redundantBracketCol = col;
    }

    /**
     * This method is used to update the tracking position of the curly bracket.
     * @param row The new row.
     * @param col the new column.
     */
    public void updateUnclosedCurlyBracketPosition(int row, int col){
        redundantCurlyBracketRow = row;
        redundantCurlyBracketCol = col;
    }

    /**
     * This method is used to get the row of the tracking position of the parenthesis.
     * @return The row of the tracking position of the parenthesis.
     */
    public int getRedundantParRow(){
        return redundantParRow;
    }

    /**
     * This method is used to get the column of the tracking position of the parenthesis.
     * @return The column of the tracking position of the parenthesis.
     */
    public int getRedundantParCol(){
        return redundantParCol;
    }

    /**
     * This method is used to get the row of the tracking position of the bracket.
     * @return The row of the tracking position of the bracket.
     */
    public int getRedundantBracketRow(){
        return redundantBracketRow;
    }

    /**
     * This method is used to get the column of the tracking position of the bracket.
     * @return The column of the tracking position of the bracket.
     */
    public int getRedundantBracketCol(){
        return redundantBracketCol;
    }

    /**
     * This method is used to get the row of the tracking position of the curly bracket.
     * @return The row of the tracking position of the curly bracket.
     */
    public int getRedundantCurlyBracketRow(){
        return redundantCurlyBracketRow;
    }

    /**
     * This method is used to get the column of the tracking position of the curly bracket.
     * @return The column of the tracking position of the curly bracket.
     */
    public int getRedundantCurlyBracketCol(){
        return redundantCurlyBracketCol;
    }

    /**
     * This method is used to get the status of the tracking of the parenthesis.
     * @return The status of the tracking of the parenthesis.
     */
    public int getParState(){
        return hasNotYetFinishPar;
    }

    /**
     * This method is used to get the status of the tracking of the bracket.
     * @return The status of the tracking of the bracket.
     */
    public int getBracketState(){
        return hasNotYetFinishBracket;
    }

    /**
     * This method is used to get the status of the tracking of the curly bracket.
     * @return The status of the tracking of the curly bracket.
     */
    public int getCurlyBracketState(){
        return hasNotYetFinishCurlyBracket;
    }

    /**
     * This method is used to set the status of the tracking of the parenthesis.
     * @param status The new status of the parenthesis.
     */
    public void setParState(int status){
        hasNotYetFinishPar += status;
    }

    /**
     * This method is used to set the status of the tracking of the bracket.
     * @param status The new status of the bracket.
     */
    public void setBracketState(int status){
        hasNotYetFinishBracket += status;
    }

    /**
     * This method is used to set the status of the tracking of the curly bracket.
     * @param status The new status of the curly bracket.
     */
    public void setCurlyBracketState(int status){
        hasNotYetFinishCurlyBracket += status;
    }

    /**
     * This method is used to check whether there has redundant bracket or not.
     * @return A boolean result.
     */
    public boolean hasRedundantBrackets(){
        if ((hasNotYetFinishPar | hasNotYetFinishBracket | hasNotYetFinishCurlyBracket) != 0) {
            int minPosRow = MAX_INTEGER;
            int minPosCol = 1;

            if(hasNotYetFinishPar != 0) {
                minPosRow = getRedundantParRow();
                minPosCol = getRedundantParCol();
            }

            if (hasNotYetFinishBracket != 0 && minPosRow > getRedundantBracketRow()){
                minPosRow = getRedundantBracketRow();
                minPosCol = getRedundantBracketCol();
            }

            if (hasNotYetFinishCurlyBracket != 0 &&  minPosRow > getRedundantCurlyBracketRow()){
                minPosRow = getRedundantCurlyBracketRow();
                minPosCol = getRedundantCurlyBracketCol();
            }

            ErrorReport.redundantBracketError(minPosRow, minPosCol, tArea);
            return true;
        }
        return false;
    }

    /**
     * This method is used to check whether the current character belongs to the symbol table or not.
     * @param c The current character.
     * @return A boolean result.
     */
    public boolean isSpecialSymbol(char c){
        for (JsonValue jsonValue : symbolArray) {
            JsonObject object = (JsonObject) jsonValue;
            for (String key : object.keySet()) {
                if (object.get(key).toString().equals(Character.toString(c))) return true;
            }

        }
        return false;
    }

    /**
     * This method is used to get the key that belongs to the current character.
     * @param c The current character.
     * @return The name of the key.
     */
    public String getAttributeLabel (char c) {
        for (JsonValue jsonValue : symbolArray) {
            JsonObject object = (JsonObject) jsonValue;
            for (String key : object.keySet()) {
                if (object.get(key).toString().equals(Character.toString(c))) return key;
            }
        }
        return "";
    }

    /**
     * This method is used to get the key that belongs to the current string.
     * @param str The current character.
     * @return The name of the key.
     */
    public String getAttributeLabel (String str) {
        for (JsonValue jsonValue : symbolArray) {
            JsonObject object = (JsonObject) jsonValue;
            for (String key : object.keySet()) {
                if (object.get(key).toString().equals(str)) return key;
            }
        }
        return "";
    }
}
