import java.util.*;

public class Grammar {

    final private static String EOF = "$";
    final private  List<Rule> rules;
    final private String start;

    final private List<String> nonTerminals;
    final private Map<String, Integer> nonTerminalIndices;

    final private List<Boolean> nullables = new ArrayList<>();
    final private List<Set<String>> firsts = new ArrayList<>();
    final private List<Set<String>> follows = new ArrayList<>();

    public Grammar(List<Rule> rules, String start) {
        this.rules = rules;
        this.start = start;

        Set<String> leftSides = new HashSet<>();
        for (Rule rule : rules) {
            leftSides.add(rule.getLeftSide());
        }
        nonTerminals = new ArrayList<>(leftSides);

        nonTerminalIndices = new HashMap<>();
        for (int i = 0; i < nonTerminals.size(); i++) {
            nonTerminalIndices.put(nonTerminals.get(i), i);
        }

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

            for (String string : first(symbol)) {
                result.add(string);
            }

            if (!isNullable(symbol)) {
                isNullable = false;
                break;
            }
        }

        if (isNullable) {
            for (String symbol : follow) {
                result.add(symbol);
            }
        }

        return result;
    }

    private Set<String> first(String symbol) {
        if (!isTerminal(symbol)) {
            return firsts.get(nonTerminalIndices.get(symbol));
        } else {
            Set<String> result = new HashSet<>();
            result.add(symbol);

            return result;
        }
    }
}
