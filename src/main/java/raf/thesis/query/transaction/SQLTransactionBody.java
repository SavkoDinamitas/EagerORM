package raf.thesis.query.transaction;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface SQLTransactionBody {
    void execute(Connection conn) throws SQLException;
}
