package raf.thesis.query.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Functional interface for representing transaction bodies that doesn't have return value.
 */
@FunctionalInterface
public interface SQLTransactionBody {
    void execute(Connection conn) throws SQLException;
}
