package raf.thesis.query.dialect;

import raf.thesis.query.tree.*;

import java.util.List;
import java.util.stream.Collectors;

public class ANSISQLDialect implements Dialect {
    protected String quote(String value){
        return "\"" + value.replaceAll("\"", "\"\"") + "\"";
    }

    @Override
    public String generateSelectClause(SelectNode select, List<Literal> args) {
        return "SELECT%s\n%s\n FROM %s AS %s".formatted(select.isDistinct() ? " DISTINCT" : "", generateFields(select.getSelectFieldNodes(), select.getBaseAlias(), args), select.getBaseTableName(), quote(select.getBaseAlias()));
    }

    private String generateFields(List<Expression> fieldNodes, String baseAlias, List<Literal> args){
        StringBuilder sb = new StringBuilder();
        for(Expression exp : fieldNodes){
            //generate fields with needed aliases for mapper
            if(exp instanceof FieldNode fn){
                sb.append(generateFieldExpWithAlias(fn, baseAlias));
            }
            else
                sb.append(exp.toSql(this, args));
            sb.append(",\n");
        }
        sb.delete(sb.length()-2, sb.length());
        return sb.toString();
    }

    @Override
    public String generateJoinClause(JoinNode joinNode){
        return "%s JOIN %s AS %s ON (%s)".formatted(joinNode.getJoinType().name(), joinNode.getTableName(), quote(joinNode.getJoiningTableAlias()), generateOnJoinClause(joinNode));
    }

    protected String generateOnJoinClause(JoinNode joinNode){
        return "%s = %s".formatted(generateKeyTuple(joinNode.getJoiningTableAlias(), joinNode.getJoiningTablePk()),
                generateKeyTuple(joinNode.getForeignTableAlias(), joinNode.getForeignTableFk()));
    }

    private String generateKeyTuple(String tableAlias, List<String> tableColumns){
        return "(%s)".formatted(tableColumns.stream().map("."::concat).map(quote(tableAlias)::concat).collect(Collectors.joining()));
    }

    @Override
    public String generateWhereClause(WhereNode whereNode, List<Literal> args) {
        return "WHERE " + whereNode.getExpression().toSql(this, args);
    }

    @Override
    public String generateHavingClause(HavingNode havingNode, List<Literal> args) {
        return "HAVING " + havingNode.getExpression().toSql(this, args);
    }

    @Override
    public String generateGroupByClause(GroupByNode groupByNode, List<Literal> args) {
        StringBuilder sb = new StringBuilder();
        sb.append("GROUP BY ");
        for(var g : groupByNode.getExpressions()){
            sb.append(g.toSql(this, args));
            sb.append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    @Override
    public String generateOrderByClause(List<OrderByNode> orderByNodes, List<Literal> args) {
        StringBuilder sb = new StringBuilder();
        sb.append("ORDER BY ");
        for(OrderByNode orderByNode : orderByNodes){
            sb.append(orderByNode.getExp().toSql(this, args));
            sb.append(" ");
            sb.append(orderByNode.getOrder());
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    @Override
    public String generateLimitClause(LimitNode limitNode, List<Literal> args) {
        return "%s %s".formatted(generateOffset(limitNode.getOffset(), args), generateLimit(limitNode.getLimit(), args));
    }

    protected String generateOffset(Integer offset, List<Literal> args){
        if(offset == null){
            return "";
        }
        else{
            //add literals to prepared statement arguments
            registerLiteral(new Literal.LongCnst(offset), args);
            return "OFFSET (?) ROWS";
        }
    }

    protected String generateLimit(Integer limit, List<Literal> args){
        if(limit == null){
            return "";
        }
        else{
            registerLiteral(new Literal.LongCnst(limit), args);
            return "FETCH NEXT (?) ROWS ONLY";
        }
    }

    @Override
    public String generateBinaryOperationExp(BinaryOp operation, List<Literal> args) {
        StringBuilder result = new StringBuilder();
        result.append("(");
        result.append(operation.getLeft().toSql(this, args));
        result.append(")");
        switch (operation.getCode()){
            case AND -> result.append(" AND (");
            case OR -> result.append(" OR (");
            case EQ -> result.append(" = (");
            case GT -> result.append(" > (");
            case LT -> result.append(" < (");
            case LIKE -> result.append(" LIKE (");
            case IN -> result.append(" IN ");
        }
        result.append(operation.getRight().toSql(this, args));
        if(operation.getCode() != BinaryOpCode.IN)
            result.append(")");
        return result.toString();
    }

    @Override
    public String generateUnaryOperationExp(UnaryOp operation, List<Literal> args) {
        StringBuilder result = new StringBuilder();
        switch (operation.getCode()){
            case NOT:
                result.append("NOT (");
                result.append(operation.getExp().toSql(this, args));
                result.append(")");
                break;
            case IS_NULL:
                result.append(operation.getExp().toSql(this, args));
                result.append(" IS NULL");
        }
        return result.toString();
    }

    @Override
    public String generateLiteralExp(Literal literal, List<Literal> args) {
//        return switch(literal){
//            case Literal.DoubleCnst d -> String.valueOf(d.x());
//            case Literal.LongCnst l -> String.valueOf(l.x());
//            case Literal.StringCnst s -> "'" + s.x() + "'";
//            case Literal.BoolCnst b -> String.valueOf(b.x());
//            case Literal.DateCnst d -> "'%s-%s-%s'".formatted(d.x().getYear(), d.x().getMonthValue(), d.x().getDayOfMonth());
//            case Literal.DateTimeCnst d -> "'%s-%s-%s %s:%s:%s.%03d'".formatted(d.x().getYear(), d.x().getMonthValue(), d.x().getDayOfMonth(), d.x().getHour(), d.x().getMinute(), d.x().getSecond(), d.x().getNano() / 1_000_000);
//            case Literal.TimeCnst d -> "'%s:%s:%s.%03d'".formatted(d.x().getHour(), d.x().getMinute(), d.x().getSecond(), d.x().getNano() / 1_000_000);
//            case Literal.NullCnst n -> "NULL";
//        };
        registerLiteral(literal, args);
        return "?";
    }

    @Override
    public String generateFunctionExp(FunctionNode functionNode, List<Literal> args) {
        StringBuilder result = new StringBuilder();
        switch (functionNode.getCode()){
            case COUNT -> result.append("COUNT(");
            case SUM -> result.append("SUM(");
            case AVG -> result.append("AVG(");
            case MIN -> result.append("MIN(");
            case MAX -> result.append("MAX(");
        }
        if(functionNode.isDistinct()){
            result.append("DISTINCT ");
        }
        result.append(functionNode.getExp().toSql(this, args));
        result.append(")");
        return result.toString();
    }

    @Override
    public String generateTupleExp(TupleNode tupleNode, List<Literal> args) {
        StringBuilder result = new StringBuilder();
        result.append("(");
        for(var x : tupleNode.getOperands()){
            result.append(x.toSql(this, args));
            result.append(",");
        }
        result.deleteCharAt(result.length()-1);
        result.append(")");
        return result.toString();
    }

    @Override
    public String generateFieldExp(FieldNode fieldNode) {
        return "%s.%s".formatted(quote(fieldNode.getTableAlias()), fieldNode.getFieldName());
    }

    @Override
    public String generateAliasedFieldExp(AliasedColumn column, List<Literal> args){
        return "%s AS %s".formatted(column.getExpression().toSql(this, args), column.getColAlias());
    }

    @Override
    public String generateInsertQuery(List<String> columns, String tableName) {
        return "INSERT INTO %s (%s) VALUES (%s);".formatted(tableName, generateInsertColumnParenthesis(columns), generateQuestionMarks(columns.size()));
    }



    @Override
    public String generateUpdateQuery(List<String> columns, String tableName, List<String> keyColumnNames) {
        return "UPDATE %s\nSET %sWHERE %s;".formatted(tableName, generateSetClause(columns), generateUpdateWhereClause(keyColumnNames));
    }

    @Override
    public String generateDeleteQuery(List<String> keyColumnNames, String tableName) {
        return "DELETE FROM %s\nWHERE %s;".formatted(tableName, generateUpdateWhereClause(keyColumnNames));
    }

    @Override
    public String generateUpsertQuery(List<String> columnNames, String tableName, List<String> keyColumnNames) {
        return """
                MERGE INTO %s t
                USING (VALUES(%s)) n(%s)
                ON(%s)
                WHEN MATCHED THEN
                    UPDATE SET
                     %s
                WHEN NOT MATCHED THEN
                    INSERT(%s)
                    VALUES(%s);
                """.formatted(tableName, generateQuestionMarks(columnNames.size()), generateInsertColumnParenthesis(columnNames), generateUpsertOnClause(keyColumnNames),
                generateUpsertMatchedClause(columnNames), generateInsertColumnParenthesis(columnNames), generateUpsertValuesClause(columnNames));
    }

    protected String generateUpsertOnClause(List<String> keyColumnNames){
        return "%s = %s".formatted(generateUpsertTuple(keyColumnNames, "t"), generateUpsertTuple(keyColumnNames, "n"));
    }

    protected String generateUpsertTuple(List<String> keyColumnNames, String table){
        return keyColumnNames.stream().map("."::concat).map(table::concat).collect(Collectors.joining(", ", "(", ")"));
    }

    protected String generateUpsertMatchedClause(List<String> columnNames){
        StringBuilder result = new StringBuilder();
        for (var column : columnNames){
            result.append(column);
            result.append(" = n.");
            result.append(column);
            result.append(",\n");
        }
        result.deleteCharAt(result.length()-1);
        result.deleteCharAt(result.length()-1);
        return result.toString();
    }

    protected String generateUpsertValuesClause(List<String> columnNames){
        return columnNames.stream().map("n."::concat).collect(Collectors.joining(", "));
    }

    @Override
    public void registerLiteral(Literal literal, List<Literal> args) {
        args.add(literal);
    }

    protected String generateSetClause(List<String> columns){
        StringBuilder result = new StringBuilder();
        for(String column : columns){
            result.append(column);
            result.append(" = ?");
            result.append(",\n");
        }
        result.delete(result.length()-2, result.length());
        result.append("\n");
        return result.toString();
    }

    protected String generateUpdateWhereClause(List<String> keys){
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < keys.size(); i++){
            result.append(keys.get(i));
            result.append(" = ?");
            if(i != keys.size()-1)
                result.append(" AND ");
        }
        return result.toString();
    }

    protected String generateInsertColumnParenthesis(List<String> columns){
        StringBuilder result = new StringBuilder();
        for(String column : columns){
            result.append(column);
            result.append(",");
        }
        result.deleteCharAt(result.length()-1);
        return result.toString();
    }

    protected String generateQuestionMarks(int number){
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < number; i++){
            result.append("?");
            result.append(",");
        }
        result.deleteCharAt(result.length()-1);
        return result.toString();
    }

    protected String generateFieldExpWithAlias(FieldNode fieldNode, String baseAlias) {
        return "%s AS %s".formatted(generateFieldExp(fieldNode),
                quote("%s%s.%s".formatted(handleRootField(fieldNode.getTableAlias(), baseAlias), fieldNode.getTableAlias(), fieldNode.getFieldName())));
    }

    protected String handleRootField(String tableAlias, String baseRoot){
        if(tableAlias.equals(baseRoot)){
            return "";
        }
        return baseRoot + ".";
    }
}
