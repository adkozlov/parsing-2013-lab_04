grammar Grammar;

@header {
    import java.util.*;
}

@members {

    private List<Rule> rules = new ArrayList<>();
    private String start = null;

    private List<String> terminalsList = new ArrayList<>();
    private Map<String, String> terminalsMap = new HashMap<>();
    private Map<String, Integer> terminalIndices = new HashMap<>();

    private List<String> nonTerminalsList = new ArrayList<>();
    private Map<String, String> nonTerminalsMap = new HashMap<>();
    private Map<String, Integer> nonTerminalIndices = new HashMap<>();

    private Map<String, List<Attribute>> attributesMap = new HashMap();
    private Map<String, List<RuleConditions>> actions = new HashMap<>();

    public Grammar getGrammar() throws GrammarException {
        return new Grammar(rules, start, nonTerminalsList, nonTerminalIndices);
    }

    public FullGrammar getFullGrammar() throws GrammarException {
        return new FullGrammar(rules, start, terminalsList, terminalsMap, terminalIndices, nonTerminalsList, nonTerminalsMap, nonTerminalIndices, attributesMap, actions);
    }
}

WS
    :   (
            ' '
        |   '\t'
        |   '\n'
        )+
    ;

LEFT_BRACE
    :   '{'
    ;

RIGHT_BRACE
    :   '}'
    ;

SECTION_START
    :   '@'
    ;

LowerCase
    :   [a-z]
    ;

UpperCase
    :   [A-Z]
    ;

letter
    :   LowerCase
    |   UpperCase
    ;

Digit
    :   [0-9]
    ;

POINT
    :   '.'
    ;

COMMA
    :   ','
    ;

APOSTROPHE
    :   '\''
    ;

UNDERLINE
    :   '_'
    ;

ARROW
    :   '->'
    ;

EQUALS
    :   '='
    ;

IF
    :   'if'
    ;

ELSE
    :   'else'
    ;

file
    :   WS? terminals WS nonTerminals WS start WS rules WS? EOF
    ;

lowerId
    :   LowerCase idSuffix
    ;

upperId
    :   UpperCase idSuffix
    ;

idSuffix
    :   ( letter | Digit | UNDERLINE )*
    ;

TERMINALS
    :   'terminals'
    ;

terminals
    :   SECTION_START TERMINALS WS LEFT_BRACE WS terminal+ RIGHT_BRACE
    ;

terminal
@init {
    String id = null;
}
    :   TerminalId WS description
    {
        id = $TerminalId.text.replaceAll("\'", "");
        String description = $description.text;

        terminalsList.add(id);
        terminalsMap.put(id, description);
        terminalIndices.put(id, terminalIndices.size());
    }
    ( WS LEFT_BRACE WS attributes RIGHT_BRACE
        {
            attributesMap.put(terminalsMap.get(id), $attributes.attrs);
        }
    )? WS
    ;

TerminalId
    :  '\'' ( ~( '\'' | ' ' | '\t' | '\n' ) )* '\''
    ;

description
    :   lowerId | upperId
    ;

attributes returns [List<Attribute> attrs]
@init {
    $attrs = new ArrayList<>();
}
    :   attribute
    {
        $attrs.add($attribute.attr);
    }
    ( COMMA WS attribute
        {
            $attrs.add($attribute.attr);
        }
    )* WS
    ;

attribute returns [Attribute attr]
@init {
    String type = null;
    String name = null;
    String value = null;
}
    :   Type WS lowerId
    {
        type = $Type.text;
        name = $lowerId.text;
    }
    ( WS EQUALS WS Value
        {
            value = $Value.text.replaceAll("#", "");
        }
    )?
    {
        $attr = new Attribute(type, name, value != null ? value : "");
    }
    ;

Value
    :   '#' ( ~'#' )+ '#'
    ;

Type
    :   'Boolean'
    |   'Integer'
    |   'Double'
    |   'Character'
    ;

NON_TERMINALS
    :   'nonTerminals'
    ;

nonTerminals
    :   SECTION_START NON_TERMINALS WS LEFT_BRACE WS nonTerminal+ RIGHT_BRACE
    ;

nonTerminalId
    :   upperId APOSTROPHE*
    ;

nonTerminal
@init {
    String id = null;
}
    :   nonTerminalId WS description
    {
        id = $nonTerminalId.text;
        String description = $description.text;

        nonTerminalsList.add(id);
        nonTerminalsMap.put(id, description);
        nonTerminalIndices.put(id, nonTerminalIndices.size());

        actions.put(id, new ArrayList<RuleConditions>());
    }
    ( WS LEFT_BRACE WS attributes RIGHT_BRACE
        {
            attributesMap.put(nonTerminalsMap.get(id), $attributes.attrs);
        }
    )? WS
    ;

START
    :   'start'
    ;

start
    :   SECTION_START START WS EQUALS WS nonTerminalId
    {
        start = $nonTerminalId.text;
    }
    ;

RULES
    :   'rules'
    ;

rules
@init {
    String nonTerminal = null;
}
    :   SECTION_START RULES WS LEFT_BRACE WS
    (
        ruleSignature WS
        {
            rules.add($ruleSignature.rule);
            nonTerminal = $ruleSignature.rule.getLeftSide();
        }
        ruleImplementation WS
        {
            List<RuleConditions> conditionsLists = actions.get(nonTerminal);
            conditionsLists.add(new RuleConditions($ruleImplementation.conditions, rules.size() - 1));
            actions.put(nonTerminal, conditionsLists);
        }
    )+ RIGHT_BRACE
    ;

ruleSignature returns [Rule rule]
@init {
    String leftSide = null;
    List<String> rightSide = new ArrayList<>();
}
    :   nonTerminalId WS ARROW
    {
        leftSide = $nonTerminalId.text;
    }
    ( WS rightSideRuleToken
        {
            if ($rightSideRuleToken.text.startsWith("\'")) {
                rightSide.add($rightSideRuleToken.text.replaceAll("\'", ""));
            } else {
                rightSide.add($rightSideRuleToken.text);
            }
        }
    )*
    {
        $rule = new Rule(leftSide, rightSide);
    }
    ;

rightSideRuleToken
    :   nonTerminalId
    |   TerminalId
    ;

ruleImplementation returns [List<Condition> conditions]
@init {
    $conditions = new ArrayList<>();
}
    :   LEFT_BRACE
    ( WS ( Value
        {
            $conditions.add(new Condition("", $Value.text.replaceAll("#", "")));
        }
        |   conditionalExpression
        {
            $conditions.addAll($conditionalExpression.conditions);
        }
        )
    )?
    ( COMMA WS ( Value |
        {
            $conditions.add(new Condition("", $Value.text.replaceAll("#", "")));
        }
        conditionalExpression
        {
            $conditions.addAll($conditionalExpression.conditions);
        }
        )
    )*
    WS RIGHT_BRACE
    ;

conditionalExpression returns [List<Condition> conditions]
@init {
    $conditions = new ArrayList<>();
}
    :   IF WS Statement WS Value WS
    {
        $conditions.add(new Condition($Statement.text.replaceAll("\"", ""), $Value.text.replaceAll("#", "")));
    }
    ELSE WS ( Value
        {
            $conditions.add(new Condition("", $Value.text.replaceAll("#", "")));
        }
        |   conditionalExpression
        {
            $conditions.addAll($conditionalExpression.conditions);
        }
    )
    ;

Statement
    :   '"' ( ~'"' )+ '"'
    ;