package util.multidb;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.testcontainers.mariadb.MariaDBContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import util.HrScheme;

import java.sql.DriverManager;
import java.util.List;

public class DBHarness implements AutoCloseable{
    private final PostgreSQLContainer PSQLContainer = new PostgreSQLContainer("postgres:18");
    private final MariaDBContainer mariaDBContainer = new MariaDBContainer("mariadb:12");
    @Getter
    private final List<DbUnderTest> dbs;

    public DBHarness(){
        PSQLContainer.start();
        mariaDBContainer.start();
        dbs = List.of(
                hikari(
                        "H2",
                        "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
                        "sa",
                        "",
                        HrScheme.SCRIPT
                ),
                new DbUnderTest("H2", () -> DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", ""), () -> {}, HrScheme.SCRIPT),
                hikari(
                        "Postgres",
                        PSQLContainer.getJdbcUrl(),
                        PSQLContainer.getUsername(),
                        PSQLContainer.getPassword(),
                        HrScheme.PSQLScript
                ),
                new DbUnderTest("Postgres", () -> PSQLContainer.createConnection(""), () -> {}, HrScheme.PSQLScript),
                hikari(
                        "MariaDB",
                        mariaDBContainer.getJdbcUrl() + "?allowMultiQueries=true",
                        mariaDBContainer.getUsername(),
                        mariaDBContainer.getPassword(),
                        HrScheme.MARIADBSCRIPT
                ),
                new DbUnderTest("MariaDb", () -> mariaDBContainer.createConnection("?allowMultiQueries=true"), () -> {}, HrScheme.MARIADBSCRIPT)
        );
    }

    @Override
    public void close() throws Exception {
        for(DbUnderTest db : dbs){
            db.closeFunction().run();
        }
        PSQLContainer.close();
        mariaDBContainer.close();
    }

    private DbUnderTest hikari(
            String dbName, String url, String user, String pass, String initScript
    ) {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(url);
        cfg.setUsername(user);
        cfg.setPassword(pass);
        cfg.setMaximumPoolSize(4);
        var dataSource = new HikariDataSource(cfg);
        return new DbUnderTest(dbName + " with hikaricp", dataSource::getConnection, dataSource::close, initScript);
    }

}
