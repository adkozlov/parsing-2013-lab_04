import booleanExpressions.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;

public class Tester {

    public static void main(String[] args) throws ParseException, FileNotFoundException {
        BooleanExpressionsParseTree parser = new BooleanExpressionsParser().parse(new FileInputStream("./src/booleanExpressions/test.in"));
    }
}
