@terminals {
    '|' OR_OPERATOR
    '&' AND_OPERATOR
    '!' NOT_OPERATOR
    '(' LEFT_PARENTHESIS
    ')' RIGHT_PARENTHESIS
    'true' TRUE { boolean value = $true$ }
    'false' FALSE { boolean value = $false$ }
}

@nonTerminals {
    E Expression { boolean value }
    E' ExpressionContinuation { boolean value }
    T Term { boolean value }
    T' TermContinuation { boolean value }
    X MaybeNegation { boolean value }
    Y VariableOrExpression { boolean value }
}

@start = E

@rules {
    E -> T E' {
        "$0.value || $1.value"
    }

    E' -> '|' T E' {
        "$1.value || $2.value"
    }

    E' -> {
        "false"
    }

    T -> X T' {
        "$0.value && $1.value"
    }

    T' -> '&' X T' {
        "$1.value && $2.value"
    }

    T' -> {
        "true"
    }

    X -> '!' X {
        "!$1.value"
    }

    X -> Y {
        "$0.value"
    }

    Y -> 'true' {
        "$0.value"
    }

    Y -> 'false' {
        "$0.value"
    }

    Y -> '(' E ')' {
        "$1.value"
    }
}