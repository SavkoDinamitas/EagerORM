package raf.thesis.query.internal.tree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import raf.thesis.query.dialect.Dialect;

import java.util.List;

/**
 * Expression tree node for storing columns with specific user aliases.
 * Used for {@link raf.thesis.metadata.annotations.PDO} queries.
 */
@Getter@AllArgsConstructor
public class AliasedColumn implements Expression{
    private Expression expression;
    private String colAlias;
    @Override
    public String toSql(Dialect dialect, List<Literal> args) {
        return dialect.generateAliasedFieldExp(this, args);
    }
}
