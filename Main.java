import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;

public class Main {

    private static final Set<String> keywords = new HashSet<>(Set.of("if", "else", "return", "int", "float", "String", "Boolean"));
    private static final Set<String> operators = new HashSet<>(Set.of("+", "-", "*", "/", "=", "==", "!=", ">", "<"));
    private static final Set<String> separators = new HashSet<>(Set.of("(", ")", "{", "}", ",", ";"));

    public static void main(String[] args) {
        
        // Don't forget to Change path here according to your path!
        String path = "C:\\Users\\sevou\\Desktop\\HU\\HU SEM 8 (Fall)\\CSC239\\CSC239 Assignments\\Assignment 3\\LexicalAnalyzer\\input.txt";

        // Initialize BufferedReader to read input from the specified file path
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // This is where tokenization and syntax checking will occur
                System.out.println(line); // Temporary line to show file content
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}
