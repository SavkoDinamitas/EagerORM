package raf.thesis.query.dialect;

import raf.thesis.query.tree.Literal;

import java.util.List;
import java.util.stream.Collectors;

public class MSSQLServerDialect extends ANSISQLDialect implements Dialect.UsesInsertReturning{

    private String generateOutputClause(List<String> keys){
        return keys.stream().map("INSERTED."::concat).collect(Collectors.joining(", ", "\nOUTPUT ", "\n"));
    }

    @Override
    protected String generateOffset(Integer offset, List<Literal> args){
        if(offset == null){
            return "OFFSET 0 ROWS";
        }
        else{
            registerLiteral(new Literal.LongCnst(offset), args);
            return "OFFSET ? ROWS";
        }
    }

    @Override
    public String generateInsertQuery(List<String> columns, String tableName, List<String> returningKeys) {
        return "INSERT INTO %s (%s)%sVALUES (%s);".formatted(tableName, generateInsertColumnParenthesis(columns), generateOutputClause(returningKeys), generateQuestionMarks(columns.size()));
    }
}
