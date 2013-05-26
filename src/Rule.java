import java.util.List;

public class Rule {

    final private String leftSide;
    final private List<String> rightSide;

    public Rule(String leftSide, List<String> rightSide) {
        this.leftSide = leftSide;
        this.rightSide = rightSide;
    }

    public String getLeftSide() {
        return leftSide;
    }

    public List<String> getRightSide() {
        return rightSide;
    }
}
