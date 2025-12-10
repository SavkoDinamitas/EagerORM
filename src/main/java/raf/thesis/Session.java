package raf.thesis;

import lombok.NonNull;
import raf.thesis.mapper.DefaultMapperImplementation;
import raf.thesis.mapper.RowMapper;
import raf.thesis.metadata.scan.MetadataScanner;
import raf.thesis.query.InsertSolver;
import raf.thesis.query.PreparedStatementQuery;
import raf.thesis.query.QueryBuilder;
import raf.thesis.query.dialect.ANSISQLDialect;
import raf.thesis.query.dialect.Dialect;
import raf.thesis.query.tree.Literal;

import java.sql.*;
import java.util.List;
import java.util.Optional;


public class Session {
    private final ConnectionSupplier connectionSupplier;
    private final RowMapper rowMapper = new DefaultMapperImplementation();
    private final Dialect dialect = new ANSISQLDialect();
    private final InsertSolver insertSolver = new InsertSolver(dialect);
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
        List<PreparedStatementQuery> queries = insertSolver.generateInsert(obj);
        Connection conn = connectionSupplier.getConnection();
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

}
