module EagerORM {
    requires transitive java.sql;
    requires static lombok;
    requires org.reflections;
    requires org.slf4j;
    requires org.apache.commons.beanutils2;
    exports raf.thesis.api;
    exports raf.thesis.query.dialect;
    exports raf.thesis.query.transaction;
    exports raf.thesis.metadata.annotations;
    exports raf.thesis.query.exceptions;
    exports raf.thesis.metadata.exception;
    exports raf.thesis.mapper.exceptions;
    exports raf.thesis.query.internal.tree;
}