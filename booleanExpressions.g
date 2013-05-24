@header {
    BooleanExpressions
    E
}

@terminals {
    | OR_OPERATOR
    ^ XOR_OPERATOR
    & AND_OPERATOR
    ! NOT_OPERATOR
    ( LEFT_PARENTHESIS
    ) RIGHT_PARENTHESIS
    v VARIABLE { boolean value }
}

@nonTerminals {
    E Disjunct { boolean value }
    E' DisjunctContinuation { boolean value }
    A Conjunct { boolean value }
    A' ConjunctContinuation { boolean value }
    B NegateMaybe { boolean value }
    C VariableOrExpression { boolean value }
}

@rules {
    E -> A E' {
        $0.value || $1.value
    }

    E' -> | A E' {
        $1.value || $2.value
    }

    E' -> {
        false
    }

    A -> B A' {
        $0.value || $1.value
    }

    A' -> | B A' {
        $1.value || $2.value
    }

    A' -> {
        true
    }

    B -> C {
        $0.value
    }

    B -> !B {
        !$1.value
    }

    C -> v {
        $0.value
    }

    C -> ( E ) {
        $1.value
    }
}