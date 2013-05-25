grammar Grammar;

@header {
    import java.util.*;
}

@members {

    private List<Rule> rules = new ArrayList<>();
    private String start = null;

    private List<List<String>> actions = new ArrayList<>();

    public Grammar getGrammar() {
        return new Grammar(rules.toArray(new Rule[0]), start);
    }

    public List<List<String>> getActions() {
        return actions;
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

file
    :   WS? terminals WS nonTerminals WS start WS rules WS?
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
    :   TerminalId WS description ( WS LEFT_BRACE WS attributes RIGHT_BRACE )? WS
    ;

TerminalId
    :  '\'' ( ~( '\'' | ' ' | '\t' | '\n' ) )* '\''
    ;

description
    :   lowerId | upperId
    ;

attributes
    :   attribute ( COMMA WS attribute )* WS
    ;

attribute
    :   Type WS lowerId
    ;

Type
    :   'boolean'
    |   'int'
    |   'double'
    |   'char'
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
    :   nonTerminalId WS description ( WS LEFT_BRACE WS attributes RIGHT_BRACE )? WS
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
    :   SECTION_START RULES WS LEFT_BRACE WS
    (
        ruleSignature WS
        {
            rules.add($ruleSignature.rule);
        }
        ruleImplementation WS
        {
            actions.add($ruleImplementation.ruleActions);
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
        $rule = new Rule(leftSide, rightSide.toArray(new String[0]));
    }
    ;

rightSideRuleToken
    :   nonTerminalId
    |   TerminalId
    ;

ruleImplementation returns [List<String> ruleActions]
@init {
    $ruleActions = new ArrayList<>();
}
    :   LEFT_BRACE
    ( WS Expression
        {
            if ($Expression.text != null) {
                $ruleActions.add($Expression.text.replaceAll("\"", ""));
            }
        }
    )?
    ( COMMA WS Expression
        {
            $ruleActions.add($Expression.text.replaceAll("\"", ""));
        }
    )*
    WS RIGHT_BRACE
    ;

Expression
    :   '\"' ( ~'\"' )+ '\"'
    ;