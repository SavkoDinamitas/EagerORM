package raf.thesis.query.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Functional interface for representing transaction bodies that have return value.
 */
@FunctionalInterface
public interface SQLValuedTransactionBody<T> {
    T execute(Connection conn) throws SQLException;
}
