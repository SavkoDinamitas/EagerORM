package raf.thesis.query.dialect;

import java.util.List;

public class PostgreSQLDialect extends ANSISQLDialect{
    @Override
    public String generateUpsertQuery(List<String> columnNames, String tableName, List<String> keyColumnNames){
        return """
                INSERT INTO %s (%s)
                VALUES (%s)
                ON CONFLICT (%s) DO UPDATE
                SET
                %s;""".formatted(tableName, generateInsertColumnParenthesis(columnNames) , generateQuestionMarks(columnNames.size()), generateInsertColumnParenthesis(keyColumnNames), generateUpsertMatchedClause(columnNames));
    }

    @Override
    protected String generateUpsertMatchedClause(List<String> columnNames){
        StringBuilder result = new StringBuilder();
        for(var column : columnNames){
            result.append(column);
            result.append(" = EXCLUDED.");
            result.append(column).append(",\n");
        }
        result.deleteCharAt(result.length()-1);
        result.deleteCharAt(result.length()-1);
        return result.toString();
    }
}
