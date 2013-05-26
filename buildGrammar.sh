#!/bin/bash

cd src
rm -f Grammar.tokens GrammarBaseListener.java GrammarLexer.java GrammarLexer.tokens GrammarListener.java GrammarParser.java
java -jar ../lib/antlr-4.0-complete.jar Grammar.g4
cd ..
rm booleanExpressions/*.java