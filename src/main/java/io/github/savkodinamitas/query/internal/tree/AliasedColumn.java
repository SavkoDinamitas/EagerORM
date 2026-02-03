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

package io.github.savkodinamitas.query.internal.tree;

import io.github.savkodinamitas.metadata.annotations.PDO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import io.github.savkodinamitas.query.dialect.Dialect;

import java.util.List;

/**
 * Expression tree node for storing columns with specific user aliases.
 * Used for {@link PDO} queries.
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
