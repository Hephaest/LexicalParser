/**
 * This class is used to track comment symbols.
 * @author Miao Cai
 * @since 15/12/2019 9:32 PM
 */
package Utils;

public class CommentTracker {
    // Variables declaration
    private int hasNotYetFinishComment;
    private int unclosedRowPos;
    private int unclosedColPos;

    /* Initialization. */
    public CommentTracker(int status){
        this.hasNotYetFinishComment = status;
    }

    /**
     * This method is used to update the tracking position.
     * @param row The new row.
     * @param col the new column.
     */
    public void updateUnclosedPosition(int row, int col){
        unclosedRowPos = row;
        unclosedColPos = col;
    }

    /**
     * This method is used to get the row of the tracking position.
     * @return The row of the tracking position.
     */
    public int getUnclosedRowPos(){
        return unclosedRowPos;
    }

    /**
     * This method is used to get the column of the tracking position.
     * @return The column of the tracking position.
     */
    public int getUnclosedColPos(){
        return unclosedColPos;
    }

    /**
     * This method is used to get the status of the tracking.
     * @return The status of the tracking.
     */
    public int getCommentState(){
        return hasNotYetFinishComment;
    }

    /**
     * This method is used to set the status of the tracking.
     * @param status The new status.
     */
    public void setCommentState(int status){
        hasNotYetFinishComment += status;
    }
}
