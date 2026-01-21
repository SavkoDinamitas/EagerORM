package raf.thesis.query.tree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import raf.thesis.query.dialect.Dialect;

import java.util.List;

@Getter@AllArgsConstructor
public class AliasedColumn implements Expression{
    private Expression expression;
    private String colAlias;
    @Override
    public String toSql(Dialect dialect, List<Literal> args) {
        return dialect.generateAliasedFieldExp(this, args);
    }
}
