import tools.Grammar;
import tools.GrammarParser;
import tools.ParserGenerator;

import java.io.FileReader;
import java.io.IOException;

public class Main {

    final private static String GRAMMAR_FILE_EXTENSION = ".g";

    public static void main(String[] args) throws IOException {
        Grammar grammar = new GrammarParser(new FileReader("booleanExpression" + GRAMMAR_FILE_EXTENSION)).parse();
        new ParserGenerator(grammar).generate();
    }
}
