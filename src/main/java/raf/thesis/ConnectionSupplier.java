package raf.thesis;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface ConnectionSupplier {
    Connection getConnection() throws SQLException;
}
