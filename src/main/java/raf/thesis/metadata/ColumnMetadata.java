package raf.thesis.metadata;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;


/**
 * Column metadata store.
 */
@Getter @Setter @NoArgsConstructor
public class ColumnMetadata {
    private String columnName;
    private Field field;

    public ColumnMetadata(String columnName, Field field) {
        this.columnName = columnName.toLowerCase();
        this.field = field;
    }

    public void setColumnName(String name){
        columnName = name.toLowerCase();
    }
}
