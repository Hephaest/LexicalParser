/**
 * This class is used to achieve the Recognizer.
 * This class will read the file line by line and transfer to the FMS character by character.
 * @author Miao Cai
 * @since 15/12/2019 9:32 PM
 */
package Scanner;

import Utils.*;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;

public class Recognizer {
    // Variables declaration
    private final int ERROR = -1;
    private final int SUCCESS = 0;

    private String line = "";
    private int row = 1;
    private int col = 0;
    private BufferedReader br;
    private JTextArea tArea;
    private JButton finishBtn;
    private JButton openBtn;
    private CommentTracker ct;
    private SymbolTracker st;
    private StringTracker strTracker;
    private FSM machine;
    private StringBuilder sb = new StringBuilder();

    /**
     * This constructor will create a new object of the Recognizer.
     * @param br The object of BufferedReader class.
     * @param tArea The object of JTextArea class.
     * @param finishBtn The object of JButton class.
     * @param openBtn The object of JButton class.
     */
    public Recognizer(BufferedReader br, JTextArea tArea, JButton finishBtn, JButton openBtn){
    // Initialization.
    this.br = br;
    this.ct = new CommentTracker(0);
    this.st = new SymbolTracker(0, tArea);
    this.strTracker = new StringTracker(false);
    machine = new FSM(ct, st, strTracker, tArea);
    this.tArea = tArea;
    this.finishBtn = finishBtn;
    this.openBtn = openBtn;
    checkEverySingleLine();
    }

    /**
     * This method is used to read the buffer line by line.
     */
    private void checkEverySingleLine() {

        boolean skip = false;
        try {
        while (((line = br.readLine())!= null)) {
            if (checkEverySingleWord(line, row) == ERROR) {
                skip = true;
                break;
            }
            row ++;
        }
        br.close();
        if (!skip) {
            /* If there is anything redundant, report an error. */
            if (ct.getCommentState() > 0){
                ErrorReport.unclosedComtError(ct.getUnclosedRowPos(), ct.getUnclosedColPos(), tArea);
                ErrorReport.parsingError(--row, col, tArea);
            } else if (ct.getCommentState() < 0){
                ErrorReport.illegalStartError(ct.getUnclosedRowPos(), ct.getUnclosedColPos(), tArea);
            } else if (strTracker.hasRedundantQuote()){
                ErrorReport.unclosedStrError(strTracker.getUnclosedRowPos(), strTracker.getUnclosedColPos(), tArea);
            } else if (!st.hasRedundantBrackets()){
                tArea.append("Successfully parsing!\n");
            }
        }
        // Set back to default state.
        finishBtn.setEnabled(true);
        openBtn.setEnabled(true);
        } catch (IOException e) {
            ErrorReport.ioError(tArea);
        }
    }

    /**
     * This method is used to read the line character by character.
     * @param line The content of the file.
     * @param row The current row.
     * @return The parsing status.
     */
    private int checkEverySingleWord(String line, int row){
        for (col = 0; col < line.length(); col ++){
            if ((col = machine.changeState(line, col, row, sb)) == ERROR)
                return ERROR;
            sb.setLength(0);
        }
        return SUCCESS;
    }


}
