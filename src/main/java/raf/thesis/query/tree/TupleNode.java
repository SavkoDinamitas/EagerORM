package raf.thesis.query.tree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import raf.thesis.query.dialect.Dialect;

import java.util.List;

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
