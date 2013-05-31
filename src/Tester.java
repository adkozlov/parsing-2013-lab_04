import booleanExpressions.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;

public class Tester {

    public static void main(String[] args) throws ParseException, FileNotFoundException {
        Main.main(args);
        Expression_ParseTree parseTree = (Expression_ParseTree) new BooleanExpressionsParser().parse(new FileInputStream("./src/booleanExpressions/test.in"));
        System.out.println(parseTree.value);
    }
}
