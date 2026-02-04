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

import io.github.savkodinamitas.query.internal.ToSql;

/**
 * Represents a SQL expression that can be composed into larger query conditions
 * and rendered into SQL.
 */
public interface Expression extends ToSql {
    /**
     * Creates a {@code >} (greater-than) condition with the given expression.
     *
     * @param expr the right-hand side expression
     * @return a new comparison expression
     */
    default Expression gt(Expression expr) {
        return new BinaryOp(this, expr, BinaryOpCode.GT);
    }

    /**
     * Creates a {@code <} (less-than) condition with the given expression.
     *
     * @param expr the right-hand side expression
     * @return a new comparison expression
     */
    default Expression lt(Expression expr) {
        return new BinaryOp(this, expr, BinaryOpCode.LT);
    }

    /**
     * Creates an {@code =} (equality) condition with the given expression.
     *
     * @param expr the right-hand side expression
     * @return a new comparison expression
     */
    default Expression eq(Expression expr) {
        return new BinaryOp(this, expr, BinaryOpCode.EQ);
    }

    /**
     * Creates a {@code LIKE} pattern-matching condition using the given pattern.
     *
     * @param pattern the SQL LIKE pattern
     * @return a new pattern-matching expression
     */
    default Expression like(String pattern) {
        return new BinaryOp(this, new Literal.StringCnst(pattern), BinaryOpCode.LIKE);
    }

    /**
     * Creates an {@code IN} membership condition with the given expression.
     *
     * @param expr the right-hand side expression (e.g. a subquery or tuple expression)
     * @return a new membership expression
     */
    default Expression in(Expression expr) {
        return new BinaryOp(this, expr, BinaryOpCode.IN);
    }

    /**
     * Creates an {@code IS NULL} null-check condition for this expression.
     *
     * @return a new unary expression
     */
    default Expression isNull(){
        return new UnaryOp(this, UnaryOpCode.IS_NULL);
    }
}
