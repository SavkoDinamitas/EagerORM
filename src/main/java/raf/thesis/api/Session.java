/*
 * EagerORM - A predictable object-relation mapper
 * Copyright (C) 2026  Dimitrije Andžić <dimitrije.andzic@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package raf.thesis.api;

import raf.thesis.mapper.internal.DefaultMapperImplementation;
import raf.thesis.mapper.internal.RowMapper;
import raf.thesis.metadata.internal.EntityMetadata;
import raf.thesis.metadata.internal.scan.MetadataScanner;
import raf.thesis.metadata.internal.storage.MetadataStorage;
import raf.thesis.query.internal.DBUpdateSolver;
import raf.thesis.query.internal.PreparedStatementQuery;
import raf.thesis.query.dialect.*;
import raf.thesis.query.exceptions.ConnectionUnavailableException;
import raf.thesis.query.exceptions.EntityObjectRequiredException;
import raf.thesis.query.transaction.SQLTransactionBody;
import raf.thesis.query.transaction.SQLValuedTransactionBody;
import raf.thesis.query.internal.tree.Literal;

import java.sql.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Central entry point for executing queries and persistence operations.
 * <p>
 * A {@code Session} manages SQL generation, execution, result mapping,
 * dialect resolution, and transaction scoping over connections supplied
 * by a {@link ConnectionSupplier}.
 */
@SuppressWarnings("ClassEscapesDefinedScope")
public class Session {
    private final ConnectionSupplier connectionSupplier;
    private final RowMapper rowMapper = new DefaultMapperImplementation();
    private final Dialect dialect;
    private final DBUpdateSolver DBUpdateSolver;
    private static final MetadataScanner metadataScanner = new MetadataScanner();

    private final ThreadLocal<Connection> activeConnection = new ThreadLocal<>();

    /**
     * Creates a session that auto-detects the SQL dialect from the database
     * connection metadata and scans the given packages for entity metadata.
     *
     * @param connectionSupplier supplier of database connections
     * @param scanPackage       package to scan for entity metadata
     * @param scanPackages other packages to scan for entity metadata
     */
    public Session(ConnectionSupplier connectionSupplier, String scanPackage, String... scanPackages) {
        this.connectionSupplier = connectionSupplier;
        metadataScanner.discoverMetadata(Stream.concat(Stream.of(scanPackage), Stream.of(scanPackages)).toArray(String[]::new));
        //detect which Dialect to use based on connection db
        dialect = getDialect();
        DBUpdateSolver = new DBUpdateSolver(dialect);
    }

    /**
     * Creates a session with an explicit SQL dialect and scans the given
     * packages for entity metadata.
     *
     * @param connectionSupplier supplier of database connections
     * @param dialect            SQL dialect to use
     * @param scanPackage       package to scan for entity metadata
     * @param scanPackages other packages to scan for entity metadata
     */
    public Session(ConnectionSupplier connectionSupplier, Dialect dialect, String scanPackage, String... scanPackages) {
        this.connectionSupplier = connectionSupplier;
        metadataScanner.discoverMetadata(Stream.concat(Stream.of(scanPackage), Stream.of(scanPackages)).toArray(String[]::new));
        //detect which Dialect to use based on connection db
        this.dialect = dialect;
        DBUpdateSolver = new DBUpdateSolver(dialect);
    }

    /**
     * Detect database from connection
     */
    private Dialect getDialect() {
        try (Connection conn = connectionSupplier.getConnection()) {
            String driverName = conn.getMetaData().getDriverName();
            if (driverName.toLowerCase().contains("mariadb"))
                return new MariaDBDialect();
            if (driverName.toLowerCase().contains("mysql"))
                return new MariaDBDialect();
            if (driverName.toLowerCase().contains("microsoft"))
                return new MSSQLServerDialect();
            if (driverName.toLowerCase().contains("postgresql"))
                return new PostgreSQLDialect();
            else
                return new ANSISQLDialect();
        } catch (SQLException e) {
            throw new ConnectionUnavailableException("Given connection supplier doesn't supply connections!");
        }
    }

    /**
     * Method that enables running of multiple Session methods on the same connection, used for transactions.
     * Each method that works with database connection is wrapped inside this method
     */
    private <T> T runBody(SQLValuedTransactionBody<T> body) throws SQLException {
        //if there is an active transaction, execute method on its connection
        if (activeConnection.get() != null) return body.execute(activeConnection.get());
        else
            try (var conn = connectionSupplier.getConnection()) {
                return body.execute(conn);
            }
    }

    /**
     * Executes a select query built by a {@link QueryBuilder} and maps
     * the result set to entity objects.
     *
     * @param queryBuilder query builder
     * @param resultClass  resulting mapped entity .class type
     * @return mapped list of entity objects
     */
    public <T> List<T> executeSelect(QueryBuilder queryBuilder, Class<T> resultClass) throws SQLException {
        return runBody(conn -> {
            PreparedStatementQuery pq = queryBuilder.buildPreparedStatement(dialect);
            if (pq.getQuery() == null)
                return null;
            ResultSet rs = executePreparedQuery(pq, conn);
            return rowMapper.mapWithRelations(rs, resultClass);
        });
    }

    /**
     * Executes a raw SQL select query and maps the result set to entity objects.
     * <p>
     * All field aliases must be in a dot-separated relation path format
     *
     * @param query       SQL query string in required format
     * @param resultClass resulting mapped entity .class type
     * @return mapped list of entity objects
     */
    public <T> List<T> executeSelect(String query, Class<T> resultClass) throws SQLException {
        return runBody(conn -> {
            List<T> result;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                result = rowMapper.mapWithRelations(rs, resultClass);
            }
            return result;
        });
    }

    /**
     * Executes a {@link QueryBuilder} select query and maps only the direct fields of the result
     * objects (no relations).
     * <p>
     * Used for mapping {@link raf.thesis.metadata.annotations.PDO} objects
     *
     * @param queryBuilder query builder
     * @param resultClass  resulting {@link raf.thesis.metadata.annotations.PDO} marked class
     * @return mapped list of {@link raf.thesis.metadata.annotations.PDO} objects
     */
    public <T> List<T> executePDOSelect(QueryBuilder queryBuilder, Class<T> resultClass) throws SQLException {
        return runBody((conn -> {
            PreparedStatementQuery pq = queryBuilder.buildPreparedStatement(dialect);
            if (pq.getQuery() == null)
                return null;
            ResultSet rs = executePreparedQuery(pq, conn);
            return rowMapper.mapList(rs, resultClass);
        }));
    }

    /**
     * Executes a raw SQL select query and maps only the direct fields of the result
     * objects (no relations).
     * <p>
     * All field aliases must be in a dot-separated relation path format.
     * <p>
     * Used for mapping {@link raf.thesis.metadata.annotations.PDO} objects
     *
     * @param query       SQL query string in required format
     * @param resultClass resulting {@link raf.thesis.metadata.annotations.PDO} marked class
     * @return mapped list of {@link raf.thesis.metadata.annotations.PDO} objects
     */
    public <T> List<T> executePDOSelect(String query, Class<T> resultClass) throws SQLException {
        return runBody((conn -> {
            List<T> result;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                result = rowMapper.mapList(rs, resultClass);
            }
            return result;
        }));
    }

    /**
     * Executes a {@link QueryBuilder} select query with a single row result
     * and maps only the direct fields of the result objects (no relations).
     * <p>
     * Used for mapping {@link raf.thesis.metadata.annotations.PDO} objects
     *
     * @param queryBuilder query builder
     * @param resultClass  resulting {@link raf.thesis.metadata.annotations.PDO} marked class
     * @return mapped list of {@link raf.thesis.metadata.annotations.PDO} objects
     */
    public <T> Optional<T> executeSingleRowPDOSelect(QueryBuilder queryBuilder, Class<T> resultClass) throws SQLException {
        return runBody(conn -> {
            PreparedStatementQuery pq = queryBuilder.buildPreparedStatement(dialect);
            if (pq.getQuery() == null)
                return Optional.empty();
            ResultSet rs = executePreparedQuery(pq, conn);
            return Optional.ofNullable(rowMapper.map(rs, resultClass));
        });
    }

    /**
     * Executes a raw SQL select query with a single row result and maps
     * only the direct fields of the result objects (no relations).
     * <p>
     * All field aliases must be in a dot-separated relation path format.
     * <p>
     * Used for mapping {@link raf.thesis.metadata.annotations.PDO} objects
     *
     * @param query       SQL query string in required format
     * @param resultClass resulting {@link raf.thesis.metadata.annotations.PDO} marked class
     * @return mapped list of {@link raf.thesis.metadata.annotations.PDO} objects
     */
    public <T> Optional<T> executeSingleRowPDOSelect(String query, Class<T> resultClass) throws SQLException {
        return runBody(conn -> {
            Optional<T> result;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                result = Optional.ofNullable(rowMapper.map(rs, resultClass));
            }
            return result;
        });
    }

    /**
     * Persists a new entity instance and returns it populated with
     * generated key values.
     * <p>
     * New entity instance is persisted with all relations if related
     * entity objects are already in the DB, otherwise it will fail the
     * foreign key constraint.
     *
     * @param obj entity to insert
     * @return inserted entity with generated primary keys
     */
    public <T> T insert(T obj) throws SQLException {
        return runBody((conn -> {
            PreparedStatementQuery mainInsert = DBUpdateSolver.generateInsert(obj);
            ResultSet rs;

            //databases that doesn't support generatedKeys() with given column labels
            if (dialect instanceof Dialect.UsesInsertReturning)
                rs = insertReturning(conn, mainInsert);
                //normal ones
            else
                rs = insertAndGetKeys(conn, mainInsert, obj);

            rs.next();
            T keysObject = rowMapper.map(rs, obj);

            //solve relationships with foreign keys that are not in the object
            List<PreparedStatementQuery> queries = DBUpdateSolver.generateRelationshipUpdateQueries(keysObject);

            //solve many-to-many relationships
            queries.addAll(DBUpdateSolver.generateManyToManyInserts(keysObject));

            //go in reverse as last element in list is the main insert, others are many to many inserts
            for (int k = queries.size() - 1; k >= 0; k--) {
                PreparedStatementQuery pq = queries.get(k);
                PreparedStatement ps = conn.prepareStatement(pq.getQuery());
                for (int i = 1; i <= pq.getArguments().size(); i++) {
                    bindLiteral(ps, i, pq.getArguments().get(i - 1));
                }
                ps.executeUpdate();
            }
            return keysObject;
        }));
    }

    /**
     * method for filling the generated keys via jdbc getGeneratedKeys()
     */
    private ResultSet insertAndGetKeys(Connection conn, PreparedStatementQuery mainInsert, Object obj) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement(mainInsert.getQuery(), extractKeys(obj));
        for (int i = 1; i <= mainInsert.getArguments().size(); i++) {
            bindLiteral(preparedStatement, i, mainInsert.getArguments().get(i - 1));
        }
        preparedStatement.executeUpdate();
        return preparedStatement.getGeneratedKeys();
    }

    /**
     * method for filling the generated keys via the RETURNING keyword in SQL query
     */
    private ResultSet insertReturning(Connection conn, PreparedStatementQuery mainInsert) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement(mainInsert.getQuery());
        for (int i = 1; i <= mainInsert.getArguments().size(); i++) {
            bindLiteral(preparedStatement, i, mainInsert.getArguments().get(i - 1));
        }
        return preparedStatement.executeQuery();
    }

    /**
     * Helper function for executing the {@link PreparedStatementQuery} queries on given connection
     */
    private ResultSet executePreparedQuery(PreparedStatementQuery pq, Connection conn) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement(pq.getQuery());
        for (int i = 1; i <= pq.getArguments().size(); i++) {
            bindLiteral(preparedStatement, i, pq.getArguments().get(i - 1));
        }
        return preparedStatement.executeQuery();
    }

    /**
     * Updates an existing entity instance.
     * <p>
     * Updates only direct fields of the entity (no relations)
     *
     * @param obj entity to update
     */
    public void update(Object obj) throws SQLException {
        PreparedStatementQuery update = DBUpdateSolver.updateObject(obj, false);
        executeUpdateStatement(update);
    }

    /**
     * Updates an existing entity instance with ignoring null-valued fields.
     * <p>
     * Updates only direct fields of the entity (no relations)
     *
     * @param obj entity to update
     */
    public void update(Object obj, IgnoreNull ignoreNull) throws SQLException {
        PreparedStatementQuery update = DBUpdateSolver.updateObject(obj, true);
        executeUpdateStatement(update);
    }

    /**
     * Persists a new entity instance if there is no object with the same
     * primary key in DB table.
     * If there is one, updates its column values and relations.
     * <p>
     * Entity instance is persisted with all relations if related
     * entity objects are already in the DB, otherwise it will fail the
     * foreign key constraint.
     * <p>
     * Update will ignore null-valued relations, old values will remain the same.
     *
     * @param obj entity to insert or update if existent
     */
    public void upsert(Object obj) throws SQLException {
        runBody(conn -> {
            List<PreparedStatementQuery> upsert = DBUpdateSolver.upsertObject(obj);
            for (var update : upsert) {
                PreparedStatement preparedStatement = conn.prepareStatement(update.getQuery());
                for (int i = 1; i <= update.getArguments().size(); i++) {
                    bindLiteral(preparedStatement, i, update.getArguments().get(i - 1));
                }
                preparedStatement.executeUpdate();
            }
            return null;
        });
    }

    /**
     * Deletes an entity instance from the DB.
     *
     * @param obj entity to delete
     */
    public void delete(Object obj) throws SQLException {
        PreparedStatementQuery delete = DBUpdateSolver.deleteObject(obj);
        executeUpdateStatement(delete);
    }

    /**
     * Creates a relationship between two entity instances .
     * Fills the relation with given relation name.
     * Works for all relation types.
     *
     * @param obj1         first entity
     * @param obj2         second entity
     * @param relationName relation name in the first entity
     */
    public void connectRows(Object obj1, Object obj2, String relationName) throws SQLException {
        PreparedStatementQuery connect = DBUpdateSolver.connect(obj1, obj2, relationName);
        executeUpdateStatement(connect);
    }

    /**
     * Removes a relationship represented by a foreign key held by {@code obj1}.
     * <p>
     * Relationship is removed by placing NULL in foreign key column.
     *
     * @param obj1         owning entity
     * @param relationName relation name
     */
    //only for MANY-TO-ONE and ONE-TO-ONE with containsFK = true relations
    public void disconnectRow(Object obj1, String relationName) throws SQLException {
        PreparedStatementQuery disconnect = DBUpdateSolver.disconnect(obj1, null, relationName);
        executeUpdateStatement(disconnect);
    }

    /**
     * Removes a relationship between two entity instances.
     * <p>
     * Relationship is removed by placing {@code NULL} in the foreign key column,
     * or by removing a row in joined table for {@link raf.thesis.metadata.annotations.ManyToMany} relations
     *
     * @param obj1         first entity
     * @param obj2         second entity
     * @param relationName relation name
     */
    //only for MANY-TO-MANY, ONE-TO-MANY and ONE-TO-ONE with containsFK = false relations
    public void disconnectRows(Object obj1, Object obj2, String relationName) throws SQLException {
        PreparedStatementQuery disconnect = DBUpdateSolver.disconnect(obj1, obj2, relationName);
        executeUpdateStatement(disconnect);
    }

    /**
     * Helper function for executing update prepared statements
     */
    private void executeUpdateStatement(PreparedStatementQuery update) throws SQLException {
        runBody(conn -> {
            PreparedStatement preparedStatement = conn.prepareStatement(update.getQuery());
            for (int i = 1; i <= update.getArguments().size(); i++) {
                bindLiteral(preparedStatement, i, update.getArguments().get(i - 1));
            }
            preparedStatement.executeUpdate();
            return null;
        });
    }

    /**
     * Helper function to extract primary key names from entity
     */
    private String[] extractKeys(Object obj) {
        EntityMetadata metadata = MetadataStorage.get(obj.getClass());
        if (metadata == null)
            throw new EntityObjectRequiredException("Given object: " + obj.getClass().getName() + " is not an entity");

        String[] keys = new String[metadata.getIdFields().size()];
        int i = 0;
        for (var column : metadata.getColumns().values()) {
            if (metadata.getIdFields().contains(column.getField())) {
                keys[i++] = column.getColumnName();
            }
        }
        return keys;
    }

    /**
     * Helper function to fill prepared statement jokers with right arguments
     */
    private void bindLiteral(PreparedStatement ps, int idx, Literal lit) throws SQLException {
        switch (lit) {
            case Literal.DoubleCnst d -> ps.setDouble(idx, d.x());
            case Literal.LongCnst l -> ps.setLong(idx, l.x());
            case Literal.StringCnst s -> ps.setString(idx, s.x());
            case Literal.BoolCnst b -> ps.setBoolean(idx, b.x());
            case Literal.DateCnst d -> ps.setDate(idx, java.sql.Date.valueOf(d.x()));
            case Literal.DateTimeCnst dt -> ps.setTimestamp(idx, java.sql.Timestamp.valueOf(dt.x()));
            case Literal.TimeCnst t -> ps.setTime(idx, java.sql.Time.valueOf(t.x()));
            case Literal.NullCnst _ -> ps.setNull(idx, java.sql.Types.NULL);

            default -> throw new IllegalArgumentException("Unsupported literal: " + lit.getClass());
        }
    }

    /**
     * Marker object indicating that null-valued fields should be ignored
     * during update generation.
     */
    private static class IgnoreNull {
        public String toString() {
            return "IGNORE NULL";
        }
    }

    public static final IgnoreNull IGNORE_NULL = new IgnoreNull();


    //transaction management

    /**
     * Executes the given body within a transaction boundary.
     * <p>
     * The transaction is committed on successful completion and rolled
     * back on failure.
     *
     * @param body transactional body
     * @return body result
     */
    public <T> T transaction(SQLValuedTransactionBody<T> body) throws SQLException {
        return withConnection(connection -> {
            connection.setAutoCommit(false);
            var committed = false;
            try {
                var ret = body.execute(connection);
                committed = true;
                connection.commit();
                return ret;
            } finally {
                if (!committed) connection.rollback();
            }
        });
    }

    /**
     * Executes the given body within a transaction boundary.
     * <p>
     * The transaction is committed on successful completion and rolled
     * back on failure.
     *
     * @param body transactional body
     */
    public void transaction(SQLTransactionBody body) throws SQLException {
        transaction(conn -> {
            body.execute(conn);
            return null;
        });
    }

    /**
     * Executes the given body using only the provided connection.
     *
     * @param connection body execution connection
     * @param body       body to execute
     * @return body result
     */
    public <T> T withConnection(Connection connection, SQLValuedTransactionBody<T> body) throws SQLException {
        Objects.requireNonNull(connection);
        if (activeConnection.get() != null)
            throw new IllegalStateException("Cannot nest withConnection or transaction constructs");
        try {
            activeConnection.set(connection);
            return body.execute(connection);
        } finally {
            activeConnection.remove();
        }
    }

    /**
     * Executes the given body using only the provided connection.
     *
     * @param connection body execution connection
     * @param body       body to execute
     */
    public void withConnection(Connection connection, SQLTransactionBody body) throws SQLException {
        withConnection(connection, conn -> {
            body.execute(conn);
            return null;
        });
    }

    /**
     * Executes the given body using only one {@link ConnectionSupplier} connection.
     *
     * @param body body to execute
     * @return body result
     */
    public <T> T withConnection(SQLValuedTransactionBody<T> body) throws SQLException {
        try (var connection = connectionSupplier.getConnection()) {
            return withConnection(connection, body);
        }
    }

    /**
     * Executes the given body using only one {@link ConnectionSupplier} connection.
     *
     * @param body body to execute
     */
    public void withConnection(SQLTransactionBody body) throws SQLException {
        withConnection(conn -> {
            body.execute(conn);
            return null;
        });
    }
}
