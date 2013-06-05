//import booleanExpressions.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;

public class Tester {

    public static void main(String[] args) throws ParseException, IOException, GrammarException {
        Main.main(args);
        //Equivalence_ParseTree parseTree = (Equivalence_ParseTree) new BooleanExpressionsParser().parse(new FileInputStream("./src/booleanExpressions/test.in"));
        //System.out.println(parseTree.get_value());
    }
}
