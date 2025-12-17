package raf.thesis.metadata.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a POJO class as being mapped to a database table.
 * The class must provide getters and setters for all fields participating in the mapping.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Entity {
    /**
     * Name of the related table in the DB.
     * Must not be empty.
     */
    String tableName();
}
