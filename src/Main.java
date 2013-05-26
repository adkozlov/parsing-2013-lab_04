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
        final String dirName = "./booleanExpressions/";
        final String fileName = "booleanExpressions";

        try {
            System.out.printf(START_MESSAGE, dirName + fileName);

            CharStream input = new ANTLRInputStream(new FileInputStream(dirName + fileName + GRAMMAR_FILE_EXTENSION));
            GrammarLexer lexer = new GrammarLexer(input);

            GrammarParser parser = new GrammarParser(new CommonTokenStream(lexer));
            parser.file();

            FullGrammar grammar = parser.getFullGrammar();
            //new ParserGenerator(grammar, dirName, fileName).generateSourceFiles();

            System.out.printf(SUCCESS_MESSAGE, dirName + fileName);
        } catch (Exception e) {
            System.err.printf(FAIL_MESSAGE, dirName + fileName, e.getMessage());
        }
    }
}
