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
    :   WS? header WS terminals WS nonTerminals WS rules WS?
    ;

header
    :   SECTION_START 'header' WS LEFT_BRACE WS ( 'package=' packageName WS )? 'class=' className WS 'start='startNonTerminal WS RIGHT_BRACE
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
    :   SECTION_START 'terminals' WS LEFT_BRACE WS terminal+ RIGHT_BRACE
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

nonTerminals
    :   SECTION_START 'nonTerminals' WS LEFT_BRACE WS nonTerminal+ RIGHT_BRACE
    ;

nonTerminal
    :   nonTerminalId WS description ( WS LEFT_BRACE WS attributes RIGHT_BRACE )? WS
    ;

rules
    :   SECTION_START 'rules' WS LEFT_BRACE WS ( ruleSignature ( WS ruleImplementation )? WS )+ RIGHT_BRACE
    ;

ruleSignature
    :   nonTerminalId WS '->' ( WS ( nonTerminalId | TerminalId ) )*
    ;

ruleImplementation
    :   LEFT_BRACE WS Expression ( COMMA WS Expression )* WS RIGHT_BRACE
    ;

Expression
    :   '\"' ( ~'\"' )+ '\"'
    ;