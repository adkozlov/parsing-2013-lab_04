import org.antlr.v4.runtime.*;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

public class Main {

    final private static String GRAMMAR_FILE_EXTENSION = ".g";

    private static final String TESTS_FORMAT = "%s";
    private static final String START_MESSAGE = TESTS_FORMAT + " started\n";
    private static final String SUCCESS_MESSAGE = TESTS_FORMAT + " succeeded\n";
    private static final String FAIL_MESSAGE = TESTS_FORMAT + " failed: %s\n";

    public static void main(String[] args) {
        final String fileName = "booleanExpressions";

        try {
            System.out.printf(START_MESSAGE, fileName);

            CharStream input = new ANTLRInputStream(new FileInputStream(fileName + GRAMMAR_FILE_EXTENSION));
            GrammarLexer lexer = new GrammarLexer(input);

            GrammarParser parser = new GrammarParser(new CommonTokenStream(lexer));
            parser.file();

            Grammar grammar = parser.getGrammar();
            List<List<String>> a = parser.getActions();
            Map<String, List<Attribute>> b = parser.getAttributesMap();
            //new ParserGenerator(grammar).generate();

            System.out.printf(SUCCESS_MESSAGE, fileName);
        } catch (Exception e) {
            System.err.printf(FAIL_MESSAGE, fileName, e.getMessage());
        }
    }
}
