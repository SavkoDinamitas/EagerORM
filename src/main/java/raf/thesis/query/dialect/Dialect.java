package raf.thesis.query.dialect;

import raf.thesis.query.tree.*;

import java.util.List;

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

    void registerLiteral(Literal literal, List<Literal> args);

    public interface UsesInsertReturning extends Dialect{
        String generateInsertQuery(List<String> columns, String tableName, List<String> returningKeys);
    }
}
