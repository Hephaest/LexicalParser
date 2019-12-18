/**
 * This interface is used to report errors.
 * @author Miao Cai
 * @since 15/12/2019 9:32 PM
 */
package Utils;

import javax.swing.*;

public interface ErrorReport {

    static void illegalStartError (int row, int col, JTextArea tArea){
        tArea.append("Error:(" + row + "," + col + ") java: illegal start of type\n");
    }

    static void illegalDefinedSizeError (int row, int col, JTextArea tArea){
        tArea.append("Error:(" + row + "," + col + ") java: the length of the user defined identifier is larger than 32 bytes\n");
    }

    static void unclosedComtError (int row, int col, JTextArea tArea){
        tArea.append("Error:(" + row + "," + col + ") java: unclosed comment\n");
    }

    static void parsingError (int row, int col, JTextArea tArea){
        tArea.append("Error:(" + row + "," + col + ") java: reached end of file while parsing\n");
    }

    static void unsupportedEncodingError (int row, int col, JTextArea tArea){
        tArea.append("Error:(" + row + "," + col + ") java: unsupported encoding exception\n");
    }

    static void redundantBracketError (int row, int col, JTextArea tArea){
        tArea.append("Error:(" + row + "," + col + ") java: Redundant bracket found\n");
    }

    static void unclosedCharError(int row, int col, JTextArea tArea){
        tArea.append("Error:(" + row + "," + (col + 1) + ") java: too many characters in character literal\n");
    }

    static void unclosedStrError(int row, int col, JTextArea tArea){
        tArea.append("Error:(" + row + "," + (col + 1) + ") java: unclosed string literal\n");
    }

    static void unexpectedIdentifierError(int row, int col, JTextArea tArea){
        tArea.append("Error:(" + row + "," + (col + 1) + ") java: <identifier> expected\n");
    }

    static void illegalIntSizeError(int row, int col, JTextArea tArea){
        tArea.append("Error:(" + row + "," + col + ") java: integer number too large\n");
    }

    static void illegalHexSizeError(int row, int col, JTextArea tArea){
        tArea.append("Error:(" + row + "," + col + ") java: hexadecimal numbers must contain at least one hexadecimal digit\n");
    }

    static void illegalBinarySizeError(int row, int col, JTextArea tArea) {
        tArea.append("Error:(" + row + "," + col + ") java: hexadecimal numbers must contain at least one binary digit\n");
    }

    static void ioError (JTextArea tArea){
        tArea.append("IO error! Try again later.\n");
    }
}