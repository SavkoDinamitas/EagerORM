package raf.thesis.query.dialect;

import raf.thesis.query.tree.*;

import java.util.List;
import java.util.Map;

public interface Dialect {
    String generateSelectClause(SelectNode select);

    String generateJoinClause(JoinNode joinNode);

    String generateWhereClause(WhereNode whereNode);

    String generateHavingClause(HavingNode havingNode);

    String generateGroupByClause(GroupByNode groupByNode);

    String generateOrderByClause(List<OrderByNode> orderByNodes);

    String generateLimitClause(LimitNode limitNode);

    String generateBinaryOperationExp(BinaryOp operation);

    String generateUnaryOperationExp(UnaryOp operation);

    String generateLiteralExp(Literal literal);

    String generateFunctionExp(FunctionNode functionNode);

    String generateTupleExp(TupleNode tupleNode);

    String generateFieldExp(FieldNode fieldNode);

    String generateAliasedFieldExp(AliasedColumn column);

    String generateInsertClause(List<String> columns, String tableName);

    String generateUpdateClause(List<String> columns, String tableName, List<String> keyColumnNames);
}
