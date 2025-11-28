package raf.thesis.query;


import lombok.NoArgsConstructor;
import raf.thesis.metadata.EntityMetadata;
import raf.thesis.metadata.storage.MetadataStorage;


@SuppressWarnings("ClassEscapesDefinedScope")
@NoArgsConstructor
public class QueryBuilder {
    /**
     * Set root object of query builder to specify type that is returned
     * @param object .class of entity a query should return
     * @return updated query builder
     */
    public QueryBuilder select(Class<?> object){
        return this;
    }

    /**
     * Add distinct keyword in query
     * @return updated query builder
     */
    public QueryBuilder distinct(){
        return this;
    }

    /**
     * Specify which relations should be populated in returning objects (inner join by default)
     * @param relationPath dot separated relation path from root entity
     * @return updated query builder
     */
    public QueryBuilder join(String relationPath){
        return this;
    }

    /**
     * Specify which relations should be populated in returning objects
     * @param relationPath dot separated relation path from root entity
     * @param left boolean to determine type of join, left is true -> left join, left is false -> inner join
     * @return updated query builder
     */
    public QueryBuilder join(String relationPath, boolean left){
        return this;
    }

    /**
     * Specify where clause for query
     * @param condition condition inside where clause
     * @return updated query builder
     */
    public QueryBuilder where(ConditionBuilder condition){
        return this;
    }

    /**
     * Specify having clause for query
     * @param condition condition inside having clause
     * @return updated query builder
     */
    public QueryBuilder having(ConditionBuilder condition){
        return this;
    }

    /**
     * Specify groupBy clause for query
     * @param fieldPath dot separated relation path to field
     * @return updated query builder
     */
    public QueryBuilder groupBy(String... fieldPath){
        return this;
    }

    /**
     * Specify orderBy clause for query
     * @param fieldPath dot separated relation path to field
     * @return updated query builder
     */
    public QueryBuilder orderBy(String... fieldPath){
        return this;
    }

    /**
     * Specify limit of number of rows that should be returned
     * @param limit maximum number of rows
     * @return updated query builder
     */
    public QueryBuilder limit(int limit){
        return this;
    }

    /**
     * Specify from which row should DB return the result
     * @param offset offset from start
     * @return updated query builder
     */
    public QueryBuilder offset(int offset){
        return this;
    }

    /**
     * Generate SQL query from builder
     * @return built SQL query
     */
    public String build(){
        return "";
    }

    //traverse through path to find right class
    private Class<?> findInstanceType(String path, Class<?> start) {
        Class<?> current = start;
        for (String s : path.split("\\.")) {
            EntityMetadata currMeta = MetadataStorage.get(current);
            for (var relation : currMeta.getRelations()) {
                if (relation.getRelationName().equalsIgnoreCase(s)) {
                    current = relation.getForeignClass();
                    break;
                }
            }
        }
        return current;
    }

    private static enum Join{LEFT, INNER, RIGHT, FULL};
    public final Join LEFT = Join.LEFT;
    public final Join INNER = Join.INNER;
    public final Join RIGHT = Join.RIGHT;
    public final Join FULL = Join.FULL;
}
