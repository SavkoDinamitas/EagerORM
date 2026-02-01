package raf.thesis.query.dialect;

import raf.thesis.query.api.QueryBuilder;
import raf.thesis.query.tree.*;

import java.util.List;

/**
 * Defines database-specific SQL behavior and capabilities.
 * <p>
 * Encapsulates differences in SQL syntax, feature support,
 * and query generation rules between database vendors.
 * It is used by the {@link QueryBuilder} to produce compatible SQL.
 */
public interface Dialect {
    String generateSelectClause(SelectNode select, List<Literal> args);

    String generateJoinClause(JoinNode joinNode);

    String generateWhereClause(WhereNode whereNode, List<Literal> args);

    String generateHavingClause(HavingNode havingNode, List<Literal> args);

    String generateGroupByClause(GroupByNode groupByNode, List<Literal> args);

    String generateOrderByClause(List<OrderByNode> orderByNodes, List<Literal> args);

    String generateLimitClause(LimitNode limitNode, List<Literal> args);

    String generateBinaryOperationExp(BinaryOp operation, List<Literal> args);

    String generateUnaryOperationExp(UnaryOp operation, List<Literal> args);

    String generateLiteralExp(Literal literal, List<Literal> args);

    String generateFunctionExp(FunctionNode functionNode, List<Literal> args);

    String generateTupleExp(TupleNode tupleNode, List<Literal> args);

    String generateFieldExp(FieldNode fieldNode);

    String generateAliasedFieldExp(AliasedColumn column, List<Literal> args);

    String generateInsertQuery(List<String> columns, String tableName);

    String generateUpdateQuery(List<String> columns, String tableName, List<String> keyColumnNames);

    String generateDeleteQuery(List<String> keyColumnNames, String tableName);

    String generateUpsertQuery(List<String> columnNames, String tableName, List<String> keyColumnNames);

    /**
     * Special method to track literals in generated SQL query to make prepared statement arguments list
     */
    void registerLiteral(Literal literal, List<Literal> args);

    /**
     * Special interface to determine if dialect driver doesn't support same column names in generatedKeys() method.
     * RETURNING clause is used in that case.
     */
    public interface UsesInsertReturning extends Dialect{
        String generateInsertQuery(List<String> columns, String tableName, List<String> returningKeys);
    }
}
