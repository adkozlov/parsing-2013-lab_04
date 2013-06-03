public class Condition {

    final private String condition;
    final private String value;

    public Condition(String condition, String value) {
        this.condition = condition;
        this.value = value;
    }

    public String getCondition() {
        return condition;
    }

    public String getValue() {
        return value;
    }
}
