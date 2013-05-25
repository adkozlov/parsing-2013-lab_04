@header {
    BooleanExpressions
    E
}

@terminals {
    | OR_OPERATOR
    & AND_OPERATOR
    ! NOT_OPERATOR
    ( LEFT_PARENTHESIS
    ) RIGHT_PARENTHESIS
    v VARIABLE { boolean value }
}

@nonTerminals {
    E Expression { boolean value }
    E' ExpressionContinuation { boolean value }
    T Term { boolean value }
    T' TermContinuation { boolean value }
    X VariableOrExpression { boolean value }
}

@rules {
    E -> T E' {
        $0.value || $1.value
    }

    E' -> | T E' {
        $1.value || $2.value
    }

    E' -> {
        false
    }

    T -> B T' {
        $0.value || $1.value
    }

    T' -> | B T' {
        $1.value || $2.value
    }

    T' -> {
        true
    }

    X -> !X {
        !$1.value
    }

    X -> v {
        $0.value
    }

    X -> ( E ) {
        $1.value
    }
}