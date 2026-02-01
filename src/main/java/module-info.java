module EagerORM {
    requires java.sql;
    requires static lombok;
    requires org.apache.commons.beanutils;
    requires org.reflections;
    requires org.slf4j;
    exports raf.thesis.api;
    exports raf.thesis.query.api;
    exports raf.thesis.query.dialect;
    exports raf.thesis.query.transaction;
    exports raf.thesis.metadata.annotations;
    exports raf.thesis.query.exceptions;
    exports raf.thesis.metadata.exception;
    exports raf.thesis.mapper.exceptions;
}