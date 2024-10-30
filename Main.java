import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    // Define sets for keywords, operators, and separators to identify tokens in the input
    private static final Set<String> keywords = new HashSet<>(Set.of("if", "else", "return", "int", "float", "String", "Boolean", "for", "while"));
    private static final Set<String> operators = new HashSet<>(Set.of("+", "-", "*", "/", "=", "==", "!=", ">", "<", "<=", ">=", "&&", "||", "++", "--"));
    private static final Set<String> separators = new HashSet<>(Set.of("(", ")", "{", "}", ",", ";"));

    // Map to store declared variables with their types (key: identifier, value: type)
    private static final Map<String, String> declaredVariables = new HashMap<>();
    private static final List<String> errors = new ArrayList<>(); // List to store all errors

    public static void main(String[] args) {

        // Path to the input file
        String path = "C:\\Users\\sevou\\Desktop\\HU\\HU SEM 8 (Fall)\\CSC239\\CSC239 Projects\\Mini-Project\\LexicalAnalyzer\\input.txt";

        List<Token> tokens = new ArrayList<>(); // List to store all tokens
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            int lineNumber = 1; // Track the current line number

            // Read each line of the input file
            while ((line = reader.readLine()) != null) {
                // Validate 'if', 'for', and 'while' statements and collect errors
                validateSyntax(line, lineNumber);

                // Tokenize the line and check for errors
                List<Token> lineTokens = tokenize(line, lineNumber);
                if (lineTokens != null) {
                    tokens.addAll(lineTokens); // Tokenize each line and collect tokens
                }
                lineNumber++; // Increment the line number after processing the line
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        // If there are any errors, display them and suppress token output
        if (!errors.isEmpty()) {
            System.out.println("Errors found:");
            errors.forEach(System.out::println);
        } else {
            // Print each token along with its type only if no errors are found
            for (Token token : tokens) {
                System.out.println(token);
            }
        }
    }

    /**
     * Validate the syntax of 'if', 'for', and 'while' statements using regex.
     * Ensures that these statements are followed by parentheses and valid conditions.
     * Includes the line number in case of an error.
     */
    public static void validateSyntax(String line, int lineNumber) {
        // Regex to match a valid identifier
        String identifier = "[a-zA-Z_][a-zA-Z0-9_]*";
    
        // Regex to match a number literal (integer or floating-point)
        String numberLiteral = "\\d+(\\.\\d+)?";  // Allows integers and floating-point numbers
    
        // Regex to match a boolean literal (true, false)
        String booleanLiteral = "(true|false)";
    
        // Regex to match a string literal (enclosed in quotes)
        String stringLiteral = "\".*\"";
    
        // **Updated Regex for Comparison Operators** (>, <, >=, <=, ==, !=, &&, ||)
        String comparisonOperator = "(==|!=|>=|<=|>|<|&&|\\|\\|)";
    
        // Full regex for 'if' or 'while' condition with multiple conditions allowed (identifier|literal operator identifier|literal)
        String ifWhileRegex = "(if|while)\\s*\\((" + identifier + "|" + numberLiteral + "|" + booleanLiteral + "|" + stringLiteral + ")\\s*"
                            + comparisonOperator + "\\s*"
                            + "(" + identifier + "|" + numberLiteral + "|" + booleanLiteral + "|" + stringLiteral + ")"
                            + "(\\s*(&&|\\|\\|)\\s*"
                            + "(" + identifier + "|" + numberLiteral + "|" + booleanLiteral + "|" + stringLiteral + ")"
                            + "\\s*" + comparisonOperator + "\\s*"
                            + "(" + identifier + "|" + numberLiteral + "|" + booleanLiteral + "|" + stringLiteral + "))*\\)";
    
        // Regex for 'for' loop (from the previous logic)
        String forRegex = "for\\s*\\(\\s*" + "(int|float|double|String|Boolean)" + "\\s+" + identifier + "\\s*=\\s*" + numberLiteral 
                          + "\\s*;\\s*" + identifier + "\\s*" + comparisonOperator + "\\s*" + numberLiteral 
                          + "\\s*;\\s*" + identifier + "\\s*(\\+\\+|--)\\s*\\)";
    
        // **New Regex for String Declaration** (String x = "value";)
        String stringDeclarationRegex = "String\\s+(" + identifier + ")\\s*=\\s*" + stringLiteral + "\\s*;";
    
        // Regex for declaration statements (int x = 10; or Boolean isActive = true;)
        String declarationRegex = "(int|float|double|Boolean)\\s+(" + identifier + ")\\s*=\\s*(" 
                                + numberLiteral + "|" + booleanLiteral + "|" + stringLiteral + ")\\s*;";
    
        // Regex for reassignment statements (e.g., name = "Sevag";)
        String reassignmentRegex = "(" + identifier + ")\\s*=\\s*(" + numberLiteral + "|" + booleanLiteral + "|" + stringLiteral + "|" + identifier + ")\\s*;";
    
        // Compile the regex statements into a Pattern object
        Pattern ifWhilePattern = Pattern.compile(ifWhileRegex);
        Pattern forPattern = Pattern.compile(forRegex);
        Pattern stringDeclarationPattern = Pattern.compile(stringDeclarationRegex);
        Pattern declarationPattern = Pattern.compile(declarationRegex);
        Pattern reassignmentPattern = Pattern.compile(reassignmentRegex);
    
        // Check for 'if' or 'while' syntax errors
        Matcher ifWhileMatcher = ifWhilePattern.matcher(line);
        if (line.contains("if") || line.contains("while")) {
            if (!ifWhileMatcher.find()) {
                errors.add("Syntax error on line " + lineNumber + ": Invalid 'if' or 'while' statement.");
            }
        }
    
        // Check for 'for' syntax errors
        Matcher forMatcher = forPattern.matcher(line);
        if (line.contains("for")) {
            if (!forMatcher.find()) {
                errors.add("Syntax error on line " + lineNumber + ": Invalid 'for' statement.");
            }
        }
    
        // Check for string declaration errors (must be enclosed in quotes)
        Matcher stringDeclarationMatcher = stringDeclarationPattern.matcher(line);
        if (stringDeclarationMatcher.find()) {
            String varName = stringDeclarationMatcher.group(1);
            
            // Check for duplicate identifier
            if (declaredVariables.containsKey(varName)) {
                errors.add("Error on line " + lineNumber + ": Duplicate identifier '" + varName + "'.");
            } else {
                declaredVariables.put(varName, "String"); // Store the variable after validation
            }
        } else if (line.contains("String")) {
            errors.add("Error on line " + lineNumber + ": String value must be enclosed in double quotes.");
        }

        // ** Check for int, float, and Boolean declaration errors**
        Matcher declarationMatcher = declarationPattern.matcher(line);
        if (declarationMatcher.find()) {
            String type = declarationMatcher.group(1);
            String varName = declarationMatcher.group(2);
            String value = declarationMatcher.group(3);
            
            // Check for duplicate identifier
            if (declaredVariables.containsKey(varName)) {
                errors.add("Error on line " + lineNumber + ": Duplicate identifier '" + varName + "'.");
            } else {
                // **Type mismatch check for int, float, Boolean**
                String errorMessage = "Error on line " + lineNumber + ": Type mismatch for variable '" + varName + "'. Expected type: " + type;
                if (!isLiteralMatch(type, value) && !errors.contains(errorMessage)) {
                    errors.add(errorMessage);
                }
                declaredVariables.put(varName, type); // Store the variable after validation
            }
        } 
    
        // Check for reassignment syntax errors
        Matcher reassignmentMatcher = reassignmentPattern.matcher(line);
        if (reassignmentMatcher.find()) {
            String varName = reassignmentMatcher.group(1);
            String value = reassignmentMatcher.group(2);
    
            // Ensure the variable was declared
            if (!declaredVariables.containsKey(varName)) {
                errors.add("Error on line " + lineNumber + ": Variable '" + varName + "' is not declared.");
            } else {
                // Ensure the type matches the declared type
                String expectedType = declaredVariables.get(varName);
                String errorMessage = "Error on line " + lineNumber + ": Type mismatch for variable '" + varName + "'. Expected type: " + expectedType;
                if (expectedType != null && !isLiteralMatch(expectedType, value) && !errors.contains(errorMessage)) {
                    errors.add(errorMessage);
                }
            }
        }
    }

    // Helper function to check if the value matches the expected type
    public static boolean isLiteralMatch(String expectedType, String value) {
        if (value == null) return false;

        switch (expectedType) {
            case "int":
                return value.matches("\\d+");  // Integers must be whole numbers with no decimals
            case "float":
            case "double":
                return value.matches("\\d+(\\.\\d+)?");  // Floats and doubles can have decimal points
            case "String":
                return value.matches("\".*\""); // Ensure the value is enclosed in quotes for String
            case "Boolean":
                return value.equals("true") || value.equals("false");  // Booleans must be "true" or "false"
            default:
                return false;
        }
    }

    /**
     * Tokenize the input line and return a list of tokens.
     * If an unrecognized character or invalid identifier is found, print an error and return `null` to stop further processing.
     */
    public static List<Token> tokenize(String line, int lineNumber) {
        List<Token> tokens = new ArrayList<>();
        
        // Adjust regex pattern to treat invalid identifiers like "3121x" as a single token
        Pattern pattern = Pattern.compile(
                "\"[^\"]*\"|" +       // Matches strings enclosed in double quotes (e.g., "text")
                "\\d+\\.\\d+|" +     // Matches floating-point numbers (e.g., 3.14)
                "\\d+([a-zA-Z_][a-zA-Z0-9_]*)?|" + // Matches numbers or invalid identifiers like "1x"
                "==|!=|>=|<=|&&|\\|\\||\\+\\+|--|" +     // Matches multi-character operators (==, !=, >=, <=, &&, ||, ++, --)
                "[+\\-*/=<>!]+|" +   // Matches single-character operators (e.g., +, -, *, /, etc.)
                "[a-zA-Z_][a-zA-Z0-9_]*|" + // Matches valid identifiers
                "[(){};,]|" +        // Matches separators (e.g., parentheses, braces, commas, semicolons)
                "[^\\s]");           // Matches unrecognized characters (anything not matched by above patterns)

        // Matcher object used to find tokens based on the defined pattern in the input line
        Matcher matcher = pattern.matcher(line);

        // Loop through all found tokens in the line
        while (matcher.find()) {
            String part = matcher.group();  // Get the matched token
            Token token = classifyToken(part, lineNumber);
            if (token.getType().equals("UNKNOWN") || token.getType().equals("INVALID_IDENTIFIER")) {
                // If an unrecognized character or invalid identifier is found, stop further tokenization
                return null;
            }
            tokens.add(token); // Otherwise, continue adding tokens
        }

        return tokens;
    }

    // Classify each token based on predefined categories, handling unrecognized characters and invalid identifiers
    public static Token classifyToken(String token, int lineNumber) {
        if (keywords.contains(token)) {
            return new Token(token, "KEYWORD");
        } else if (isString(token)) {  // Check for string literals
            return new Token(token, "STRING");
        } else if (isBoolean(token)) {  // Check for booleans
            return new Token(token, "BOOLEAN");
        } else if (isNumber(token)) {  // Check for numbers
            return new Token(token, "NUMBER");
        } else if (isInvalidIdentifier(token)) { // Check for invalid identifiers
            errors.add("Invalid identifier on line " + lineNumber + ": '" + token + "'");
            return new Token(token, "INVALID_IDENTIFIER");
        } else if (isIdentifier(token)) { // Check for valid identifiers
            return new Token(token, "IDENTIFIER");
        } else if (operators.contains(token)) { // Check for operators
            return new Token(token, "OPERATOR");
        } else if (separators.contains(token)) { // Check for separators
            return new Token(token, "SEPARATOR");
        } else {
            // Handle unrecognized characters
            errors.add("Unrecognized character on line " + lineNumber + ": '" + token + "'");
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

    // Check if token is a boolean (true or false)
    public static boolean isBoolean(String token) {
        return token.equals("true") || token.equals("false");
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
