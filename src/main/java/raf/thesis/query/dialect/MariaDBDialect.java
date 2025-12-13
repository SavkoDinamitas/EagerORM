package raf.thesis.query.dialect;

import java.util.Arrays;
import java.util.stream.Collectors;

public class MariaDBDialect extends ANSISQLDialect{
    @Override
    protected String quote(String value){
        return "`" + value.replaceAll("`", "``") + "`";
    }
    public String generateReturningClause(String[] keys){
        return Arrays.stream(keys).collect(Collectors.joining(", ", " RETURNING ", ""));
    }
}
