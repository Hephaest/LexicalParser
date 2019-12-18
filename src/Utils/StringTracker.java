/**
 * This class is used to track string symbols.
 * @author Miao Cai
 * @since 15/12/2019 9:32 PM
 */
package Utils;

import Scanner.Translator;

import javax.swing.*;

public class StringTracker {
    // Variables declaration
    private boolean hasNotYetFinishQuote;
    private int unclosedStrRow;
    private int unclosedStrCol;
    private StringBuilder sb;

    /* Initialization. */
    public StringTracker(boolean status){
        this.hasNotYetFinishQuote = status;
        sb = new StringBuilder();
    }

    /**
     * This method is used to update the tracking position.
     * @param row The new row.
     * @param col the new column.
     */
    public void updateUnclosedPosition(int row, int col){
        unclosedStrRow = row;
        unclosedStrCol = col;
    }

    /**
     * This method is used to get the row of the tracking position.
     * @return The row of the tracking position.
     */
    public int getUnclosedRowPos(){
        return unclosedStrRow;
    }

    /**
     * This method is used to get the column of the tracking position.
     * @return The column of the tracking position.
     */
    public int getUnclosedColPos(){
        return unclosedStrCol;
    }

    /**
     * This method is used to set the status of the tracking.
     * This like a switch.
     */
    public void setStrState(){
        hasNotYetFinishQuote = !hasNotYetFinishQuote;
    }

    /**
     * This method is used to collect the character and combine it with the existing string.
     * @param c the current character.
     */
    public void appendChar(char c){
        sb.append(c);
    }

    /**
     * This method is used to clean the StringBuilder instance.
     */
    public void clearBuilder(){
        sb.setLength(0);
    }

    /**
     * This method is used to add a string into a token.
     * @param translator The object of Translator class.
     * @param tArea The object of JTextArea.
     */
    public void addStringToToken(Translator translator, JTextArea tArea){
        translator.addToken("STRING_TOKEN", sb.toString(), tArea);
    }

    /**
     * This method is used to get the status of the tracking.
     * @return The status of the tracking.
     */
    public boolean hasRedundantQuote(){
       return hasNotYetFinishQuote ;
    }
}
