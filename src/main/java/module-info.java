module EagerORM {
    requires transitive java.sql;
    requires static lombok;
    requires org.reflections;
    requires org.slf4j;
    requires org.apache.commons.beanutils2;
    exports io.github.savkodinamitas.api;
    exports io.github.savkodinamitas.query.dialect;
    exports io.github.savkodinamitas.query.transaction;
    exports io.github.savkodinamitas.metadata.annotations;
    exports io.github.savkodinamitas.query.exceptions;
    exports io.github.savkodinamitas.metadata.exception;
    exports io.github.savkodinamitas.mapper.exceptions;
    exports io.github.savkodinamitas.query.internal.tree;
}