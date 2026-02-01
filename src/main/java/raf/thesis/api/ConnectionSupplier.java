package raf.thesis.api;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Functional interface for supplying database connections from any underlying source
 * (e.g. JDBC {@link javax.sql.DataSource}, connection pool, test container, or direct driver).
 */
@FunctionalInterface
public interface ConnectionSupplier {
    /**
     * Obtains a database {@link Connection}.
     *
     * @return a database connection
     * @throws SQLException if acquiring the connection fails
     */
    Connection getConnection() throws SQLException;
}
