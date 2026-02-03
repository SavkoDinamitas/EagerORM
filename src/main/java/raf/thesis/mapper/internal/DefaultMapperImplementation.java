/*
 * EagerORM - A predictable object-relation mapper
 * Copyright (C) 2026  Dimitrije Andžić <dimitrije.andzic@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package raf.thesis.mapper.internal;

import org.apache.commons.beanutils2.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import raf.thesis.mapper.exceptions.ClassInstantiationException;
import raf.thesis.mapper.exceptions.ResultSetAccessException;
import raf.thesis.mapper.exceptions.TypeConversionException;
import raf.thesis.metadata.internal.ColumnMetadata;
import raf.thesis.metadata.internal.EntityMetadata;
import raf.thesis.metadata.internal.RelationMetadata;
import raf.thesis.metadata.internal.RelationType;
import raf.thesis.metadata.internal.storage.MetadataStorage;
import raf.thesis.query.exceptions.InvalidRelationPathException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultMapperImplementation implements RowMapper {
    private static final Logger log = LoggerFactory.getLogger(DefaultMapperImplementation.class);

    @Override
    public <T> T map(ResultSet rs, Class<T> clazz) {
        return singleRowMap(rs, clazz);
    }

    @Override
    public <T> T map(ResultSet rs, T instance){
        return singleRowMap(rs, instance);
    }

    @Override
    public <T> List<T> mapList(ResultSet rs, Class<T> clazz) {
        List<T> instances = new ArrayList<>();
        try {
            while (rs.next()) {
                instances.add(singleRowMap(rs, clazz));
            }
        } catch (SQLException e) {
            throw new ResultSetAccessException(e);
        }
        return instances;
    }

    /**
     * ResultSet column aliases must be a dot separated relation path from root object.
     * Root table alias is "%root", so for root table columns, aliases should be "%root.colName".
     * For other columns, aliases should be "%root.relationName1.relationName2...colName" to specify
     * which two objects are related.
     * Algorithm goes row by row:
     * In each row detect all objects and create their instances with mapped attributes.
     * Using one global map deduplicate entities with same primary keys. Key of the map is list of .class
     * type of entity and value of its primary key. After deduplication, connect objects that are in same
     * row using the above-mentioned aliases: take the last relation in path and find object that is gotten to
     * via the prefix of the path. Insert first object in relation field of the prefix object by finding the
     * relation with that name. At the end, only the objects that have relation path "%root" are returned by
     * inserting them in LinkedSet to preserve order and deduplicate.
     */
    @Override
    public <T> List<T> mapWithRelations(ResultSet rs, Class<T> clazz) {
        Map<List<Object>, Object> madeObjects = new HashMap<>();
        //linked hash set to preserve order from DB
        Set<Object> returningObjects = new LinkedHashSet<>();
        Set<List<Object>> relationDeduplication = new HashSet<>();
        try {
            while (rs.next()) {
                ResultSetMetaData rsMeta = rs.getMetaData();
                int columnCount = rsMeta.getColumnCount();
                //constructing objects
                Map<String, Object> rowInstances = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = rsMeta.getColumnLabel(i);
                    List<String> path = Arrays.stream(columnName.split("\\."))
                            .collect(Collectors.toList());
                    String fieldName = path.getLast();
                    path.removeLast();
                    String joinedPath = String.join(".", path);

                    Object instance = rowInstances.get(joinedPath);
                    if (instance == null) {
                        var type = findInstanceType(path, clazz);
                        instance = type.getDeclaredConstructor().newInstance();
                        rowInstances.put(joinedPath, instance);
                    }
                    //map column in instance, handle null objects from DB
                    if (!mapSingleProperty(instance, columnName, fieldName, rs))
                        rowInstances.put(joinedPath, NullMarker.NULL);
                }
                //deduplicating objects by inserting in madeObjects map
                for (var entry : rowInstances.entrySet()) {
                    //skip null objects
                    if (entry.getValue().equals(NullMarker.NULL)) {
                        continue;
                    }
                    madeObjects.putIfAbsent(getPrimaryKey(entry.getValue()), entry.getValue());
                }
                //solve relations
                for (var freshObjectEntry : rowInstances.entrySet()) {
                    List<String> path = Arrays.stream(freshObjectEntry.getKey().split("\\.")).collect(Collectors.toList());
                    //skip mapping null objects
                    if (freshObjectEntry.getValue().equals(NullMarker.NULL)) {
                        continue;
                    }
                    //these are original object required to return (relation path is "%root")
                    if (path.size() == 1) {
                        returningObjects.add(madeObjects.get(getPrimaryKey(freshObjectEntry.getValue())));
                        continue;
                    }
                    //if path is longer than 1, object should be connected to its predecessor in relation path
                    List<Object> myKey = getPrimaryKey(freshObjectEntry.getValue());
                    String relation = path.getLast();
                    path.removeLast();
                    String joinedPath = String.join(".", path);
                    List<Object> parentKey = getPrimaryKey(rowInstances.get(joinedPath));
                    Object parent = madeObjects.get(parentKey);
                    Object child = madeObjects.get(myKey);
                    //relation deduplication
                    if (relationDeduplication.add(List.of(parent, child, relation))) {
                        solveRelations(parent, child, relation);
                    }

                }

            }
            List<T> instances = new ArrayList<>();
            for (var entry : returningObjects) {
                if (clazz.isInstance(entry)) {
                    instances.add((T) entry);
                }
            }
            return instances;
        } catch (SQLException e) {
            throw new ResultSetAccessException(e);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new ClassInstantiationException(e);
        }
    }

    /**
     * Depending on the type of relation, populate missing objects.
     * Find the relation with the same name in parent and add child
     * to its relation field. If it is a list add a new element, else
     * just set it to child.
     */
    private void solveRelations(Object parent, Object child, String relationName) {
        Class<?> parentClass = parent.getClass();
        EntityMetadata parentMetadata = MetadataStorage.get(parentClass);
        RelationMetadata relation = parentMetadata.getRelations().get(relationName.toLowerCase());
        if (relation == null) {
            throw new RuntimeException("Relation " + relationName + " not found");
        }
        try {
            if (relation.getRelationType() == RelationType.ONE_TO_MANY || relation.getRelationType() == RelationType.MANY_TO_MANY) {
                Field fk = relation.getForeignField();
                fk.setAccessible(true);
                Object listObject = fk.get(parent);
                if (listObject == null) {
                    List newList = new ArrayList<>();
                    newList.add(child);
                    fk.set(parent, newList);
                } else {
                    ((List) listObject).add(child);
                }
            } else {
                Field fk = relation.getForeignField();
                fk.setAccessible(true);
                fk.set(parent, child);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error populating the relationship " + relationName, e);
        }

    }

    /**
     * Traverse through relation path to find required class type
     */
    private Class<?> findInstanceType(List<String> path, Class<?> start) {
        Class<?> current = start;
        for (int i = 1; i < path.size(); i++) {
            EntityMetadata currMeta = MetadataStorage.get(current);
            var nextRel = currMeta.getRelations().get(path.get(i).toLowerCase());
            if(nextRel == null)
                throw new InvalidRelationPathException("Invalid relation path: " + path);
            current = nextRel.getForeignClass();
        }
        return current;
    }

    /**
     * Make a new instance of clazz type with mapped fields from ResultSet row.
     */
    private <T> T singleRowMap(ResultSet rs, Class<T> clazz) {
        EntityMetadata entityMetadata = MetadataStorage.get(clazz);

        if (entityMetadata == null) {
            return null;
        }


        T instance;
        try {
            instance = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new ClassInstantiationException(e);
        }
        return singleRowMap(rs, instance);
    }

    /**
     * Map single row of a ResultSet into a given instance
     */
    private <T> T singleRowMap(ResultSet rs, T instance){
        //for each column in result set, find the designated field
        // and do the conversion to java datatype
        try {
            ResultSetMetaData rsMeta = rs.getMetaData();
            int columnCount = rsMeta.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = rsMeta.getColumnLabel(i).toLowerCase();
                mapSingleProperty(instance, columnName, columnName, rs);
            }

            return instance;
        } catch (SQLException e) {
            throw new ResultSetAccessException(e);
        }
    }

    /**
     * Maps single column value to property in instance,
     * returns false if DB null object mapping is detected.
     */
    private boolean mapSingleProperty(Object instance, String resultSetColumnName, String columnName, ResultSet rs) {
        try {
            Class<?> clazz = instance.getClass();
            EntityMetadata entityMetadata = MetadataStorage.get(clazz);
            //check if instance is nullObject
            if (instance.equals(NullMarker.NULL))
                return true;

            ColumnMetadata columnMetadata = entityMetadata.getColumns().get(columnName);

            if (columnMetadata == null) {
                // Column doesn't belong to this entity -> skip for now
                log.warn("Column '{}' does not exist in entity '{}'; skipping.", columnName, clazz.getSimpleName());
                return true;
            }

            Field field = columnMetadata.getField();
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();
            Object value;
            //check if it is enum
            if(fieldType.isEnum()){
                value = enumFromString(fieldType, rs.getString(resultSetColumnName));
            }
            //convert primitive types to java wrappers for JDBC
            else
                value = rs.getObject(resultSetColumnName, javaPrimitiveTypes(fieldType));
            //check if field is PK and null -> db null object
            List<Field> pkFields = entityMetadata.getIdFields();
            if (pkFields.contains(field) && value == null) {
                //flag for null object
                return false;
            }
            //populate field
            BeanUtils.setProperty(instance, fieldName, value);
            return true;
        } catch (SQLException e) {
            throw new ResultSetAccessException(e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new TypeConversionException(e);
        }
    }

    /** Construct instance of enum that is given */
    private <E extends Enum<E>> E enumFromString(Class<?> enumClass, String value) {
        assert enumClass.isEnum();
        if (value == null) return null;
        return Enum.valueOf((Class<E>) enumClass, value);
    }

    /** Extract primary key value from instance */
    private List<Object> getPrimaryKey(Object instance) {
        try {
            EntityMetadata objectMetadata = MetadataStorage.get(instance.getClass());
            List<Object> keys = new ArrayList<>();
            keys.add(instance.getClass());
            var pkFields = objectMetadata.getIdFields();
            for (var pk : pkFields) {
                pk.setAccessible(true);
                keys.add(pk.get(instance));
            }
            return keys;
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract primary key from object " + instance.getClass(), e);
        }
    }

    //map for solving primitive java types
    private static final Map<Class<?>, Class<?>> primitiveTypes = Map.of(
            boolean.class, Boolean.class,
            byte.class, Byte.class,
            char.class, Character.class,
            short.class, Short.class,
            int.class, Integer.class,
            long.class, Long.class,
            float.class, Float.class,
            double.class, Double.class
            );

    private Class<?> javaPrimitiveTypes(Class<?> clazz) {
        return primitiveTypes.getOrDefault(clazz, clazz);
    }

    private enum NullMarker {NULL}
}
