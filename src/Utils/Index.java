/**
 * This class extends Token class and is used to create an Index object.
 * This class also have some mutable properties.
 * @author Miao Cai
 * @since 15/12/2019 9:32 PM
 */
package Utils;

public class Index extends Token{

    // Variables declaration
    private int index;

    /**
     * This constructor is used to create a new Index object.
     * @param type The type of the token.
     * @param value The value of the token.
     * @param index The unique id of the token.
     */
    public Index (String type, String value, int index){
        super(type, value);
        this.index = index;
    }

    /**
     * This method is used to check whether the value of other Index object is the same as this one.
     * @param value The value of other Index object.
     * @return A boolean result.
     */
    public boolean equals(String value) {
        return super.equals(value);
    }

    /**
     * This method is used to get the all information of this Index object.
     * @return The string of information of this Index object.
     */
    public String getInfo(){
        return super.getInfo() + index + " >" + "\n";
    }
}
