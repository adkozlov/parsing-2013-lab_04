#!/bin/bash

rm -f src/Grammar?*.*
java -jar lib/antlr-4.0-complete.jar src/Grammar.g4
