import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

            print(grammar);
        } catch (Exception e) {
            System.err.printf(FAIL_MESSAGE, dirName + fileName, e.getMessage());
        }
    }

    private static void print(FullGrammar grammar) {
        List<String> terminals = new ArrayList<>(grammar.getTerminals());
        terminals.add(0, "");

        Object[][] data = new Object[grammar.getNonTerminals().size()][];
        for (int i = 0; i < grammar.getNonTerminals().size(); i++) {
            String nonTerminal = grammar.getNonTerminals().get(i);
            Set<String> first = grammar.getFirsts().get(i);
            Set<String> follow = grammar.getFollows().get(i);

            if (grammar.getNullables().get(i)) {
                first.add("ε");
            }

            System.out.printf("FIRST(%s) = %s\n", nonTerminal, replaceBracketsWithBraces(first));
            System.out.printf("FOLLOW(%s) = %s\n\n", nonTerminal, replaceBracketsWithBraces(follow));
            List<Integer> row = grammar.getTable().get(i);

            List<Object> tableRow = new ArrayList<>();
            tableRow.add(nonTerminal);
            for (int j : row) {
                tableRow.add(j == -1 ? "" : j + 1);
            }

            data[i] = tableRow.toArray();
        }

        JTable table = new JTable(data, terminals.toArray(new Object[terminals.size()]));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);

        JScrollPane pane = new JScrollPane(table);
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(pane);
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        JFrame.setDefaultLookAndFeelDecorated(true);
    }

    private static String replaceBracketsWithBraces(Set<?> set) {
        return set.toString().replace("[", "{ ").replace("]", " }");
    }
}
