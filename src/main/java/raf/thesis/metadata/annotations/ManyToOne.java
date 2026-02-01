package raf.thesis.metadata.annotations;

import raf.thesis.query.api.QueryBuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Marks a field in an {@link Entity} class to be mapped as a many-to-one (n:1)
 * relationship in the database.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ManyToOne {
    /**
     * Relationship names are used in making paths for {@link QueryBuilder} join methods.
     * Default relationship name is the name of the field.
     */
    String relationName() default "";

    /**
     * Names of the columns in this table that act as a foreign key to the related object.
     * By default, foreign key column names match the primary key column names of the related object's table.
     */
    String[] foreignKey() default {};
}
