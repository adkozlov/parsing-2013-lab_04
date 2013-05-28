import java.util.*;

public class Grammar {

    final public static String EOF = "$";
    protected final List<Rule> rules;
    final private String start;

    final private List<String> nonTerminals;
    protected final Map<String, Integer> nonTerminalIndices;

    final protected List<Boolean> nullables = new ArrayList<>();
    final protected List<Set<String>> firsts = new ArrayList<>();
    final protected List<Set<String>> follows = new ArrayList<>();

    public String getStart() {
        return start;
    }

    public List<Boolean> getNullables() {
        return nullables;
    }

    public List<Set<String>> getFirsts() {
        return firsts;
    }

    public List<Set<String>> getFollows() {
        return follows;
    }

    public List<String> getNonTerminals() {
        return nonTerminals;
    }

    public Grammar(List<Rule> rules, String start, List<String> nonTerminals, Map<String, Integer> nonTerminalIndices) {
        this.rules = rules;
        this.start = start;
        this.nonTerminals = nonTerminals;
        this.nonTerminalIndices = nonTerminalIndices;

        fillNullables();
        fillFirsts();
        fillFollows();
    }

    private void fillNullables() {
        for (ListIterator<String> iterator = nonTerminals.listIterator(); iterator.hasNext(); iterator.next()) {
            nullables.add(false);
        }

        boolean changed = true;
        while (changed) {
            changed = false;

            for (Rule rule : rules) {
                int index = nonTerminalIndices.get(rule.getLeftSide());

                if (!nullables.get(index)) {
                    boolean isNullable = true;

                    List<String> rightSide = rule.getRightSide();
                    for (String symbol : rightSide) {
                        if (!isNullable(symbol)) {
                            isNullable = false;
                        }
                    }

                    if (isNullable) {
                        nullables.set(index, true);
                        changed = true;
                    }
                }
            }
        }
    }

    public boolean isNullable(String string) {
        if (isTerminal(string)) {
            return false;
        }

        return nullables.get(nonTerminalIndices.get(string));
    }

    public boolean isTerminal(String string) {
        return !nonTerminalIndices.containsKey(string);
    }

    private void fillFirsts() {
        for (ListIterator<String> iterator = nonTerminals.listIterator(); iterator.hasNext(); iterator.next()) {
            firsts.add(new HashSet<String>());
        }

        boolean changed = true;
        while (changed) {
            changed = false;

            for (Rule rule : rules) {
                int index = nonTerminalIndices.get(rule.getLeftSide());
                List<String> rightSide = rule.getRightSide();

                if (!rightSide.isEmpty()) {
                    int current = 0;

                    do {
                        String symbol = rightSide.get(current);

                        if (isTerminal(symbol)) {
                            changed |= firsts.get(index).add(symbol);
                        } else {
                            for (String string : firsts.get(nonTerminalIndices.get(symbol))) {
                                changed |= firsts.get(index).add(string);
                            }
                        }
                    } while (isNullable(rightSide.get(current++)) && current < rightSide.size());
                }
            }
        }
    }

    private void fillFollows() {
        for (String nonTerminal : nonTerminals) {
            Set<String> follow = new HashSet<>();
            if (nonTerminal.equals(start)) {
                follow.add(EOF);
            }

            follows.add(follow);
        }

        boolean changed = true;
        while (changed) {
            changed = false;

            for (Rule rule : rules) {
                int index = nonTerminalIndices.get(rule.getLeftSide());
                List<String> rightSide = rule.getRightSide();

                for (ListIterator<String> iterator = rightSide.listIterator(); iterator.hasNext();) {
                    String symbol = iterator.next();

                    if (!isTerminal(symbol)) {
                        int current = nonTerminalIndices.get(symbol);

                        for (String string : followFromSuffix(rightSide, iterator.nextIndex(), follows.get(index))) {
                            changed |= follows.get(current).add(string);
                        }
                    }
                }
            }
        }
    }

    private Set<String> followFromSuffix(List<String> rightSide, int begin, Set<String> follow) {
        boolean isNullable = true;

        Set<String> result = new HashSet<>();
        for (ListIterator<String> iterator = rightSide.listIterator(begin); iterator.hasNext();) {
            String symbol = iterator.next();
            result.addAll(first(symbol));

            if (!isNullable(symbol)) {
                isNullable = false;
                break;
            }
        }

        if (isNullable) {
            result.addAll(follow);
        }

        return result;
    }

    protected Set<String> first(String symbol) {
        if (!isTerminal(symbol)) {
            return firsts.get(nonTerminalIndices.get(symbol));
        } else {
            Set<String> result = new HashSet<>();
            result.add(symbol);

            return result;
        }
    }
}
