package raf.thesis.query.dialect;

import raf.thesis.query.tree.LimitNode;
import raf.thesis.query.tree.Literal;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MariaDBDialect extends ANSISQLDialect implements Dialect.UsesInsertReturning{
    @Override
    protected String quote(String value){
        return "`" + value.replaceAll("`", "``") + "`";
    }

    @Override
    public String generateLimitClause(LimitNode limitNode, List<Literal> args){
        return "%s %s".formatted(generateLimit(limitNode.getLimit(), args), generateOffset(limitNode.getOffset(), args));
    }

    @Override
    protected String generateLimit(Integer limit, List<Literal> args){
        if(limit == null){
            return "LIMIT 18446744073709551615";
        }
        else{
            registerLiteral(new Literal.LongCnst(limit), args);
            return "LIMIT ?";
        }
    }

    @Override
    protected String generateOffset(Integer offset, List<Literal> args){
        if(offset == null){
            return "";
        }
        else{
            registerLiteral(new Literal.LongCnst(offset), args);
            return "OFFSET ?";
        }
    }

    protected String insertHelper(List<String> columns, String tableName) {
        String s = generateInsertQuery(columns, tableName);
        return s.substring(0, s.length()-1);
    }

    public String generateReturningClause(List<String> keys){
        return keys.stream().collect(Collectors.joining(", ", " RETURNING ", ""));
    }

    @Override
    public String generateInsertQuery(List<String> columns, String tableName, List<String> returningKeys) {
        return insertHelper(columns, tableName) + generateReturningClause(returningKeys);
    }
}
