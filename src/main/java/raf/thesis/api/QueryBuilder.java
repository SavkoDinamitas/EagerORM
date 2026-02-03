package raf.thesis.api;

import lombok.extern.slf4j.Slf4j;
import raf.thesis.metadata.internal.EntityMetadata;
import raf.thesis.metadata.internal.RelationMetadata;
import raf.thesis.metadata.internal.RelationType;
import raf.thesis.metadata.internal.storage.MetadataStorage;
import raf.thesis.query.internal.PreparedStatementQuery;
import raf.thesis.query.dialect.Dialect;
import raf.thesis.query.exceptions.InvalidRelationPathException;
import raf.thesis.query.internal.tree.*;

import java.util.*;
import java.util.stream.Stream;

/**
 * Provides a simple API for constructing SQL queries compatible with EagerORM.
 * Supports standard SQL query clauses and expressions via {@link ConditionBuilder} class
 * and {@link Expression} methods.
 * <p>
 * The builder is compatible with both entity-mapped objects and Plain Data Objects (PDOs) annotated
 * with {@link raf.thesis.metadata.annotations.PDO}. For PDOs, select expressions may include custom aliases
 * that directly correspond to object fields.
 * <p>
 * Provides integration with {@link SubQueryBuilder} for constructing subquery expressions,
 * allowing the composition of complex queries.
 */
@Slf4j
public class QueryBuilder {
    protected final SelectNode rootSelectNode;

    private final Set<String> joinTables = new HashSet<>();

    private boolean pdoQuery = false;

    protected List<Literal> queryArguments = new ArrayList<>();

    /**
     * Sets the root entity of the {@code QueryBuilder} to specify the type of object the query will return.
     *
     * @param object class of the entity the query should return
     * @return a new {@code QueryBuilder} instance with the specified root entity
     */
    public static QueryBuilder select(Class<?> object){
        SelectNode sn = new SelectNode(object, MetadataStorage.get(object).getTableName());
        return new QueryBuilder(sn);
    }

    /**
     * Constructs a {@code QueryBuilder} for returning {@link raf.thesis.metadata.annotations.PDO}-annotated objects.
     * A {@code QueryBuilder} constructed with this method doesn't support entity mapping.
     *
     * @param object class of the entity whose table will be used in the FROM clause
     * @param a1 a column expression to return with a custom alias (should match the PDO field name)
     * @param columns additional column expressions to return
     * @return a new {@code QueryBuilder} instance with the specified root table and selected columns
     */

    public static QueryBuilder select(Class<?> object, AliasedColumn a1, AliasedColumn... columns){
        SelectNode sn = new SelectNode(object, MetadataStorage.get(object).getTableName());
        return new QueryBuilder(sn, Stream.concat(Stream.of(a1), Stream.of(columns)).toList());
    }

    /**
     * Constructs a {@link SubQueryBuilder} for making subquery expressions.
     *
     * @param object class of the entity whose table will be used in the FROM clause
     * @param e1 first column expression the subquery should return
     * @param exps additional column expressions the subquery should return
     * @return new instance of {@code SubQueryBuilder}
     */
    public static SubQueryBuilder subQuery(Class<?> object, Expression e1, Expression... exps){
        return new SubQueryBuilder(object, Stream.concat(Stream.of(e1), Stream.of(exps)).toList());
    }

    /**
     * Main {@code QueryBuilder} constructor.
     *
     * @param rootSelectNode root node of the select query AST
     */
    protected QueryBuilder(SelectNode rootSelectNode){
        this.rootSelectNode = rootSelectNode;
        handleRootColumns(rootSelectNode.getRoot());
    }

    /**
     * Constructs a {@code QueryBuilder} for {@link raf.thesis.metadata.annotations.PDO}-annotated objects.
     * Invoked by {@link QueryBuilder#select(Class, AliasedColumn, AliasedColumn...)}.
     *
     * @param rootSelectNode root node of the select query AST
     * @param columns select clause expressions with custom aliases
     */

    private QueryBuilder(SelectNode rootSelectNode, List<AliasedColumn> columns){
        List<Expression> cols = new ArrayList<>(columns);
        rootSelectNode.setSelectFieldNodes(cols);
        this.rootSelectNode = rootSelectNode;
        this.pdoQuery = true;
    }
    /**
     * Adds {@code DISTINCT} keyword in select query.
     *
     * @return this query builder with the {@code DISTINCT} keyword applied
     */
    public QueryBuilder distinct(){
        rootSelectNode.setDistinct(true);
        return this;
    }

    /**
     * Specifies which relations should be populated in the returned objects.
     * Uses an {@link Join#INNER} by default.
     *
     * @param relationPath dot-separated relation path from the root entity
     * @return this query builder with the added {@code JOIN} clause
     */
    public QueryBuilder join(String relationPath){
        rootSelectNode.addJoinNode(generateJoinNode(rootSelectNode.getRoot(), relationPath, Join.INNER));
        if(!pdoQuery)
            handleJoinedTableColumns(relationPath);
        return this;
    }

    /**
     * Specifies which relations should be populated in the returned objects.
     *
     * @param relationPath dot-separated relation path from the root entity
     * @param join the join type to use {@link Join}
     * @return this query builder with the added {@code JOIN} clause
     */
    public QueryBuilder join(String relationPath, Join join){
        rootSelectNode.addJoinNode(generateJoinNode(rootSelectNode.getRoot(), relationPath, join));
        if(!pdoQuery)
            handleJoinedTableColumns(relationPath);
        return this;
    }

    /**
     * Specifies the {@code WHERE} clause for the query.
     *
     * @param expression the {@code WHERE} condition expression
     * @return this query builder with the {@code WHERE} clause applied
     */
    public QueryBuilder where(Expression expression){
        rootSelectNode.setWhereNode(new WhereNode(expression));
        return this;
    }

    /**
     * Specifies the {@code HAVING} clause for the query.
     * Intended for queries that map to {@link raf.thesis.metadata.annotations.PDO} objects.
     *
     * @param expression the {@code HAVING} condition expression
     * @return this query builder with the {@code HAVING} clause applied
     */
    public QueryBuilder having(Expression expression){
        rootSelectNode.setHavingNode(new HavingNode(expression));
        return this;
    }

    /**
     * Specifies the {@code GROUP BY} clause for the query.
     * Intended for queries that map to {@link raf.thesis.metadata.annotations.PDO} objects.
     *
     * @param e1 required primary grouping expression
     * @param expressions optional additional grouping expressions
     * @return this query builder with the grouping applied
     */
    public QueryBuilder groupBy(Expression e1, Expression... expressions){
        rootSelectNode.setGroupByNode(new GroupByNode(Stream.concat(Stream.of(e1), Stream.of(expressions)).toList()));
        return this;
    }

    /**
     *  Specifies the {@code ORDER BY} clause for the query.
     * Use {@link ConditionBuilder#asc} and {@link ConditionBuilder#desc} static functions to create ordering nodes.
     *
     * @param orderByNode required primary ordering node
     * @param others optional additional ordering nodes
     * @return this query builder with the ordering applied
     */
    public QueryBuilder orderBy(OrderByNode orderByNode, OrderByNode... others){
        rootSelectNode.setOrderByNodes(Stream.concat(Stream.of(orderByNode), Stream.of(others)).toList());
        return this;
    }

    /**
     * Specifies the maximum number of rows that should be returned beginning from the offset.
     * If offset is not specified, returns the rows starting from the beginning.
     *
     * @param limit maximum number of rows to return
     * @return this query builder with the limit applied
     */
    public QueryBuilder limit(int limit){
        rootSelectNode.setLimit(limit);
        return this;
    }

    /**
     * Specifies the starting row (offset) from which the database should return result rows.
     *
     * @param offset offset number of rows to skip before returning results
     * @return this query builder with the offset applied
     */
    public QueryBuilder offset(int offset){
        rootSelectNode.setOffset(offset);
        return this;
    }

    /**
     * Generates SQL query from builder using given {@link Dialect}.
     *
     * @param dialect dialect used for query generation
     * @return built SQL query
     */
    String build(Dialect dialect){
        return generateSelectClause(dialect) + "\n" + generateJoinClauses(dialect) + generateWhereClause(dialect) + generateGroupByClause(dialect) + generateHavingClause(dialect) + generateOrderByClause(dialect) + generateLimitClause(dialect) + ";";
    }

    PreparedStatementQuery buildPreparedStatement(Dialect dialect){
        //reset queryArguments in case there are multiple calls of same QB
        queryArguments = new ArrayList<>();
        String query = build(dialect);
        //log generated query
        log.debug("Generated select query:\n{}\nParameters:\n{}", query, queryArguments);
        return new PreparedStatementQuery(query, Collections.unmodifiableList(queryArguments));
    }

    /**
     * Creates the select AST's {@link JoinNode} nodes for the given relation path.
     *
     * @param root root class of select node
     * @param joiningRelationPath dot-separated relation path of joining table
     * @param joinType {@link Join} join type
     * @return list of generated {@link JoinNode} AST nodes
     */
    private List<JoinNode> generateJoinNode(Class<?> root, String joiningRelationPath, Join joinType){
        //fill foreign table fields
        String foreignTableAlias;
        //find alias for old table (old -> already joined or root)
        if(joiningRelationPath.split("\\.").length > 1)
            foreignTableAlias = joiningRelationPath.substring(0, joiningRelationPath.lastIndexOf("."));
        else
            foreignTableAlias = rootSelectNode.getBaseAlias();
        //locate relation metadata of given path
        Class<?> foreignClass = findInstanceType(foreignTableAlias, root);
        EntityMetadata foreignMetadata = MetadataStorage.get(foreignClass);
        String relationName = joiningRelationPath.substring(joiningRelationPath.lastIndexOf(".") + 1);
        RelationMetadata rel = foreignMetadata.getRelations().get(relationName.toLowerCase());
        //check if relation exists
        if(rel == null)
            throw new InvalidRelationPathException(joiningRelationPath);
        //2 join nodes for many_to_many relations
        if(rel.getRelationType() == RelationType.MANY_TO_MANY){
            List<JoinNode> result = new ArrayList<>();
            //make node for joining table on first time joinedTableNameUse
            //no need to make new one for other side join in same query
            String joinedTableName = rel.getJoinedTableName();
            if(joinTables.add(joinedTableName)){
                List<String> joiningTablePk = rel.getMyJoinedTableFks();
                result.add(new JoinNode(joinType, joinedTableName, joinedTableName, joiningTablePk, foreignTableAlias, extractKeys(foreignMetadata)));
            }
            //get table name for joining class
            Class<?> joiningClass = findInstanceType(joiningRelationPath, root);
            EntityMetadata joiningMetadata = MetadataStorage.get(joiningClass);
            String tableName = joiningMetadata.getTableName();
            //make list of 2 nodes for n:m relations
            List<String> secondTablePk = extractKeys(joiningMetadata);
            result.add(new JoinNode(Join.INNER, tableName, joiningRelationPath, secondTablePk, joinedTableName, rel.getForeignKeyNames()));
            return result;
        }
        else{
            //fill joining table fields
            Class<?> joiningClass = findInstanceType(joiningRelationPath, root);
            EntityMetadata joiningMetadata = MetadataStorage.get(joiningClass);
            String tableName = joiningMetadata.getTableName();
            //two cases: @MANY_TO_ONE and @ONE_TO_ONE with containsKey = true and others
            //@MANY_TO_ONE -> old table has the foreign key and new one uses its pk for join
            //others -> joining table has the fk that joins on old table pk
            List<String> joiningTablePk;
            List<String> foreignTableKeys;
            if(rel.getRelationType() == RelationType.MANY_TO_ONE || (rel.getRelationType() == RelationType.ONE_TO_ONE && rel.getMySideKey())){
                foreignTableKeys = rel.getForeignKeyNames();
                joiningTablePk = extractKeys(joiningMetadata);
            }
            else{
                foreignTableKeys = extractKeys(foreignMetadata);
                joiningTablePk = rel.getForeignKeyNames();
            }
            return List.of(new JoinNode(joinType, tableName, joiningRelationPath, joiningTablePk, foreignTableAlias, foreignTableKeys));
        }
    }

    /**
     * Traverses through the relation path to find the last class on that path.
     * Used to determine which entity is supposed to be joined.
     *
     * @param path dot-separated relation path
     * @param start root object to start from traversing
     * @return last entity on relation path
     */
    private Class<?> findInstanceType(String path, Class<?> start) {
        Class<?> current = start;
        if(path.equals(rootSelectNode.getBaseAlias()))
            return start;
        for (String s : path.split("\\.")) {
            EntityMetadata currMeta = MetadataStorage.get(current);
            var nextRel = currMeta.getRelations().get(s.toLowerCase());
            if(nextRel == null)
                throw new InvalidRelationPathException("Invalid relation path: " + path);
            current = nextRel.getForeignClass();
        }
        return current;
    }

    /**
     * Returns the list of columns that are primary keys in given entity metadata
     */
    private List<String> extractKeys(EntityMetadata metadata){
        List<String> keys = new ArrayList<>();
        for(var column : metadata.getColumns().values()){
            if(metadata.getIdFields().contains(column.getField())){
                keys.add(column.getColumnName());
            }
        }
        return keys;
    }

    /**
     * Adds root columns in select clause
     */
    private void handleRootColumns(Class<?> root){
        extractColumns(MetadataStorage.get(root), rootSelectNode.getBaseAlias());
    }

    /**
     * Adds joined table columns in select clause
     */
    private void handleJoinedTableColumns(String relationPath){
        Class<?> entity = findInstanceType(relationPath, rootSelectNode.getRoot());
        extractColumns(MetadataStorage.get(entity), relationPath);
    }

    /**
     * Makes list of FieldNodes and adds them to select clause in AST
     */
    private void extractColumns(EntityMetadata metadata, String tableAlias){
        for(var column : metadata.getColumns().values()){
            FieldNode node = new FieldNode(column.getColumnName(), tableAlias);
            rootSelectNode.addSelectClauseColumn(node);
        }
    }

    //methods for generating clauses from their nodes via the given Dialect
    public String generateJoinClauses(Dialect dialect){
        StringBuilder builder = new StringBuilder();
        for(var join : rootSelectNode.getJoinNodes()){
            builder.append(dialect.generateJoinClause(join));
            builder.append("\n");
        }
        return builder.toString();
    }
    public String generateSelectClause(Dialect dialect){
        return dialect.generateSelectClause(rootSelectNode, queryArguments);
    }
    public String generateWhereClause(Dialect dialect){
        return rootSelectNode.getWhereNode() != null ? dialect.generateWhereClause(rootSelectNode.getWhereNode(), queryArguments) + "\n" : "";
    }
    public String generateGroupByClause(Dialect dialect){
        return rootSelectNode.getGroupByNode() != null ? dialect.generateGroupByClause(rootSelectNode.getGroupByNode(), queryArguments) + "\n" : "";
    }
    public String generateHavingClause(Dialect dialect){
        return rootSelectNode.getHavingNode() != null ? dialect.generateHavingClause(rootSelectNode.getHavingNode(), queryArguments) + "\n" : "";
    }
    public String generateOrderByClause(Dialect dialect){
        return rootSelectNode.getOrderByNodes() != null ? dialect.generateOrderByClause(rootSelectNode.getOrderByNodes(), queryArguments) + "\n" : "";
    }
    public String generateLimitClause(Dialect dialect){
        return rootSelectNode.getLimitNode() != null ? dialect.generateLimitClause(rootSelectNode.getLimitNode(), queryArguments) + "\n" : "";
    }
}
