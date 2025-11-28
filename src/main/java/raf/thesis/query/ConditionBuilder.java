package raf.thesis.query;

import lombok.NoArgsConstructor;

@SuppressWarnings("ClassEscapesDefinedScope")
@NoArgsConstructor
public class ConditionBuilder {
    /**
     * Specify field for condition check
     * @param fieldPath dot separated relation path to field
     * @return updated condition builder
     */
    public ConditionBuilder field(String fieldPath) {
        return this;
    }

    public ConditionBuilder gt(String value){
        return this;
    }
    public ConditionBuilder gt(QueryBuilder subQuery){
        return this;
    }

    public ConditionBuilder lt(String value){
        return this;
    }
    public ConditionBuilder lt(QueryBuilder subQuery){
        return this;
    }

    public ConditionBuilder eq(String value){
        return this;
    }
    public ConditionBuilder eq(QueryBuilder subQuery){
        return this;
    }

    public ConditionBuilder like(String pattern){
        return this;
    }

    public ConditionBuilder in(String... values){
        return this;
    }
    public ConditionBuilder in(QueryBuilder subQuery){
        return this;
    }

    public ConditionBuilder isNull(){
        return this;
    }

    public ConditionBuilder exists(QueryBuilder subquery){
        return this;
    }

    public static ConditionBuilder avg(Distinct distinct, String fieldPath){
        return new ConditionBuilder();
    }
    public static ConditionBuilder avg(String fieldPath){
        return avg(null, fieldPath);
    }

    public static ConditionBuilder sum(Distinct distinct, String fieldPath){
        return new ConditionBuilder();
    }
    public static ConditionBuilder sum(String fieldPath){
        return sum(null, fieldPath);
    }

    public static ConditionBuilder max(Distinct distinct, String fieldPath){
        return new ConditionBuilder();
    }
    public static ConditionBuilder max(String fieldPath){
        return max(null, fieldPath);
    }

    public static ConditionBuilder min(Distinct distinct, String fieldPath){
        return new ConditionBuilder();
    }
    public static ConditionBuilder min(String fieldPath){
        return min(null, fieldPath);
    }

    public static ConditionBuilder count(Distinct distinct, String fieldPath){
        return new ConditionBuilder();
    }
    public static ConditionBuilder count(String fieldPath){
        return count(null, fieldPath);
    }

    public static ConditionBuilder and(ConditionBuilder... conditions){
        return new ConditionBuilder();
    }

    public static ConditionBuilder or(ConditionBuilder... conditions){
        return new ConditionBuilder();
    }

    public static ConditionBuilder not(ConditionBuilder conditions){
        return new ConditionBuilder();
    }

    private static class Distinct { public String toString() { return "DISTINCT"; } }
    public static final Distinct DISTINCT = new Distinct();
}
