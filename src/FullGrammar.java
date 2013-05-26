import java.util.*;

public class FullGrammar extends Grammar {

    final private Map<String, String> terminals;
    final private Map<String, String> nonTerminals;
    final private Map<String, List<Attribute>> attributes;
    final private List<List<String>> actions;

    final private Map<String, Integer> terminalIndices;

    final private List<List<Integer>> table;

    public FullGrammar(List<Rule> rules, String start, Map<String, String> terminals, Map<String, String> nonTerminals, Map<String, List<Attribute>> attributes, List<List<String>> actions) throws GrammarException {
        super(rules, start);
        this.terminals = terminals;
        this.nonTerminals = nonTerminals;
        this.attributes = attributes;
        this.actions = actions;

        terminalIndices = new HashMap<>();
        for (String terminal : terminals.keySet()) {
            terminalIndices.put(terminal, terminalIndices.size());
        }
        terminalIndices.put(Grammar.EOF, terminalIndices.size());

        table = new ArrayList<>();
        for (Iterator<String> i = nonTerminalIndices.keySet().iterator(); i.hasNext(); i.next()) {
            List<Integer> row = new ArrayList<>();

            for (Iterator<String> j = terminalIndices.keySet().iterator(); j.hasNext(); j.next()) {
                row.add(-1);
            }

            table.add(row);
        }
        fillTable();
    }

    public List<List<Integer>> getTable() {
        return table;
    }

    public List<String> getTerminals() {
        return new ArrayList<>(terminals.keySet());
    }

    private void fillTable() throws GrammarException {
        for (int i = 0; i < rules.size(); i++) {
            Rule rule = rules.get(i);
            int leftIndex = nonTerminalIndices.get(rule.getLeftSide());

            for (String symbol : first(rule.getRightSide())) {
                int rightIndex = terminalIndices.get(symbol);

                setValue(leftIndex, rightIndex, i);
            }

            if (isNullable(rule.getRightSide())) {
                for (String symbol : follows.get(leftIndex)) {
                    int rightIndex = terminalIndices.get(symbol);
                    setValue(leftIndex, rightIndex, i);
                }
            }
        }
    }

    private void setValue(int leftIndex, int rightIndex, int ruleIndex) throws GrammarException{
        List<Integer> row = table.get(leftIndex);
        if (row.get(rightIndex) == ruleIndex) {
            return;
        } else if (row.get(rightIndex) == -1) {
            row.set(rightIndex, ruleIndex);
        } else {
            throw new GrammarException("rules conflict");
        }
    }

    private Set<String> first(List<String> rightSide) {
        Set<String> result = new HashSet<>();

        for (String symbol : rightSide) {
            result.addAll(first(symbol));

            if (!isNullable(symbol)) {
                break;
            }
        }

        return result;
    }

    public boolean isNullable(List<String> rightSide) {
        for (String symbol : rightSide) {
            if (!isNullable(symbol)) {
                return false;
            }
        }

        return true;
    }
}
