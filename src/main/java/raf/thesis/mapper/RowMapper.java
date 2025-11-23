package raf.thesis.mapper;

import java.sql.ResultSet;
import java.util.List;

public interface RowMapper {
    <T> T map(ResultSet rs, Class<T> clazz);

    <T> List<T> mapList(ResultSet rs, Class<T> clazz);

    <T> List<T> mapWithRelations(ResultSet rs, Class<T> clazz);
}
