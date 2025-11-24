package raf.thesis.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RelationMetadata {
    Field foreignField;
    String relationName;
    RelationType relationType;
    Class<?> foreignClass;
    String foreignRelationName;

    public RelationMetadata(Field foreignField, String relationName, RelationType relationType, Class<?> foreignClass) {
        this.foreignField = foreignField;
        this.relationName = relationName;
        this.relationType = relationType;
        this.foreignClass = foreignClass;
    }
}
