package raf.thesis.mapper.internal;

import java.sql.ResultSet;
import java.util.List;

/**
 * Interface for mapping layer of EagerORM
 */
public interface RowMapper {
    /**
     * Maps ResultSet from DB to new instance of given class.
     * Doesn't map relations, only direct fields.
     * @param rs ResultSet from DB with single row
     * @param clazz required return class type
     * @return mapped class
     */
    <T> T map(ResultSet rs, Class<T> clazz);

    /**
     * Updates given instance with ResultSet first row data.
     * Doesn't map relations, only direct fields.
     * @param rs ResultSet from DB with single row
     * @param instance instance to be updated
     * @return updated instance
     */
    <T> T map(ResultSet rs, T instance);

    /**
     * Maps ResultSet from DB to a list of new instances of given class.
     * Doesn't map relations, only direct fields.
     * @param rs ResultSet from DB with multiple rows
     * @param clazz required return class type
     * @return list of mapped classes
     */
    <T> List<T> mapList(ResultSet rs, Class<T> clazz);

    /**
     * Maps ResultSet from DB to a list of new instances of given class
     * with all relations. Column aliases must be in a specific EagerORM
     * format for relations to be mapped successfully.
     * @param rs ResultSet from DB
     * @param clazz required return class type
     * @return list of mapped classes with all relations
     */
    <T> List<T> mapWithRelations(ResultSet rs, Class<T> clazz);
}
