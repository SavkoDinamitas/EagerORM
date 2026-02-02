package raf.thesis.query.internal.tree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raf.thesis.query.dialect.Dialect;

import java.util.List;

/**
 * Expression tree node for storing binary operations.
 * Supported operations: {@link BinaryOpCode}
 */
@AllArgsConstructor
@Getter @Setter
@NoArgsConstructor
public class BinaryOp implements Expression{
    private Expression left, right;
    private BinaryOpCode code;

    @Override
    public String toSql(Dialect dialect, List<Literal> args) {
        return dialect.generateBinaryOperationExp(this, args);
    }
}
