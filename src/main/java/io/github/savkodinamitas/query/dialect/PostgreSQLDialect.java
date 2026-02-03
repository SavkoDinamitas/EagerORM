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

import java.util.List;

/**
 * SQL dialect implementation for PostgreSQL and PSQL-compatible databases.
 */
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
