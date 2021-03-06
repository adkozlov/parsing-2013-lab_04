import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class SourceFilesGenerator {

    final private FullGrammar grammar;
    final private String dirName;
    final private String fileName;
    final private String pkgName;

    final private static String FILES_EXTENSION = ".java";

    public SourceFilesGenerator(FullGrammar grammar, String dirName, String fileName) {
        this.grammar = grammar;
        this.dirName = dirName;

        char first = fileName.charAt(0);
        this.fileName = Character.toUpperCase(first) + fileName.substring(1);
        pkgName = fileName;
    }

    private void generateTokenSourceFiles() throws IOException {
        PrintWriter pw = new PrintWriter(dirName + fileName + "Token" + FILES_EXTENSION);

        pw.printf("package %s;\n\n" +
                "public class %sToken {\n\n" +
                "\tfinal public String symbol;\n\n" +
                "\tpublic %sToken(String symbol) {\n" +
                "\t\tthis.symbol = symbol;\n" +
                "\t}\n" +
                "}\n", pkgName, fileName, fileName);
        pw.close();

        for (Map.Entry<String, String> terminal : grammar.getTerminalsMap().entrySet()) {
            pw = new PrintWriter(dirName + terminal.getValue() + "_Token" + FILES_EXTENSION);

            pw.printf("package %s;\n\n" +
                    "public class %s_Token extends %sToken {\n\n", pkgName, terminal.getValue(), fileName);

            List<Attribute> attributes = grammar.getAttributesList(terminal.getValue());
            for (ListIterator<Attribute> iterator = attributes.listIterator(); iterator.hasNext();) {
                Attribute attribute = iterator.next();

                pw.printf("\tfinal public %s %s", attribute.getType(), attribute.getName());
                if (!attributeValueIsEmpty(attribute)) {
                    pw.printf(" = %s", attribute.getValue());
                }
                pw.printf(";\n");

                if (!iterator.hasNext()) {
                    pw.printf("\n");
                }
            }

            pw.printf("\tpublic %s_Token(", terminal.getValue());
            for (ListIterator<Attribute> iterator = attributes.listIterator(); iterator.hasNext();) {
                Attribute attribute = iterator.next();

                if (attributeValueIsEmpty(attribute)) {
                    pw.printf("final %s %s", attribute.getType(), attribute.getName());

                    if (iterator.hasNext()) {
                        pw.printf(", ");
                    }
                }
            }
            pw.printf(") {\n" +
                    "\t\tsuper(\"%s\");\n", terminal.getKey());
            for (Attribute attribute : attributes) {
                if (attributeValueIsEmpty(attribute)) {
                    pw.printf("\t\tthis.%s = %s;\n", attribute.getName(), attribute.getName());
                }
            }
            pw.printf("\t}\n");

            for (Attribute attribute : attributes) {
                pw.printf("\n" +
                        "\tpublic %s get_%s() {\n" +
                        "\t\treturn %s;\n" +
                        "\t}\n", attribute.getType(), attribute.getName(), attribute.getName());
            }

            pw.printf("}\n");

            pw.close();
        }
    }

    private boolean attributeValueIsEmpty(Attribute attribute) {
        return attribute.getValue().equals("");
    }

    private void generateLexicalAnalyzerSourceFiles() throws IOException {
        PrintWriter pw = new PrintWriter(dirName + fileName + "LexicalAnalyzer" + FILES_EXTENSION);

        pw.printf("package %s;\n\n" +
                "import java.io.IOException;\n" +
                "import java.io.InputStream;\n" +
                "import java.text.ParseException;\n\n" +
                "public class %sLexicalAnalyzer {\n\n", pkgName, fileName);
        pw.printf("\tprivate InputStream inputStream;\n" +
                "\tprivate String buffer = \"\";\n" +
                "\tprivate int currentCharacter;\n" +
                "\tprivate int currentPosition = 0;\n" +
                "\tprivate %sToken currentToken;\n\n" +
                "\tpublic %sLexicalAnalyzer(InputStream inputStream) throws ParseException {\n" +
                "\t\tthis.inputStream = inputStream;\n" +
                "\t\tnextCharacter();\n" +
                "\t}\n\n", fileName, fileName);
        pw.printf("\tprivate void nextCharacter() throws ParseException {\n" +
                "\t\ttry {\n" +
                "\t\t\tcurrentPosition++;\n" +
                "\t\t\tcurrentCharacter = inputStream.read();\n\n" +
                "\t\t\tif (currentCharacter != -1 && !isBlank(currentCharacter)) {\n" +
                "\t\t\t\tbuffer += (char) currentCharacter;\n" +
                "\t\t\t}\n" +
                "\t\t} catch (IOException e) {\n" +
                "\t\t\tthrow new ParseException(e.getMessage(), currentPosition);\n" +
                "\t\t}\n" +
                "\t}\n\n" +
                "\tprivate boolean isBlank(int character) {\n" +
                "\t\treturn character == ' ' || character == '\\n' || character == '\\t' || character == '\\r';\n" +
                "\t}\n\n" +
                "\tpublic boolean isEmpty() {\n" +
                "\t\treturn currentCharacter == -1;\n" +
                "\t}\n\n" +
                "\tpublic %sToken getCurrentToken() {\n" +
                "\t\treturn currentToken;\n" +
                "\t}\n\n" +
                "\tpublic int getCurrentPosition() {\n" +
                "\t\treturn currentPosition;\n" +
                "\t}\n\n", fileName);
        pw.printf("\tpublic void nextToken() throws ParseException {\n" +
                "\t\twhile (isBlank(currentCharacter)) {\n" +
                "\t\t\tnextCharacter();\n" +
                "\t\t}\n" +
                "\t\twhile (currentCharacter != -1 && !isBlank(currentCharacter)) {\n" +
                "\t\t\tnextCharacter();\n" +
                "\t\t}\n\n");

        boolean first = true;
        for (Map.Entry<String, String> terminal : grammar.getTerminalsMap().entrySet()) {
            if (terminal.getKey().equals(Grammar.EOF)) {
                continue;
            }

            if (first) {
                pw.printf("\t\t");
                first = false;
            } else {
                pw.printf(" else ");
            }

            pw.printf("if (buffer.equals(\"%s\")) {\n" +
                    "\t\t\tcurrentToken = new %s_Token();\n" +
                    "\t\t}", terminal.getKey(), terminal.getValue());
        }

        pw.printf(" else if (!buffer.equals(\"\")) {\n" +
                "\t\t\tthrow new ParseException(String.format(\"Illegal character '%%c' at position\", (int) currentCharacter), currentPosition);\n" +
                "\t\t}\n\n" +
                "\t\tif (currentCharacter == -1) {\n" +
                "\t\t\tcurrentToken = new EOF_Token();\n" +
                "\t\t}\n" +
                "\t\tbuffer = \"\";\n" +
                "\t}\n" +
                "}\n");
        pw.close();
    }

    private void generateParseTreesSourceFiles() throws IOException {
        PrintWriter pw = new PrintWriter(dirName + fileName + "ParseTree" + FILES_EXTENSION);
        pw.printf("package %s;\n\n" +
                "import java.util.Arrays;\n" +
                "import java.util.List;\n\n" +
                "public class %sParseTree {\n\n", pkgName, fileName);
        pw.printf("\tfinal private List<%sParseTree> children;\n\n", fileName);
        pw.printf("\tpublic %sParseTree() {\n" +
                "\t\tthis.children = Arrays.asList();\n" +
                "\t}\n\n", fileName);
        pw.printf("\tpublic %sParseTree(final %sParseTree... children) {\n" +
                "\t\tthis.children = Arrays.asList(children);\n" +
                "\t}\n\n", fileName, fileName);
        pw.printf("\tpublic List<%sParseTree> getChildren() {\n" +
                "\t\treturn children;\n" +
                "\t}\n" +
                "}\n", fileName);
        pw.close();

        for (Map.Entry<String, String> nonTerminal : grammar.getNonTerminalsMap().entrySet()) {
            String description = nonTerminal.getValue();
            pw = new PrintWriter(dirName + description + "_ParseTree" + FILES_EXTENSION);
            pw.printf("package %s;\n\n" +
                    "import java.util.List;\n\n" +
                    "public class %s_ParseTree extends %sParseTree {\n\n", pkgName, description, fileName);

            List<Attribute> attributes = grammar.getAttributes().get(description);
            for (ListIterator<Attribute> iterator = attributes.listIterator(); iterator.hasNext();) {
                Attribute attribute = iterator.next();
                pw.printf("\tprivate %s %s;\n", attribute.getType(), attribute.getName());

                if (!iterator.hasNext()) {
                    pw.printf("\n");
                }
            }

            pw.printf("\tpublic %s_ParseTree(" +
                    "%sParseTree... children) {\n" +
                    "\t\tsuper(children);\n" +
                    "\t}\n", description, fileName);

            for (Attribute attribute : attributes) {
                String variable = attribute.getName();

                pw.printf("\n" +
                        "\tpublic %s get_%s() {\n" +
                        "\t\tif (%s == null) {\n" +
                        "\t\t\tList<%sParseTree> children = getChildren();\n" +
                        "\n", attribute.getType(), variable, variable, fileName, description);

                List<RuleConditions> conditionsList = grammar.getActions().get(nonTerminal.getKey());
                for (ListIterator<RuleConditions> conditionsIterator = conditionsList.listIterator(); conditionsIterator.hasNext();) {
                    RuleConditions ruleConditions = conditionsIterator.next();
                    List<Condition> conditions = ruleConditions.getConditions();
                    int ruleId = ruleConditions.getRuleId();
                    List<String> rightSide = grammar.getRules().get(ruleId).getRightSide();

                    String shift = "\t\t\t";
                    pw.printf(shift + "if ((children.size() == %d)", rightSide.size());
                    for (int id = 0; id < rightSide.size(); id++) {
                        String symbol = rightSide.get(id);
                        String type;

                        if (grammar.isTerminal(symbol)) {
                            type = "Terminal";
                        } else {
                            type = grammar.getNonTerminalsMap().get(symbol) + "_";
                        }
                        type += "ParseTree";

                        pw.printf(" && (children.get(%d) instanceof %s)", id, type);

                        if (type.startsWith("Terminal")) {
                            pw.printf(" && (((TerminalParseTree) children.get(%d)).getToken() instanceof %s)", id, grammar.getTerminalsMap().get(symbol) + "_Token");
                        }
                    }
                    pw.printf(") {\n");

                    if (conditions.size() == 1) {
                        pw.printf(shift + "\t%s = %s;\n", variable, makeActionLine(conditions.get(0).getValue(), variable, rightSide));
                    } else {
                        for (ListIterator<Condition> iterator = ruleConditions.getConditions().listIterator(); iterator.hasNext();) {
                            Condition condition = iterator.next();

                            if (iterator.hasNext()) {
                                pw.printf(shift + "\tif (%s) {\n" +
                                        shift + "\t\t%s = %s;\n" +
                                        shift + "\t} else ", makeActionLine(condition.getCondition(), variable, rightSide), variable, makeActionLine(condition.getValue(), variable, rightSide));
                            } else {
                                pw.printf(" {\n" +
                                        shift + "\t\t%s = %s;\n" +
                                        shift + "\t}\n", variable, makeActionLine(condition.getValue(), variable, rightSide));
                            }
                        }
                    }

                    pw.printf(shift + "}\n");
                    if (conditionsIterator.hasNext()) {
                        pw.printf("\n");
                    }
                }

                pw.printf("\t\t}\n\n" +
                        "\t\treturn %s;\n" +
                        "\t}\n", variable);
            }

            pw.printf("}\n");

            pw.close();
        }

        pw = new PrintWriter(dirName + "TerminalParseTree" + FILES_EXTENSION);
        pw.printf("package %s;\n\n" +
                "public class TerminalParseTree extends %sParseTree {\n\n"+
                "\tfinal private %sToken token;\n\n", pkgName, fileName, fileName);
        pw.printf("\tpublic TerminalParseTree(final %sToken token) {\n" +
                "\t\tsuper();\n" +
                "\t\tthis.token = token;\n" +
                "\t}\n\n" +
                "\tpublic %sToken getToken() {\n" +
                "\t\treturn token;\n" +
                "\t}\n" +
                "}\n", fileName, fileName);
        pw.close();
    }

    private void generateParserSourceFile() throws IOException {
        PrintWriter pw = new PrintWriter(dirName + fileName + "Parser" + FILES_EXTENSION);
        pw.printf("package %s;\n\n" +
                "import java.io.InputStream;\n" +
                "import java.text.ParseException;\n\n" +
                "public class %sParser {\n\n" +
                "\tprivate %sLexicalAnalyzer lexicalAnalyzer;\n\n", pkgName, fileName, fileName);

        for (Map.Entry<String, String> terminal : grammar.getTerminalsMap().entrySet()) {
            if (terminal.getKey().equals(Grammar.EOF)) {
                continue;
            }

            String description = terminal.getValue();

            pw.printf("\tprivate %s_Token read_%s() throws ParseException {\n" +
                    "\t\t%sToken token = lexicalAnalyzer.getCurrentToken();\n\n" +
                    "\t\tif (token instanceof %s_Token) {\n" +
                    "\t\t\tlexicalAnalyzer.nextToken();\n" +
                    "\t\t\treturn (%s_Token) token;\n" +
                    "\t\t} else {\n" +
                    "\t\t\tthrow new ParseException(\"\\\"%s\\\" expected at position\", lexicalAnalyzer.getCurrentPosition() - 1);\n" +
                    "\t\t}\n" +
                    "\t}\n\n", description, description, fileName, description, description, terminal.getKey());
        }

        for (Map.Entry<String, String> nonTerminal : grammar.getNonTerminalsMap().entrySet()) {
            String description = nonTerminal.getValue();

            pw.printf("\tprivate %s_ParseTree parse_%s() throws ParseException {\n" +
                    "", description, description);

            String symbol = nonTerminal.getKey();
            List<Integer> list = grammar.getTable().get(grammar.getNonTerminalIndices().get(symbol));

            if (grammar.isNullable(symbol)) {
                pw.printf("\t\tif (lexicalAnalyzer.isEmpty()) {\n");

                int ruleIndex = list.get(grammar.getTerminalIndices().get(Grammar.EOF));
                makeProductionCode(pw, description, grammar.getRules().get(ruleIndex).getRightSide());
                pw.printf("\t\t}\n\n");
            }

            pw.printf("\t\t%sToken token = lexicalAnalyzer.getCurrentToken();\n\n", fileName);

            boolean first = true;
            for (Map.Entry<String, Integer> terminal : grammar.getTerminalIndices().entrySet()) {
                if (terminal.getKey().equals(Grammar.EOF)) {
                    continue;
                }

                Integer ruleIndex = list.get(terminal.getValue());
                if (ruleIndex != -1) {
                    if (first) {
                        pw.printf("\t\t");
                        first = false;
                    } else {
                        pw.printf(" else ");
                    }

                    pw.printf("if (token instanceof %s_Token) {\n", grammar.getTerminalsMap().get(terminal.getKey()));
                    makeProductionCode(pw, description, grammar.getRules().get(ruleIndex).getRightSide());
                    pw.printf("\t\t}");
                }
            }

            pw.printf(" else {\n" +
                    "\t\t\tthrow new ParseException(\"Unexpected token: \\\"\" + token + \"at position\", lexicalAnalyzer.getCurrentPosition() - 1);\n" +
                    "\t\t}\n" +
                    "\t}\n\n");
        }

        pw.printf("\tpublic %sParseTree parse(InputStream inputStream) throws ParseException {\n" +
                "\t\tlexicalAnalyzer = new %sLexicalAnalyzer(inputStream);\n" +
                "\t\tlexicalAnalyzer.nextToken();\n" +
                "\t\treturn parse_%s();\n" +
                "\t}\n" +
                "}\n", fileName, fileName, grammar.getNonTerminalsMap().get(grammar.getStart()));

        pw.close();
    }

    private void makeProductionCode(PrintWriter pw, String description, List<String> rightSide) {
        String shift = "\t\t\t";
        Map<String, Integer> occurrences = new HashMap<>();
        List<String> variables = new ArrayList<>();

        for (String symbol : rightSide) {
            if (grammar.isTerminal(symbol)) {
                String code = grammar.getTerminalsMap().get(symbol);
                String varName = makeVariableName(occurrences, code);

                pw.printf(shift + "%s_Token %s_Token = read_%s();\n", code, varName, code);
                pw.printf(shift + "TerminalParseTree %s = new TerminalParseTree(%s_Token);\n", varName, varName);
                variables.add(varName);
            } else {
                String code = grammar.getNonTerminalsMap().get(symbol);
                String varName = makeVariableName(occurrences, code);

                pw.printf(shift + "%s_ParseTree %s = parse_%s();\n", code, varName, code);
                variables.add(varName);
            }
        }

        pw.printf(shift + "return new %s_ParseTree(", description);
        for (ListIterator<String> iterator = variables.listIterator(); iterator.hasNext();) {
            pw.printf(iterator.next());

            if (iterator.hasNext()) {
                pw.printf(", ");
            }
        }

        pw.printf(");\n");
    }

    private String makeVariableName(Map<String, Integer> occurrences, String string) {
        if (!occurrences.containsKey(string)) {
            occurrences.put(string, 1);
            return "arg_" + string;
        }

        int current = occurrences.get(string);
        occurrences.put(string, current + 1);
        return "arg_" + string + current;
    }

    private String makeActionLine(String template, String variable, List<String> rightSide) {
        String result = template.trim();

        for (int id = 0; id < rightSide.size(); id++) {
            String description = grammar.getNonTerminalsMap().get(rightSide.get(id));

            if (description != null) {
                result = result.replace(String.format("$%d.%s", id, variable), String.format("((%s_ParseTree) children.get(%d)).get_%s()", description, id, variable));
            } else {
                result = result.replace(String.format("$%d.%s", id, variable), String.format("((%s_Token) ((TerminalParseTree) children.get(%d)).getToken()).get_%s()", grammar.getTerminalsMap().get(rightSide.get(id)), id, variable));
            }
        }

        return result;
    }

    public void generateSourceFiles() throws IOException {
        generateTokenSourceFiles();
        generateLexicalAnalyzerSourceFiles();
        generateParseTreesSourceFiles();
        generateParserSourceFile();
    }
}
