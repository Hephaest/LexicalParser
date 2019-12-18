/**
 * This class is used to create an Token object.
 * This class also have some mutable properties.
 * @author Miao Cai
 * @since 15/12/2019 9:32 PM
 */
package Utils;

public class Token {
    // Variables declaration
    private String TOKEN_KEY;
    private String TOKEN_VALUE;

    /**
     * This constructor is used to create a new Token object.
     * @param type The type of the token.
     * @param value The value of the token.
     */
    public Token (String type, String value){
        TOKEN_KEY = type;
        TOKEN_VALUE = value;
    }

    /**
     * This method is used to check whether the value of other Token object is the same as this one.
     * @param value The value of other Token object.
     * @return A boolean result.
     */
    public boolean equals(String value) {
        return TOKEN_VALUE.equals(value);
    }

    /**
     * This method is used to get the all information of this Token object.
     * @return The string of information of this Token object.
     */
    public String getInfo() {
        return "< " + TOKEN_KEY + ", " + TOKEN_VALUE+ ", ";
    }
}
