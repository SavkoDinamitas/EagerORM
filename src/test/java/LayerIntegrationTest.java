import layering.Department;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import raf.thesis.mapper.DefaultMapperImplementation;
import raf.thesis.mapper.RowMapper;
import raf.thesis.metadata.scan.MetadataScanner;
import raf.thesis.query.QueryBuilder;
import util.HrScheme;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LayerIntegrationTest {
    private static Connection conn;
    private static final RowMapper rowMapper = new DefaultMapperImplementation();
    @BeforeAll
    static void setupDatabase() throws SQLException, NoSuchFieldException {
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        try (Statement stmt = conn.createStatement()) {
            //noinspection SqlSourceToSinkFlow
            stmt.execute(HrScheme.SCRIPT);
        }
        MetadataScanner ms = new MetadataScanner();
        ms.discoverMetadata("layering");
    }

    @AfterAll
    static void closeDatabase() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    @Test
    void simpleJoinIntegrationTest() throws SQLException {
        String query = QueryBuilder.select(Department.class).join("employees").build();
        System.out.println(query);
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            List<Department> departments = rowMapper.mapWithRelations(rs, Department.class);
            assertFalse(departments.isEmpty());
        }
    }
}
