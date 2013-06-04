import booleanExpressions.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;

public class Tester {

    public static void main(String[] args) throws ParseException, IOException, GrammarException {
        Main.main(args);
        Expression_ParseTree parseTree = (Expression_ParseTree) new BooleanExpressionsParser().parse(new FileInputStream("./src/booleanExpressions/test.in"));
        System.out.println(parseTree.get_value());
    }
}
