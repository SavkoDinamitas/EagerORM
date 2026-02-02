package raf.thesis.metadata.annotations;

import raf.thesis.api.QueryBuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field in an {@link Entity} class to be mapped as a one-to-one (1:1)
 * relationship in the database.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OneToOne {
    /**
     * Relationship names are used in making paths for {@link QueryBuilder} join methods.
     * Default relationship name is the name of the field.
     */
    String relationName() default "";

    /**
     * Names of columns that act as a foreign key in this relationship.
     * Depending on the {@link OneToOne#containsFk()} field, foreign key
     * column names match the primary key of the other object's table by default.
     */
    String[] foreignKey() default {};

    /**
     * Determines if foreign key columns are positioned in this object's table or in a related object's table.
     *
     * @return {@code true} if foreign key is in this object's table, {@code false} if foreign key is in related object's table
     */
    boolean containsFk();
}
