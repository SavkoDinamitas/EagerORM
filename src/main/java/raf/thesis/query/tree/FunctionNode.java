package raf.thesis.query.tree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import raf.thesis.query.dialect.Dialect;

import java.util.List;

@AllArgsConstructor
@Setter
@Getter
public class FunctionNode implements Expression{
    private Expression exp;
    private FunctionCode code;
    private boolean distinct;

    @Override
    public String toSql(Dialect dialect, List<Literal> args) {
        return dialect.generateFunctionExp(this, args);
    }
}
