package raf.thesis;

import raf.thesis.mapper.DefaultMapperImplementation;
import raf.thesis.mapper.RowMapper;
import raf.thesis.metadata.EntityMetadata;
import raf.thesis.metadata.scan.MetadataScanner;
import raf.thesis.metadata.storage.MetadataStorage;
import raf.thesis.query.ConditionBuilder;
import raf.thesis.query.DBUpdateSolver;
import raf.thesis.query.PreparedStatementQuery;
import raf.thesis.query.QueryBuilder;
import raf.thesis.query.dialect.ANSISQLDialect;
import raf.thesis.query.dialect.Dialect;
import raf.thesis.query.exceptions.EntityObjectRequiredForInsertionException;
import raf.thesis.query.tree.Literal;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("ClassEscapesDefinedScope")
public class Session {
    private final ConnectionSupplier connectionSupplier;
    private final RowMapper rowMapper = new DefaultMapperImplementation();
    private final Dialect dialect = new ANSISQLDialect();
    private final DBUpdateSolver DBUpdateSolver = new DBUpdateSolver(dialect);
    private static final MetadataScanner metadataScanner = new MetadataScanner();

    public Session(ConnectionSupplier connectionSupplier, String... scanPackages) {
        this.connectionSupplier = connectionSupplier;
        metadataScanner.discoverMetadata(scanPackages);
    }

    public <T> List<T> executeSelect(QueryBuilder queryBuilder, Class<T> resultClass) throws SQLException {
        String sql = queryBuilder.build();
        if(sql == null)
            return null;
        return executeSelect(sql, resultClass);
    }

    public <T> List<T> executeSelect(String query, Class<T> resultClass) throws SQLException {
        Connection conn = connectionSupplier.getConnection();
        List<T> result;
        try(Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {
            result = rowMapper.mapWithRelations(rs, resultClass);
        }
        conn.close();
        return result;
    }

    public <T> List<T> executePDOSelect(QueryBuilder queryBuilder, Class<T> resultClass) throws SQLException {
        String sql = queryBuilder.build();
        if(sql == null)
            return null;
        return executePDOSelect(sql, resultClass);
    }

    public <T> List<T> executePDOSelect(String query, Class<T> resultClass) throws SQLException {
        Connection conn = connectionSupplier.getConnection();
        List<T> result;
        try(Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {
            result = rowMapper.mapList(rs, resultClass);
        }
        conn.close();
        return result;
    }

    public <T> Optional<T> executeSingleRowPDOSelect(QueryBuilder queryBuilder, Class<T> resultClass) throws SQLException {
        String sql = queryBuilder.build();
        if(sql == null)
            return Optional.empty();
        return executeSingleRowPDOSelect(sql, resultClass);
    }

    public <T> Optional<T> executeSingleRowPDOSelect(String query, Class<T> resultClass) throws SQLException {
        Connection conn = connectionSupplier.getConnection();
        Optional<T> result;
        try(Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {
            result = Optional.ofNullable(rowMapper.map(rs, resultClass));
        }
        conn.close();
        return result;
    }

    public void insert(Object obj) throws SQLException {
        Connection conn = connectionSupplier.getConnection();

        PreparedStatementQuery mainInsert = DBUpdateSolver.generateInsert(obj);
        PreparedStatement preparedStatement = conn.prepareStatement(mainInsert.getQuery(), extractKeys(obj));
        for(int i = 1; i <= mainInsert.getArguments().size(); i++){
            bindLiteral(preparedStatement, i, mainInsert.getArguments().get(i - 1));
        }
        preparedStatement.executeUpdate();
        ResultSet rs = preparedStatement.getGeneratedKeys();
        Object keysObject = rowMapper.map(rs, obj);

        //solve many-to-many relationships
        List<PreparedStatementQuery> queries = DBUpdateSolver.generateManyToManyInserts(keysObject);

        //go in reverse as last element in list is the main insert, others are many to many inserts
        for(int k = queries.size() - 1; k >= 0; k--) {
            PreparedStatementQuery pq = queries.get(k);
            PreparedStatement ps = conn.prepareStatement(pq.getQuery());
            for(int i = 1; i <= pq.getArguments().size(); i++){
                bindLiteral(ps, i, pq.getArguments().get(i - 1));
            }
            ps.executeUpdate();
        }
        conn.close();
    }

    public void update(Object obj) throws SQLException{
        PreparedStatementQuery update = DBUpdateSolver.updateObject(obj, false);
        executeUpdateStatement(update);
    }

    public void update(Object obj, IgnoreNull ignoreNull) throws SQLException{
        PreparedStatementQuery update = DBUpdateSolver.updateObject(obj, true);
        executeUpdateStatement(update);
    }

    private void executeUpdateStatement(PreparedStatementQuery update) throws SQLException{
        Connection conn = connectionSupplier.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(update.getQuery());
        for(int i = 1; i <= update.getArguments().size(); i++){
            bindLiteral(preparedStatement, i, update.getArguments().get(i - 1));
        }
        preparedStatement.executeUpdate();
        conn.close();
    }

    private String[] extractKeys(Object obj){
        EntityMetadata metadata = MetadataStorage.get(obj.getClass());
        if(metadata == null)
            throw new EntityObjectRequiredForInsertionException("Given object: " + obj.getClass().getName() + " is not an entity");

        String[] keys = new String[metadata.getIdFields().size()];
        int i = 0;
        for(var column : metadata.getColumns().values()){
            if(metadata.getIdFields().contains(column.getField())){
                keys[i++] = column.getColumnName();
            }
        }
        return keys;
    }

    private void bindLiteral(PreparedStatement ps, int idx, Literal lit) throws SQLException {
        switch (lit) {
            case Literal.DoubleCnst d -> ps.setDouble(idx, d.x());
            case Literal.LongCnst l -> ps.setLong(idx, l.x());
            case Literal.StringCnst s -> ps.setString(idx, s.x());
            case Literal.BoolCnst b -> ps.setBoolean(idx, b.x());
            case Literal.DateCnst d -> ps.setDate(idx, java.sql.Date.valueOf(d.x()));
            case Literal.DateTimeCnst dt -> ps.setTimestamp(idx, java.sql.Timestamp.valueOf(dt.x()));
            case Literal.TimeCnst t -> ps.setTime(idx, java.sql.Time.valueOf(t.x()));
            case Literal.NullCnst n -> ps.setNull(idx, java.sql.Types.NULL);

            default -> throw new IllegalArgumentException("Unsupported literal: " + lit.getClass());
        }
    }

    private static class IgnoreNull { public String toString() { return "IGNORE NULL"; } }
    public static final IgnoreNull IGNORE_NULL = new IgnoreNull();
}
