package raf.thesis.query.tree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import raf.thesis.query.dialect.Dialect;

import java.util.List;

/**
 * Expression tree node for storing unary operations.
 * Supported operations: {@link UnaryOpCode}
 */
@Getter
@Setter
@AllArgsConstructor
public class UnaryOp implements Expression{
    private Expression exp;
    private UnaryOpCode code;

    @Override
    public String toSql(Dialect dialect, List<Literal> args) {
        return dialect.generateUnaryOperationExp(this, args);
    }
}
