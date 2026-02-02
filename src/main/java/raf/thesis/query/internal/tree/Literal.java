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
