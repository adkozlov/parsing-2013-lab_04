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
    :   header terminals
    ;

header
    :   SECTION_START 'header' WS LEFT_BRACE WS ( packageName WS )? className WS startNonTerminal WS RIGHT_BRACE WS
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
    :   nonTerminalId
    ;

nonTerminalId
    :   upperId APOSTROPHE*
    ;

terminals
    :   SECTION_START 'terminals' WS LEFT_BRACE WS terminal+ RIGHT_BRACE WS
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
    :   attribute
    {
        System.out.println($attribute.text);
    }
        ( COMMA WS attribute )* WS
    ;

attribute
    :   Type WS lowerId
    ;

Type
    :   'boolean'
    ;