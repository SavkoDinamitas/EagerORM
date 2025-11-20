package raf.thesis.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;


@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ColumnMetadata {
    private String columnName;
    private Field field;
}
