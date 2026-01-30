package raf.thesis.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raf.thesis.metadata.exception.DuplicateRelationNamesException;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Entity metadata store.
 */
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class EntityMetadata {
    private String tableName;
    private Class<?> entityClass;
    private List<Field> idFields = new ArrayList<>();
    private Map<String, ColumnMetadata> columns = new LinkedHashMap<>();
    private Map<String, RelationMetadata> relations = new HashMap<>();
    private List<Boolean> generatedId = new ArrayList<>();

    public void addRelation(RelationMetadata relation) {
        if(relations.put(relation.getRelationName().toLowerCase(), relation) != null) {
            throw new DuplicateRelationNamesException("Multiple relations with name '" + relation.getRelationName() + "' in class "
                    + entityClass.getSimpleName() + ". Relation names must be unique inside class!");
        }
    }
}
