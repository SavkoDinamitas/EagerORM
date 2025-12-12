import layering.Department;
import layering.Employee;
import layering.Project;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import raf.thesis.Session;
import raf.thesis.query.QueryBuilder;
import util.HrScheme;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static raf.thesis.query.ConditionBuilder.*;

public class SessionTest {
    private static Session session;

    @BeforeAll
    public static void setUp() throws SQLException {
        session = new Session(() -> DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", ""), "layering");
    }

    @BeforeEach
    void cleanDb() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        try (Statement stmt = conn.createStatement()) {
            //noinspection SqlSourceToSinkFlow
            stmt.execute(HrScheme.SCRIPT);
        }
    }

    @Test
    void testSimpleInsert() throws SQLException {
        Employee me = new Employee(105, "Salko", "Dinamitas", LocalDate.of(2002, 10, 10));
        session.insert(me);
        List<Employee> employees = session.executeSelect(QueryBuilder.select(Employee.class).where(field("employee_id").eq(lit(105))), Employee.class);
        assertFalse(employees.isEmpty());
        assertThat(employees.getFirst()).usingRecursiveComparison().isEqualTo(me);
    }

    @Test
    void testInsertWithManyToOneRelations() throws SQLException {
        Employee Steven = new Employee(100, "Steven", "King", LocalDate.of(2003, 6, 17));
        Department Marketing = new Department(20, "Marketing");
        Employee me = new Employee(105, "Salko", "Dinamitas", LocalDate.of(2002, 10, 10));
        me.setManager(Steven);
        me.setDepartment(Marketing);
        session.insert(me);
        QueryBuilder qb = QueryBuilder.select(Employee.class)
                .join("manager")
                .join("department")
                .where(field("employee_id").eq(lit(105)));
        List<Employee> employees = session.executeSelect(qb, Employee.class);
        assertThat(employees.getFirst()).usingRecursiveComparison().isEqualTo(me);
    }

    @Test
    void testInsertWithManyToManyRelations() throws SQLException {
        Employee Steven = new Employee(100, "Steven", "King", LocalDate.of(2003, 6, 17));
        Department Marketing = new Department(20, "Marketing");
        Project Hr = new Project(1, "HR Onboarding System");
        Project Payrol = new Project(2, "Internal Payroll Platform");
        Employee me = new Employee(105, "Salko", "Dinamitas", LocalDate.of(2002, 10, 10));
        me.setManager(Steven);
        me.setDepartment(Marketing);
        me.setProjects(List.of(Hr, Payrol));
        session.insert(me);
        QueryBuilder qb = QueryBuilder.select(Employee.class)
                .join("manager")
                .join("department")
                .join("projects")
                .where(field("employee_id").eq(lit(105)));
        List<Employee> employees = session.executeSelect(qb, Employee.class);
        assertThat(employees.getFirst()).usingRecursiveComparison().isEqualTo(me);
    }

    @Test
    void testInsertWithGeneratedPK() throws SQLException {
        Project myProject = new Project();
        myProject.setProjectName("myProject");
        Project p = session.insert(myProject);
        assertEquals(p.getProjectId(), 6);
    }

    @Test
    void testInsertWithGeneratedPKAndManyToManyRelation() throws SQLException {
        Project myProject = new Project();
        myProject.setProjectName("myProject");
        Employee Steven = new Employee(100, "Steven", "King", LocalDate.of(2003, 6, 17));
        myProject.setEmployees(List.of(Steven));
        myProject = session.insert(myProject);
        QueryBuilder qb = QueryBuilder.select(Project.class)
                .join("employees")
                .where(field("project_id").eq(lit(6)));
        List<Project> projects = session.executeSelect(qb, Project.class);
        assertThat(projects.getFirst()).usingRecursiveComparison().isEqualTo(myProject);
    }
}
