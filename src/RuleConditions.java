import java.util.List;

public class RuleConditions {

    final private List<Condition> conditions;
    final private int ruleId;

    public RuleConditions(List<Condition> conditions, int ruleId) {
        this.conditions = conditions;
        this.ruleId = ruleId;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public int getRuleId() {
        return ruleId;
    }
}
