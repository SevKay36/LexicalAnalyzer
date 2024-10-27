import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    // Define sets for keywords, operators, and separators to identify tokens in the input
    private static final Set<String> keywords = new HashSet<>(Set.of("if", "else", "return", "int", "float", "String", "Boolean", "for", "while"));
    private static final Set<String> operators = new HashSet<>(Set.of("+", "-", "*", "/", "=", "==", "!=", ">", "<", "<=", ">=", "and", "or", "++", "--"));
    private static final Set<String> separators = new HashSet<>(Set.of("(", ")", "{", "}", ",", ";"));

    public static void main(String[] args) {
        
        // Path to the input file
        String path = "C:\\Users\\sevou\\Desktop\\HU\\HU SEM 8 (Fall)\\CSC239\\CSC239 Projects\\Mini-Project\\LexicalAnalyzer\\input.txt";

        List<Token> tokens = new ArrayList<>(); // List to store all tokens
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            int lineNumber = 1; // Track the current line number

            // Read each line of the input file
            while ((line = reader.readLine()) != null) {
                // Validate 'if', 'for', and 'while' statements and display line number on error
                if (!validateSyntax(line, lineNumber)) {
                    return; // Stop further tokenization if a syntax error is found
                }

                tokens.addAll(tokenize(line)); // Tokenize each line and collect tokens
                lineNumber++; // Increment the line number after processing the line
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        // Print each token along with its type
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    /**
     * Validate the syntax of 'if', 'for', and 'while' statements using regex.
     * Ensures that these statements are followed by parentheses and valid conditions.
     * Includes the line number in case of an error.
     */
    public static boolean validateSyntax(String line, int lineNumber) {
        // Regex to check for 'if' or 'while' statements in the form: if (condition) or while (condition)
        String ifWhileRegex = "(if|while)\\s*\\(.*\\)";
        
        // Regex to check for 'for' statements in the form: for (initialization; condition; increment)
        String forRegex = "for\\s*\\(.*;.*;.*\\)"; 

        Pattern ifWhilePattern = Pattern.compile(ifWhileRegex);
        Pattern forPattern = Pattern.compile(forRegex);

        Matcher ifWhileMatcher = ifWhilePattern.matcher(line);
        Matcher forMatcher = forPattern.matcher(line);

        // Check for 'if' or 'while' syntax errors
        if (line.contains("if") || line.contains("while")) {
            if (!ifWhileMatcher.find()) {
                System.err.println("Syntax error on line " + lineNumber + ": Invalid 'if' or 'while' statement.");
                return false;
            }
        }

        // Check for 'for' syntax errors
        if (line.contains("for")) {
            if (!forMatcher.find()) {
                System.err.println("Syntax error on line " + lineNumber + ": Invalid 'for' statement.");
                return false;
            }
        }

        return true; // No syntax error found
    }

    /**
     * Tokenize the input line and return a list of tokens.
     * Uses a regex pattern to match different types of tokens.
     */
    public static List<Token> tokenize(String line) {
        List<Token> tokens = new ArrayList<>();
        
        // Adjust regex pattern to treat invalid identifiers like "3121x" as a single token
        Pattern pattern = Pattern.compile(
                "\"[^\"]*\"|" +       // Matches strings enclosed in double quotes (e.g., "text")
                "\\d+\\.\\d+|" +     // Matches floating-point numbers (e.g., 3.14)
                "\\d+([a-zA-Z_][a-zA-Z0-9_]*)?|" + // Matches numbers or invalid identifiers like "1x"
                "==|!=|>=|<=|\\+\\+|--|" +     // Matches multi-character operators (==, !=, >=, <=, ++, --)
                "[+\\-*/=<>!]+|" +   // Matches single-character operators (e.g., +, -, *, /, etc.)
                "[a-zA-Z_][a-zA-Z0-9_]*|" + // Matches valid identifiers
                "[(){};,]");         // Matches separators (e.g., parentheses, braces, commas, semicolons)

        // Matcher object used to find tokens based on the defined pattern in the input line
        Matcher matcher = pattern.matcher(line);

        // Loop through all found tokens in the line
        while (matcher.find()) {
            String part = matcher.group();  // Get the matched token
            tokens.add(classifyToken(part)); // Classify and add each token to the list
        }
        return tokens;
    }

    // Classify each token based on predefined categories
    public static Token classifyToken(String token) {
        if (keywords.contains(token)) {
            return new Token(token, "KEYWORD");
        } else if (isString(token)) {  // Check for string literals
            return new Token(token, "STRING");
        } else if (isNumber(token)) {  // Check for numbers
            return new Token(token, "NUMBER");
        } else if (isInvalidIdentifier(token)) { // Check for invalid identifiers
            return new Token(token, "INVALID_IDENTIFIER");
        } else if (isIdentifier(token)) { // Check for valid identifiers
            return new Token(token, "IDENTIFIER");
        } else if (operators.contains(token)) { // Check for operators
            return new Token(token, "OPERATOR");
        } else if (separators.contains(token)) { // Check for separators
            return new Token(token, "SEPARATOR");
        } else {
            return new Token(token, "UNKNOWN");
        }
    }

    // Check if token is a number (matches integers and floating-point numbers)
    public static boolean isNumber(String token) {
        return token.matches("\\d+(\\.\\d+)?"); // Regex: digits, optionally followed by decimal digits
    }

    // Check if token is a string (enclosed in quotes)
    public static boolean isString(String token) {
        return token.matches("\".*\""); // Regex: starts and ends with double quotes
    }

    // Check if token is a valid identifier (variable or function name)
    public static boolean isIdentifier(String token) {
        return token.matches("[a-zA-Z_][a-zA-Z0-9_]*"); // Regex: starts with letter/underscore, may include digits
    }

    // Check if the token is an invalid identifier (starts with a digit but has letters)
    public static boolean isInvalidIdentifier(String token) {
        // If it starts with a digit and contains any letters, it's invalid
        return token.matches("\\d+[a-zA-Z_]+");
    }
}
