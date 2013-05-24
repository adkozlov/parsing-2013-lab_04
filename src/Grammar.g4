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

Letter
    :   LowerCase
    |   UpperCase
    ;

Digit
    :   [0-9]
    ;

DOT
    :   '.'
    ;

APOSTROPHE
    :   '\''
    ;

UNDERLINE
    :   '_'
    ;

file
    :   WS? section+ WS?
    ;

section
    :   SECTION_START SectionName WS LEFT_BRACE WS ( header | nonTerminals | terminals | rules ) WS RIGHT_BRACE WS
    ;

SectionName
    :   'header'
    |   'nonTerminals'
    |   'terminals'
    |   'rules'
    ;

header
    :   ( package WS )? name WS start
    ;

lowerId
    :   lowerCase idSuffix
    ;

upperId
    :   upperCase idSuffix
    ;

idSuffix
    :   ( Letter | Digit | UNDERLINE )*
    ;

package
    :   lowerId ( DOT lowerId )*
    ;

name
    :  upperId
    ;

start
    :  nonTerminalName
    ;

nonTerminals
    :   ( nonTerminal WS )+
    ;

terminals
    :   ( terminal WS )+
    ;

rules
    :   ( rule WS )+
    ;