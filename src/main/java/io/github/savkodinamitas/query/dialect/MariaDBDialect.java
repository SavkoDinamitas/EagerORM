/*
 * EagerORM - A predictable object-relation mapper
 * Copyright (C) 2026  Dimitrije Andžić <dimitrije.andzic@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.savkodinamitas.query.dialect;

import io.github.savkodinamitas.query.internal.tree.LimitNode;
import io.github.savkodinamitas.query.internal.tree.Literal;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SQL dialect implementation for MariaDB and MySQL-compatible databases.
 */
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

    @Override
    public String generateUpsertQuery(List<String> columnNames, String tableName, List<String> keyColumnNames){
        return """
                INSERT INTO %s (%s)
                VALUES (%s)
                ON DUPLICATE KEY UPDATE
                %s;""".formatted(tableName, generateInsertColumnParenthesis(columnNames), generateQuestionMarks(columnNames.size()), generateUpsertMatchedClause(columnNames));
    }

    @Override
    protected String generateUpsertMatchedClause(List<String> columnNames){
        StringBuilder result = new StringBuilder();
        for(var column : columnNames){
            result.append(column);
            result.append(" = VALUES (");
            result.append(column).append("),\n");
        }
        result.deleteCharAt(result.length()-1);
        result.deleteCharAt(result.length()-1);
        return result.toString();
    }
}
