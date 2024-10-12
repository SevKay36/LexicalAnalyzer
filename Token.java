// Represents a lexical token with a value and type
public class Token {
    private String value; // Token value
    private String type;  // Token type

    // Constructor to initialize a token with a specified value and type
    public Token(String value, String type) {
        this.value = value;
        this.type = type;
    }

    // Returns the token's value
    public String getValue() {
        return value;
    }

    // Returns the token's type
    public String getType() {
        return type;
    }

    // Overriding: To provide a formatted string for the token
    @Override
    public String toString() {
        return "(" + value + ", " + type + ")";
    }
}
