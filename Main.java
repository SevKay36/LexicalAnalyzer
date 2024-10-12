import java.util.Set;
import java.util.HashSet;

public class Main {

    // Define keywords, operators, and separators using HashSet for efficient lookup
    private static final Set<String> keywords = new HashSet<>(Set.of("if", "else", "return", "int", "float", "String", "Boolean"));
    private static final Set<String> operators = new HashSet<>(Set.of("+", "-", "*", "/", "=", "==", "!=", ">", "<"));
    private static final Set<String> separators = new HashSet<>(Set.of("(", ")", "{", "}", ",", ";"));

    public static void main(String[] args) {

    }
}