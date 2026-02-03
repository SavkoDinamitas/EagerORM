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

package raf.thesis.query.internal.tree;

import raf.thesis.query.dialect.Dialect;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Expression tree node for storing literals.
 * Supports all literal types in ANSI SQL dialects.
 */
public sealed interface Literal extends Expression{
    public record DoubleCnst(double x) implements Literal {
        @Override
        public String toSql(Dialect dialect, List<Literal> args) {
            return dialect.generateLiteralExp(this, args);
        }
    }
    public record LongCnst(long x) implements Literal {
        @Override
        public String toSql(Dialect dialect, List<Literal> args) {
            return dialect.generateLiteralExp(this, args);
        }
    }
    public record StringCnst(String x) implements Literal {
        @Override
        public String toSql(Dialect dialect, List<Literal> args) {
            return dialect.generateLiteralExp(this, args);
        }
    }
    public record BoolCnst(boolean x) implements Literal {
        @Override
        public String toSql(Dialect dialect, List<Literal> args) {
            return dialect.generateLiteralExp(this, args);
        }
    }
    public record DateCnst(LocalDate x) implements Literal {
        @Override
        public String toSql(Dialect dialect, List<Literal> args) {
            return dialect.generateLiteralExp(this, args);
        }
    }
    public record DateTimeCnst(LocalDateTime x) implements Literal {
        @Override
        public String toSql(Dialect dialect, List<Literal> args) {
            return dialect.generateLiteralExp(this, args);
        }
    }
    public record TimeCnst(LocalTime x) implements Literal {
        @Override
        public String toSql(Dialect dialect, List<Literal> args) {
            return dialect.generateLiteralExp(this, args);
        }
    }
    public record NullCnst() implements Literal {
        @Override
        public String toSql(Dialect dialect, List<Literal> args) {
            return dialect.generateLiteralExp(this, args);
        }
    }
}
