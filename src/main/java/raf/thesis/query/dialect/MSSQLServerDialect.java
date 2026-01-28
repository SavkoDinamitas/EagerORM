package raf.thesis.query.dialect;

import raf.thesis.query.tree.JoinNode;
import raf.thesis.query.tree.Literal;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SQL dialect implementation for Microsoft SQL Server and MSSQL-compatible databases.
 */
public class MSSQLServerDialect extends ANSISQLDialect implements Dialect.UsesInsertReturning{

    @Override
    protected String generateOnJoinClause(JoinNode joinNode){
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < joinNode.getJoiningTablePk().size(); i++){
            result.append(quote(joinNode.getJoiningTableAlias())).append(".").append(joinNode.getJoiningTablePk().get(i));
            result.append(" = ");
            result.append(quote(joinNode.getForeignTableAlias())).append(".").append(joinNode.getForeignTableFk().get(i));
            if(i != joinNode.getJoiningTablePk().size()-1)
                result.append(" AND ");
        }
        return result.toString();
    }

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

    @Override
    public String generateUpsertQuery(List<String> columnNames, String tableName, List<String> keyColumnNames) {
        return """
                MERGE INTO %s t
                USING (VALUES(%s)) n(%s)
                ON(%s)
                WHEN MATCHED THEN
                    UPDATE SET
                     %s
                WHEN NOT MATCHED THEN
                    INSERT(%s)
                    VALUES(%s);
                """.formatted(tableName, generateQuestionMarks(columnNames.size()), generateInsertColumnParenthesis(columnNames), generateUpsertOnClause(keyColumnNames),
                generateUpsertMatchedClause(columnNames), generateInsertColumnParenthesis(columnNames), generateUpsertValuesClause(columnNames));
    }

    @Override
    protected String generateUpsertOnClause(List<String> keyColumnNames){
        return keyColumnNames.stream().map(x -> "t." + x + " = " + "n." + x).collect(Collectors.joining(" AND "));
    }
}
