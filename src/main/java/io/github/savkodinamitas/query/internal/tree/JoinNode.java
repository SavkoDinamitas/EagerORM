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

package io.github.savkodinamitas.query.internal.tree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import io.github.savkodinamitas.api.Join;
import java.util.List;

/**
 * Select query AST node for storing required data for single {@code JOIN} clause generation.
 */
@Getter
@AllArgsConstructor
public class JoinNode {
    private Join joinType;
    //joining table is new table inside join clause
    private String tableName;
    private String joiningTableAlias;
    private List<String> joiningTablePk;
    //foreign table is table on which new one is joining
    private String foreignTableAlias;
    private List<String> foreignTableFk;
}
