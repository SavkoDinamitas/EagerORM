package raf.thesis.metadata.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a {@code List} field in an {@link Entity} class to be mapped as a one-to-many (1:n)
 * relationship in the database.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OneToMany {
    /**
     * Relationship names are used in making paths for {@link raf.thesis.query.QueryBuilder} join methods.
     * Default relationship name is the name of the field.
     */
    String relationName() default "";

    /**
     * Names of the columns in related object's table that act as a foreign key to the table of this object.
     * By default, foreign key column names match the primary key column names of the table of this object.
     */
    String[] foreignKey() default {};
}
