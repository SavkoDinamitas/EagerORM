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

import lombok.Getter;
import lombok.Setter;
import io.github.savkodinamitas.api.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Root of select query AST.
 * Select query AST is used to store {@link QueryBuilder} method calls.
 * Contains required information for generating {@code SELECT} and {@code FROM} clause.
 */
@Getter
public class SelectNode {
    private final Class<?> root;
    private final String baseAlias = "%root";
    private final String baseTableName;
    private List<JoinNode> joinNodes = new ArrayList<>();
    @Setter
    private List<Expression> selectFieldNodes = new ArrayList<>();
    @Setter
    private WhereNode whereNode;
    @Setter
    private HavingNode havingNode;
    @Setter
    private GroupByNode groupByNode;
    @Setter
    private List<OrderByNode> orderByNodes;
    private LimitNode limitNode;
    @Setter
    private boolean distinct;

    public SelectNode(Class<?> root, String baseTableName) {
        this.root = root;
        this.baseTableName = baseTableName;
    }

    public void addJoinNode(List<JoinNode> jn){
        joinNodes.addAll(jn);
    }

    public void setLimit(int limit){
        limitNode = limitNode == null ? new LimitNode() : limitNode;
        limitNode.setLimit(limit);
    }

    public void setOffset(int offset){
        limitNode = limitNode == null ? new LimitNode() : limitNode;
        limitNode.setOffset(offset);
    }

    public void addSelectClauseColumn(FieldNode expr){
        selectFieldNodes.add(expr);
    }
}
