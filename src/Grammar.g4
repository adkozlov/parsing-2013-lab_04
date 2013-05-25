grammar Grammar;

@header {

}

@members {

}

WS
    :   (
            ' '
        |   '\t'
        |   '\n'
        )+
        {skip();}
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

file
    :   WS? sections WS?
    ;

sections
    :   header
    ;

header
    :   SECTION_START 'header' WS LEFT_BRACE WS ( packageName WS )? className WS startNonTerminal WS RIGHT_BRACE
    {
        System.out.println($className.text);
        System.out.println($startNonTerminal.text);
    }
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

packageName
    :   lowerId ( POINT lowerId )*
    ;

className
    :  upperId
    ;

startNonTerminal
    :  nonTerminalName
    ;

description
    :   lowerId
    |   upperId
    ;

nonTerminals
    :   SECTION_START 'nonTerminals' WS LEFT_BRACE WS ( nonTerminal WS )+ RIGHT_BRACE
    ;

nonTerminalName
    :   upperId APOSTROPHE*
    ;

nonTerminal
    :   nonTerminalName WS description ( WS attributes )?
    ;

attributes
    :   LEFT_BRACE WS attribute ( COMMA WS attribute )* WS RIGHT_BRACE
    ;

attribute
    :   type description
    ;

type
    :   'boolean'
    ;

terminals
    :   {
            System.out.println("terminals");
        }
        SECTION_START 'terminals' WS LEFT_BRACE WS ( terminal WS )+ RIGHT_BRACE
    ;

terminal
    :   terminalName WS description ( WS attributes )?
        {
            System.out.println("1" + $terminalName.text);
            System.out.println("2" + $description.text);
            System.out.println("3" + $attributes.text);
        }
    ;

terminalName
    :   ( ~( ' ' |' \t' | '\n' ) )+
    ;

rules
    :   SECTION_START 'rules' WS LEFT_BRACE WS ( implementation WS )+ RIGHT_BRACE
    ;

implementation
    :   nonTerminalName WS ARROW WS ( ( nonTerminalName | terminalName ) WS? ) ( WS definition )?
    ;

definition
    :   LEFT_BRACE WS expressions RIGHT_BRACE
    ;

expressions
    :   ( expression WS )*
    ;

expression
    :   ( ~WS )*
    ;