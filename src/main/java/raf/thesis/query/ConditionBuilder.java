package raf.thesis.query;

import lombok.NoArgsConstructor;
import raf.thesis.query.tree.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.Stream;

/**
 * Static factory methods for building SQL expression trees and conditions
 * used across different SQL clauses.
 */
@SuppressWarnings("ClassEscapesDefinedScope")
@NoArgsConstructor
public class ConditionBuilder {
    /**
     * Creates a field reference expression from a dot-separated path.
     * @param fieldPath dot separated relation path to field
     * @return a field expression
     */
    public static FieldNode field(String fieldPath) {
        String[] path = fieldPath.split("\\.");
        String fieldName = path[path.length - 1];
        int index = fieldPath.lastIndexOf(".");
        String alias = index == -1 ? "%root" : fieldPath.substring(0, index);
        return new FieldNode(fieldName, alias);
    }

    /**
     * Creates an aliased column expression.
     * Used for {@link raf.thesis.metadata.annotations.PDO} queries
     *
     * @param expression the column expression
     * @param alias the column alias
     * @return an aliased column expression
     */
    public static AliasedColumn aliasedColumn(Expression expression, String alias){
        return new AliasedColumn(expression, alias);
    }

    /** Creates a string literal expression. */
    public static Literal lit(String value){
        return new Literal.StringCnst(value);
    }

    /** Creates a whole number literal expression. */
    public static Literal lit(long value){
        return new Literal.LongCnst(value);
    }

    /** Creates a real number literal expression. */
    public static Literal lit(double value){
        return new Literal.DoubleCnst(value);
    }

    /** Creates a boolean literal expression. */
    public static Literal lit(boolean value){
        return new Literal.BoolCnst(value);
    }

    /** Creates a date literal expression. */
    public static Literal lit(LocalDate date){
        return new Literal.DateCnst(date);
    }

    /** Creates a time literal expression. */
    public static Literal lit(LocalTime time){
        return new Literal.TimeCnst(time);
    }

    /** Creates a timestamp literal expression. */
    public static Literal lit(LocalDateTime timestamp){
        return new Literal.DateTimeCnst(timestamp);
    }

    /**
     * Creates a tuple expression from the given elements.
     *
     * @param e1 the first element
     * @param e2 the second element
     * @param expressions remaining elements
     * @return a tuple expression node
     */
    public static TupleNode tuple(Expression e1, Expression e2, Expression... expressions){
        return new TupleNode(Stream.concat(Stream.of(e1, e2), Stream.of(expressions)).toList());
    }

    /**
     * Creates an {@code ORDER BY ... ASC} expression.
     *
     * @param expression the ordering key
     * @return an ascending order-by node
     */
    public static OrderByNode asc(Expression expression){
        return new OrderByNode(expression, Ordering.ASC);
    }

    /**
     * Creates an {@code ORDER BY ... DESC} expression.
     *
     * @param expression the ordering key
     * @return a descending order-by node
     */
    public static OrderByNode desc(Expression expression){
        return new OrderByNode(expression, Ordering.DESC);
    }

    /** Creates an {@code AVG} aggregate function expression with {@code DISTINCT} keyword. */
    public static FunctionNode avg(Expression expression, Distinct distinct){
        return new FunctionNode(expression, FunctionCode.AVG, true);
    }
    /** Creates an {@code AVG} aggregate function expression. */
    public static FunctionNode avg(Expression expression){
        return new FunctionNode(expression, FunctionCode.AVG, false);
    }

    /** Creates a {@code SUM} aggregate function expression with {@code DISTINCT} keyword. */
    public static FunctionNode sum(Expression expression, Distinct distinct){
        return new FunctionNode(expression, FunctionCode.SUM, true);
    }
    /** Creates a {@code SUM} aggregate function expression. */
    public static FunctionNode sum(Expression expression){
        return new FunctionNode(expression, FunctionCode.SUM, false);
    }

    /** Creates a {@code MAX} aggregate function expression with {@code DISTINCT} keyword. */
    public static FunctionNode max(Expression expression, Distinct distinct){
        return new FunctionNode(expression, FunctionCode.MAX, true);
    }
    /** Creates a {@code MAX} aggregate function expression. */
    public static FunctionNode max(Expression expression){
        return new FunctionNode(expression, FunctionCode.MAX, false);
    }

    /** Creates a {@code MIN} aggregate function expression with {@code DISTINCT} keyword. */
    public static FunctionNode min(Expression expression, Distinct distinct){
        return new FunctionNode(expression, FunctionCode.MIN, true);
    }
    /** Creates a {@code MIN} aggregate function expression. */
    public static FunctionNode min(Expression expression){
        return new FunctionNode(expression, FunctionCode.MIN, false);
    }

    /** Creates a {@code COUNT} aggregate function expression with {@code DISTINCT} keyword. */
    public static FunctionNode count(Expression expression, Distinct distinct){
        return new FunctionNode(expression, FunctionCode.COUNT, true);
    }
    /** Creates a {@code COUNT} aggregate function expression. */
    public static FunctionNode count(Expression expression){
        return new FunctionNode(expression, FunctionCode.COUNT, false);
    }

    /**
     * Creates a logical {@code AND} expression over the given conditions.
     *
     * @param c1 the first condition
     * @param c2 the second condition
     * @param cRest remaining conditions
     * @return a combined {@code AND} expression
     */
    public static BinaryOp and(Expression c1, Expression c2, Expression... cRest){
        if (cRest.length > 0) {
            var result = cRest[cRest.length - 1];
            for (int i = cRest.length - 2; i >= 0; i--)
                result = new BinaryOp(cRest[i], result, BinaryOpCode.AND);
            return new BinaryOp(c1, new BinaryOp(c2, result, BinaryOpCode.AND), BinaryOpCode.AND);
        }
        return new BinaryOp(c1, c2, BinaryOpCode.AND);
    }

    /**
     * Creates a logical {@code OR} expression over the given conditions.
     *
     * @param c1 the first condition
     * @param c2 the second condition
     * @param cRest remaining conditions
     * @return a combined {@code OR} expression
     */
    public static BinaryOp or(Expression c1, Expression c2, Expression... cRest){
        if (cRest.length > 0) {
            var result = cRest[cRest.length - 1];
            for (int i = cRest.length - 2; i >= 0; i--)
                result = new BinaryOp(cRest[i], result, BinaryOpCode.OR);
            return new BinaryOp(c1, new BinaryOp(c2, result, BinaryOpCode.OR), BinaryOpCode.OR);
        }
        return new BinaryOp(c1, c2, BinaryOpCode.OR);
    }

    /**
     * Creates a logical {@code NOT} expression for the given condition.
     *
     * @param expression the condition to negate
     * @return a negated expression
     */
    public static UnaryOp not(Expression expression){
        return new UnaryOp(expression, UnaryOpCode.NOT);
    }

    /**
     * Marker object representing the SQL {@code DISTINCT} modifier
     * for aggregate functions.
     */
    private static class Distinct { public String toString() { return "DISTINCT"; } }
    public static final Distinct DISTINCT = new Distinct();
}
