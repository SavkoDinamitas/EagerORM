package raf.thesis.metadata.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a POJO class as being mapped to a special database query result set.
 * Use this annotation for classes used in {@code GROUP BY} query mappings or any query mappings that do not return entity objects.
 * The class must provide getters and setters for all fields participating in the mapping.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PDO {
}
