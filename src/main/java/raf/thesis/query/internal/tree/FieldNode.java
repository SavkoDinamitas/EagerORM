package raf.thesis.query.internal.tree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import raf.thesis.query.dialect.Dialect;

import java.util.List;

/**
 * Expression tree node for storing table columns with EagerORM specific aliases
 */
@AllArgsConstructor
@Getter
public class FieldNode implements Expression{
    private String fieldName;
    private String tableAlias;

    @Override
    public String toSql(Dialect dialect, List<Literal> args) {
        return dialect.generateFieldExp(this);
    }
}
