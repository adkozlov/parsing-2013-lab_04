@terminals {
    '|' OR_OPERATOR
    '&' AND_OPERATOR
    '!' NOT_OPERATOR
    '(' LEFT_PARENTHESIS
    ')' RIGHT_PARENTHESIS
    'true' TRUE { Boolean value = #true# }
    'false' FALSE { Boolean value = #false# }
}

@nonTerminals {
    E Expression { Boolean value }
    E' ExpressionContinuation { Boolean value }
    T Term { Boolean value }
    T' TermContinuation { Boolean value }
    X MaybeNegation { Boolean value }
    Y VariableOrExpression { Boolean value }
}

@start = E

@rules {
    E -> T E' {
        if "$0.value == true"
            #true#
        else
            #$0.value || $1.value#
    }

    E' -> '|' T E' {
        #$1.value || $2.value#
    }

    E' -> {
        #false#
    }

    T -> X T' {
        if "$0.value == false"
            #false#
        else
            #$0.value && $1.value#
    }

    T' -> '&' X T' {
        #$1.value && $2.value#
    }

    T' -> {
        #true#
    }

    X -> '!' X {
        #!$1.value#
    }

    X -> Y {
        #$0.value#
    }

    Y -> 'true' {
        #$0.value#
    }

    Y -> 'false' {
        #$0.value#
    }

    Y -> '(' E ')' {
        #$1.value#
    }
}