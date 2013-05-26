import java.util.List;
import java.util.Map;

public class FullGrammar extends Grammar {

    final private Map<String, String> terminals;
    final private Map<String, String> nonTerminals;
    final private Map<String, List<Attribute>> attributes;
    final private List<List<String>> actions;

    public FullGrammar(List<Rule> rules, String start, Map<String, String> terminals, Map<String, String> nonTerminals, Map<String, List<Attribute>> attributes, List<List<String>> actions) {
        super(rules, start);
        this.terminals = terminals;
        this.nonTerminals = nonTerminals;
        this.attributes = attributes;
        this.actions = actions;
    }
}
