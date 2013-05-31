grammar Grammar;

@header {
    import java.util.*;
}

@rulecatch {
    catch(RecognitionException e) {
        throw new GrammarException(e.getMessage(), e.getCause());
   }
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
    private List<List<String>> actions = new ArrayList<>();

    public Grammar getGrammar() throws GrammarException {
        return new Grammar(rules, start, nonTerminalsList, nonTerminalIndices);
    }

    public FullGrammar getFullGrammar() throws GrammarException {
        return new FullGrammar(rules, start, terminalsList, terminalsMap, terminalIndices, nonTerminalsList, nonTerminalsMap, nonTerminalIndices, attributesMap, actions);
    }

    public List<List<String>> getActions() {
        return actions;
    }

    public Map<String, List<Attribute>> getAttributesMap() {
        return attributesMap;
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
        String desc = $description.text;

        terminalsList.add(id);
        terminalsMap.put(id, desc);
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
            value = $Value.text.replaceAll("\\\$", "");
        }
    )?
    {
        $attr = new Attribute(type, name, value != null ? value : "");
    }
    ;

Value
    :   '$' ( ~'$' )+ '$'
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
@init {
    String id = null;
}
    :   nonTerminalId WS description
    {
        id = $nonTerminalId.text;
        String desc = $description.text;

        nonTerminalsList.add(id);
        nonTerminalsMap.put(id, desc);
        nonTerminalIndices.put(id, nonTerminalIndices.size());
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
        $rule = new Rule(leftSide, rightSide);
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
            $ruleActions.add($Expression.text.replaceAll("\"", ""));
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