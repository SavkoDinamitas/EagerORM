/*
 * EagerORM - A predictable object-relation mapper
 * Copyright (C) 2026  Dimitrije Andžić <dimitrije.andzic@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.savkodinamitas.query.dialect;

import io.github.savkodinamitas.query.internal.tree.JoinNode;
import io.github.savkodinamitas.query.internal.tree.Literal;

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
