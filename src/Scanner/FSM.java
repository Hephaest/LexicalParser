/**
 * This class is used to achieve the FMS.
 * This class will checking following symbols:
 * <UL>
 * <LI/> comment symbols
 * <LI/> single or double quote symbols
 * <LI/> bracket symbols
 * <LI/> keywords or identifiers
 * <LI/> unsigned digital symbol
 * <LI/> other symbols from symbol table
 * </UL>
 * While checking, this class will do some basic syntax checking.
 * During checking procedure, if the FMS rejects some input character then it will report errors and terminate parsing.
 * @author Miao Cai
 * @since 15/12/2019 9:32 PM
 */
package Scanner;

import Utils.*;

import javax.swing.*;
import java.io.UnsupportedEncodingException;

public class FSM {
    // Variables declaration
    private final int REPORT_ERROR = -1;
    private CommentTracker ct;
    private KeyWordChecker kc;
    private SymbolTracker st;
    private StringTracker strTracker;
    private Translator translator;
    private int row;
    private JTextArea tArea;
    private StringBuilder builder;

    /**
     * This constructor will create a new object of the FSM.
     * @param ct The object of CommentTracker class.
     * @param st The object of SymbolTracker class.
     * @param strTracker The object of StringTracker class.
     * @param tArea The instance of JTextArea class.
     */
    protected FSM(CommentTracker ct, SymbolTracker st, StringTracker strTracker, JTextArea tArea) {
        this.ct = ct;
        this.kc = new KeyWordChecker();
        this.st = st;
        this.strTracker = strTracker;
        this.tArea = tArea;
        builder = new StringBuilder();
        translator = new Translator();
    }

    /*
     * This method is used to activate the start state of FMS.
     */
    protected int changeState(String line, int col, int row, StringBuilder sb) {
        this.row = row;
        col = isComment(line, col, sb);
        return col;
    }

    /**
     * This method is used to record the number of open or close comment symbol and then change to another state.
     * @param line The content of the file.
     * @param col The current column.
     * @param sb The object of StringBuilder class.
     * @return The next state of FMS.
     */
    private int isComment(String line, int col, StringBuilder sb) {

        char c = line.charAt(col);
        // Look ahead one step.
        int lookForward = col + 1;
        boolean skip = false;

        // If the string is "*/".
        if (c == '*' && lookForward < line.length() && line.charAt(lookForward) == '/') {
            skip = true;
            ct.setCommentState(-1);
            if (ct.getCommentState() == -1) ct.updateUnclosedPosition(row, col);
        } else if (c == '/' && lookForward < line.length() && (line.charAt(lookForward) == '*')) {
            // The current string is "/*".
            skip = true;
            ct.setCommentState(1);
            if (ct.getCommentState() == 1) ct.updateUnclosedPosition(row, col);
        }
        // Skip the col we have checked.
        col = (skip)? lookForward : col;
        if (ct.getCommentState() == 0) {
            // If the string is "//", just ignore rest of the line.
            if (c == '/' && line.charAt(lookForward) == '/') return line.length() - 1;
            // Go to the next state of FMS.
            return isString(c, line, col, sb);
        } else  {
            /* Complex situation, only return the column of the last index of the target symbol. */
            int finalPos = 0;
            int endPos = line.indexOf("*/", col) + 1;
            int startPos = line.indexOf("/**", col) + 2;
            if (startPos != 1) {
                finalPos = Math.max(startPos, endPos);
                if(startPos > endPos) ct.setCommentState(1);
                return finalPos;
            } else if ((startPos = line.indexOf("/*", col) + 1) != 0){
                if(startPos > endPos) ct.setCommentState(1);
                finalPos = Math.max(startPos, endPos);
                return finalPos;
            } else if (endPos != 0){
                ct.setCommentState(-1);
                return endPos;
            } else {
                // Finish, go to next line.
                return line.length() - 1;
            }


        }
    }

    /**
     * This method is used to calculate the number of left or right brackets.
     * @param c The current character.
     * @param line The content of the file.
     * @param col The current column.
     * @return The next state of FMS.
     */
    private int isBracket(char c, String line, int col){
        switch (c){
            case '(':
                translator.addToken("left_parenthesis".toUpperCase() + "_TOKEN", "(", tArea);
                st.setParState(1);
                if (st.getParState() == 1) st.updateUnclosedParPosition(row, col);
                return col;
            case ')':
                translator.addToken("right_parenthesis".toUpperCase() + "_TOKEN", ")", tArea);
                st.setParState(-1);
                if (st.getParState() == -1) st.updateUnclosedParPosition(row, col);
                return col;
            case '[':
                translator.addToken("left_bracket".toUpperCase() + "_TOKEN", "[", tArea);
                st.setBracketState(1);
                if (st.getBracketState() == 1) st.updateUnclosedBracketPosition(row, col);
                return col;
            case ']':
                translator.addToken("right_bracket".toUpperCase() + "_TOKEN", "]", tArea);
                st.setBracketState(-1);
                if (st.getBracketState() == -1) st.updateUnclosedBracketPosition(row, col);
                return col;
            case '{':
                translator.addToken("left_curly_bracket".toUpperCase() + "_TOKEN", "{", tArea);
                st.setCurlyBracketState(1);
                if (st.getCurlyBracketState() == 1) st.updateUnclosedCurlyBracketPosition(row, col);
                return col;
            case '}':
                translator.addToken("right_curly_bracket".toUpperCase() + "_TOKEN", "}", tArea);
                st.setCurlyBracketState(-1);
                if (st.getCurlyBracketState() == -1) st.updateUnclosedCurlyBracketPosition(row, col);
                return col;
            default:
                // Go to the next state of FMS.
                return isRecognizedSymbol(c, line, col);
        }

    }

    /**
     * This method is used to check whether the current symbol is legal or not from the symbol table.
     * @param c The current character.
     * @param line The content of the file.
     * @param col The current column.
     * @return Go back to the recognizer.
     */
    private int isRecognizedSymbol(char c, String line, int col){
        if (c == ' ') return col;
        String label = st.getAttributeLabel(c);
        String lookAhead = "";
        builder.setLength(0);
        // Find the next character.
        if (col + 1 < line.length()) lookAhead = Character.toString(line.charAt(col + 1));
        if(label.isEmpty()) {
            ErrorReport.unexpectedIdentifierError(row, col, tArea);
            return REPORT_ERROR;
        } else {
            // Make these two character as a string.
            String newLabel = st.getAttributeLabel(c + lookAhead);
            if(newLabel.isEmpty()) {
                // Only one symbol.
                translator.addToken(label.toUpperCase() + "_TOKEN", Character.toString(c), tArea);
                return col;
            } else {
                // Two character combined together as one symbol.
                translator.addToken(newLabel.toUpperCase() + "_TOKEN", c + lookAhead, tArea);
                return ++col;
            }
        }
    }

    /**
     * This method is used to check whether the current symbol is unsigned number or not.
     * @param c The current character.
     * @param line The content of the file.
     * @param col The current column.
     * @param sb The object of StringBuilder class.
     * @return The next state of FMS.
     */
    private int isUnsignedNumber(char c, String line, int col, StringBuilder sb) {
        int lookForward = col + 1;
        boolean hasDot = false;

        if(c == '0' && lookForward < line.length() && line.charAt(lookForward) == 'x') {
            /* Check whether is hexadecimal number. */
            sb.append(c).append('x');
            col = col + 2;
            int count = 0;
            while (col < line.length() && isHexLetter(c = line.charAt(col))) {
                sb.append(c);
                count++;
                col ++;
            }
            if(count == 0) {
                // Wrong value.
                ErrorReport.illegalHexSizeError(row, col, tArea);
                return REPORT_ERROR;
            } else if (count < 9) {
                translator.addToken("NUMERIC_VALUE_TOKEN", sb.toString(), tArea);
            } else {
                // The length of value exceeds the maximum value.
                ErrorReport.illegalIntSizeError(row, col, tArea);
                return REPORT_ERROR;
            }
        } else if (c == '0' && lookForward < line.length() && line.charAt(lookForward) == 'b') {
            /* Check whether is binary number. */
            sb.append(c).append('b');
            col = col + 2;
            int count = 0;
            while (col < line.length() && isBinary(c = line.charAt(col))) {
                sb.append(c);
                count++;
                col ++;
            }
            if(count == 0) {
                // Wrong value.
                ErrorReport.illegalBinarySizeError(row, col, tArea);
                return REPORT_ERROR;
            } else if (count < 33) {
                translator.addToken("NUMERIC_VALUE_TOKEN", sb.toString(), tArea);
            } else {
                // The length of value exceeds the maximum value.
                ErrorReport.illegalIntSizeError(row, col, tArea);
                return REPORT_ERROR;
            }
        }
        // Clear the StringBuilder.
        sb.setLength(0);
        if(Character.isDigit(c) || (c == '-' && Character.isDigit(line.charAt(lookForward)))){
            sb.append(c);
            col ++;
            /* Maybe float or double. Do not distinguish both of them. */
            while (col <line.length() && (Character.isDigit(c = line.charAt(col)) || (!hasDot && c == '.'))){
                if (c == '.') hasDot = true;
                sb.append(c);
                col ++;
            }
            translator.addToken("NUMERIC_VALUE_TOKEN", sb.toString(), tArea);
            if (col == line.length()) return col;
        }
        // Go to the next state of FMS.
        return isBracket(c, line, col);
    }

    /**
     * This method is used to check whether the current symbol is keyword or identifier.
     * @param c The current character.
     * @param line The content of the file.
     * @param col The current column.
     * @param sb The object of StringBuilder class.
     * @return The next state of FMS.
     */
    private int isKeywordOrIdentifier(char c, String line, int col, StringBuilder sb){
        /* Java allows the identifier with prefix of "_" or "$" */
        if(isLetter(c) || c == '_' || c == '$'){
            sb.append(c);
            col ++;

            while (col <line.length() && (c = line.charAt(col)) != ' '){
                if(st.isSpecialSymbol(c)) {
                    col--;
                    break;
                }
                sb.append(c);
                col ++;
            }
            String word = sb.toString();
            if(isPreservedWord(word)) translator.addToken(word.toUpperCase() + "_TOKEN", word, tArea);
            else if (!isLargerThan32Byte(word, col)) {
                translator.addToken("IDENTIFIER_TOKEN", word, tArea);
            } else {
                ErrorReport.illegalDefinedSizeError(row, col, tArea);
                return REPORT_ERROR;
            }
            return col;
        }
        return isUnsignedNumber(c, line, col, sb);
    }

    /**
     * This method is used to check whether the current symbol is start of string or character.
     * @param c The current character.
     * @param line The content of the file.
     * @param col The current column.
     * @param sb The object of StringBuilder class.
     * @return The next state of FMS.
     */
    private int isString(char c, String line, int col, StringBuilder sb){
        int lookForwardOneStep = col + 1;
        int lookForwardTwoSteps = col + 2;
        if(c == '\"') {
            /* This line maybe contains a string. Mark it. */
            if (strTracker.hasRedundantQuote()) {
                strTracker.addStringToToken(translator, tArea);
                strTracker.setStrState();
            } else {
                strTracker.clearBuilder();
                strTracker.setStrState();
                strTracker.updateUnclosedPosition(row, col);
            }
        } else if (c == '\'' && lookForwardOneStep < line.length() && line.charAt(lookForwardOneStep) == '\\'){
            /* This is maybe a escape character. */
            char lookFwdChar = line.charAt(lookForwardTwoSteps);
            char lookFwdNextChar = line.charAt(col + 3);
            char[] list = {'\"','\'','\\','r','n','f','t','b'};
            for (char item : list) {
                if (item == lookFwdChar && lookFwdNextChar == '\'') {
                    translator.addToken("CHAR_TOKEN", "\\" + item, tArea);
                    return col + 3;
                }
            }

            if (lookFwdChar == 'u') col = col + 3; // Need to look ahead 3 steps.
            else col = col + 2;

            builder.setLength(0);
            while (col < line.length() && (c = line.charAt(col)) != '\''){
                if (!Character.isDigit(c)) {
                    /* This is definitely not a character. */
                    ErrorReport.unclosedCharError(row, col, tArea);
                    builder.setLength(0);
                    return REPORT_ERROR;
                }
                builder.append(c);
                col++;
            }

            /* Check out whether the current character is the start of Hexadecimal or octal character. */
            if (Integer.valueOf(builder.toString(),8) >= Integer.valueOf("000",8) &&
                    Integer.valueOf(builder.toString(),8) <= Integer.valueOf("377",8)){
                translator.addToken("CHAR_TOKEN", "\\u" + builder.toString(), tArea);
                return col;
            } else if (lookFwdChar == 'u' && Integer.valueOf(builder.toString(),16) >= Integer.valueOf("0000",16)
                    && Integer.valueOf(builder.toString(),16) <= Integer.valueOf("FFFF",16)){
                translator.addToken("CHAR_TOKEN", "\\" + builder.toString(), tArea);
                return col;
            } else {
                ErrorReport.unclosedCharError(row, col, tArea);
                return REPORT_ERROR;
            }

        } else if (c == '\'' && lookForwardTwoSteps < line.length() && line.charAt(lookForwardTwoSteps) == '\'') {
            /* This is definitely a character. */
            translator.addToken("CHAR_TOKEN", Character.toString(line.charAt(lookForwardOneStep)), tArea);
            return col + 2;
        } else if (c == '\'') {
            /* This is definitely character syntax error. */
            ErrorReport.unclosedCharError(row, col, tArea);
            return REPORT_ERROR;
        } else if (strTracker.hasRedundantQuote()) {
            /* Must belong to a string. */
            strTracker.appendChar(c);
            return col;
        }
        // Go to the next state of FMS.
        return isKeywordOrIdentifier(c, line, col, sb);
    }

    /**
     * This method is used to check whether the word belongs to the keyword table.
     * @param word The prediction word.
     * @return A boolean result.
     */
    private boolean isPreservedWord(String word) {
        return kc.getKeyWordArray().toString().contains(word);
    }

    /**
     * This method is used to check whether the character is letter or not.
     * @param ch The current character.
     * @return A boolean result.
     */
    private boolean isLetter(char ch){
        return ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z');
    }

    /**
     * This method is used to check whether the character is Hexadecimal letter or not.
     * @param ch The current character.
     * @return A boolean result.
     */
    private boolean isHexLetter(char ch){
        return ('0' <= ch && ch <= '9') || ('a' <= ch && ch <= 'f') || ('A' <= ch && ch <= 'F');
    }

    /**
     * This method is used to check whether the character is binary number or not.
     * @param ch The current character.
     * @return A boolean result.
     */
    private boolean isBinary(char ch) {
        return ch == '1' || ch == '0';
    }
    /**
     * This method is used to check whether the length of user defined identifier's name exceeds the 32 Bytes.
     * @param identifier The name of user defined identifier.
     * @param col The current column.
     * @return A boolean result.
     */
    private boolean isLargerThan32Byte(String identifier, int col){
        try {
            if (identifier.getBytes("utf-8").length > 32) return true;
        } catch (UnsupportedEncodingException e) {
            ErrorReport.unsupportedEncodingError(row, col, tArea);
            return true;
        }
        return false;
    }
}
