package raf.thesis.query.internal.tree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import raf.thesis.query.dialect.Dialect;

import java.util.List;

/**
 * Expression tree node for storing tuples of expressions.
 */
@Getter
@Setter
@AllArgsConstructor
public class TupleNode implements Expression{
    List<Expression> operands;

    @Override
    public String toSql(Dialect dialect, List<Literal> args) {
        return dialect.generateTupleExp(this, args);
    }
}
