package raf.thesis.query.transaction;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface SQLValuedTransactionBody<T> {
    T execute(Connection conn) throws SQLException;
}
