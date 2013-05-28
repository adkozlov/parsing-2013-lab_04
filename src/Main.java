import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.FileInputStream;
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

            print(grammar);
            System.out.printf(SUCCESS_MESSAGE, dirName + fileName);
        } catch (Exception e) {
            System.err.printf(FAIL_MESSAGE, dirName + fileName, e.getMessage());
        }
    }

    private static void print(FullGrammar grammar) {
        String[] header = new String[grammar.getTerminals().size() + 4];
        header[0] = "";
        header[1] = "FIRST";
        header[2] = "FOLLOW";
        for (int i = 0; i < grammar.getTerminals().size(); i++) {
            header[i + 3] = grammar.getTerminals().get(i);
        }
        header[grammar.getTerminals().size() + 3] = Grammar.EOF;

        Object[][] data = new Object[grammar.getNonTerminals().size()][];
        for (int i = 0; i < grammar.getNonTerminals().size(); i++) {
            String nonTerminal = grammar.getNonTerminals().get(i);
            Set<String> first = grammar.getFirsts().get(i);
            Set<String> follow = grammar.getFollows().get(i);
            if (grammar.getNullables().get(i)) {
                first.add("ε");
            }

            Object[] tableRow = new Object[grammar.getTerminals().size() + 4];
            tableRow[0] = nonTerminal;
            tableRow[1] = replaceBracketsWithBraces(first);
            tableRow[2] = replaceBracketsWithBraces(follow);

            List<Integer> row = grammar.getTable().get(i);
            for (int j = 0; j < row.size(); j++) {
                int ruleIndex = row.get(j);
                tableRow[j + 3] = (ruleIndex == -1) ? "" : ruleIndex + 1;
            }

            data[i] = tableRow;
        }

        for (String terminal : grammar.getTerminals()) {
            header[grammar.getTerminalIndex(terminal) + 3] = terminal;
        }

        JTable table = new JTable(data, header) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setPreferredScrollableViewportSize(table.getPreferredSize());

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, renderer);
        table.getTableHeader().setDefaultRenderer(renderer);

        JScrollPane pane = new JScrollPane(table);

        JFrame frame = new JFrame("Parse table");
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(pane);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        JFrame.setDefaultLookAndFeelDecorated(true);
    }

    private static String replaceBracketsWithBraces(Set<?> set) {
        String result = set.toString();
        return "{ " + result.substring(1, result.length() - 1) + " }";
    }
}
