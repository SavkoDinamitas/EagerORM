# EagerORM

EagerORM is a lightweight object–relational mapper focused on explicit, predictable data access.
It maps plain Java objects to database tables using simple annotations and provides a fluent API for building SQL queries.
Unlike traditional ORMs, EagerORM does not use lazy loading or proxies. All relationships are loaded only when explicitly joined in a query.
This makes SQL execution and data fetching behavior visible in code and easy to reason about.

The library supports common relationship types (1:1, 1:N, N:1, N:M), CRUD operations, and dialect-aware SQL generation.
For non-entity queries such as aggregations or reports, EagerORM provides projection classes via Plain Data Objects `@PDO`.
Raw SQL can also be executed and mapped to entities or PDOs when needed. Raw SQL queries need to be in special format regarding the column aliases.
Transaction and connection scoping are supported to allow controlled unit-of-work style operations.

---

## Features

- Annotation-based entity mapping (`@Entity`, `@Column`, `@Id`)
- Relationship mapping (`@OneToOne`, `@OneToMany`, `@ManyToOne`, `@ManyToMany`)
- Explicit eager loading via `join(...)` (no lazy loading)
- Fluent SQL query builder with conditions, joins, grouping, and ordering
- Subquery and expression support
- CRUD operations and upsert
- Plain Data Object (PDO) projections for custom result sets
- Relationship management API (connect/disconnect)
- Raw SQL execution with entity or PDO mapping
- Dialect-aware SQL generation (ANSI, H2, PostgreSQL, MariaDB/MySQL, MSSQL)
- Transaction and connection scoping


---

## Import library
Add EagerORM dependency to your project:
```
# Maven
<dependency>
    <groupId>io.github.savkodinamitas</groupId>
    <artifactId>EagerORM</artifactId>
    <version>1.0.0</version>
</dependency>

# Gradle
implementation 'io.github.savkodinamitas:EagerORM:1.0.0'
```

---
## Annotate Entities and Plain Data Objects
Use `@Entity` to mark classes mapped to entities and `@PDO` to mark classes mapped to results of specific queries that don't return entities.
Entity and PDO classes must have getters and setters in order for mapping to work (standard POJO classes).
Mark primary key fields of entities with `@Id`. Primary keys can also be marked as database generated.
If field name is not the same as the column name in table, use `@Column` to map correct name.
To define field as relation, depending on the type, use `@OneToOne`, `@OneToMany`, `@ManyToOne` or `@ManyToMany`.
Here is an example of annotated entity:

```java
@Entity(tableName = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id(generated = true)
    private Long id;

    @Column(columnName = "first_name")
    private String firstName;

    @Column(columnName = "last_name")
    private String lastName;

    @ManyToOne
    private Department department;

    @ManyToMany(joinedTableName = "employee_projects")
    private List<Project> projects;
}
```

## Create a `Session`
Create a Session instance to use ORM functionalities. Session constructor requires connection supplier and names of packages that contain entity classes.
Dialect detection is automated, but concrete one can be specified in the constructor.
Below are two examples of Session creation with different connection suppliers:
1. By using JDBC driver manager to supply connections to the Session:
```java
Session session = new Session(
    () -> DriverManager.getConnection(url, user, pass),
    "com.example.entities"
);
```

2. By using some datasource (in this case Hikari pooling) to supply connections to Session:
```java
HikariConfig cfg = new HikariConfig();
cfg.setJdbcUrl(url);
cfg.setUsername(user);
cfg.setPassword(pass);
cfg.setMaximumPoolSize(4);
var dataSource = new HikariDatasource(cfg);
Session session = new Session(
    dataSource::getConnection,
    new PostgreSQLDialect(),
    "com.example.entities"
);
```

---

## `CRUD` operations
EagerORM supports all CRUD operations on DB as well as UPSERT.

### `INSERT` object in DB
EagerORM supports relations in inserts if connected objects are already persisted in DB.
```java
Department d = new Department(1, "Security");
Employee me = new Employee("Salko", "Dinamitas", d);
session.insert(d);
Employee persisted = session.insert(e);
```

### `UPDATE` object in DB
EagerORM can ignore null-valued properties in updates.
```java
Employee me = new Employee();
me.setId(1);
me.setLastName("Smith");
session.update(employee, Session.IGNORE_NULL);
```

### `DELETE` object from DB
```java
session.delete(employee);
```

### `UPSERT` object in DB
Inserts object in DB if pk is not present. If it is present, updates object fields.
Same as insert, supports relations if connected object are present in DB.
```java
session.upsert(employee);
```

---
## `SELECT` queries
`SELECT` queries can be built using `QueryBuilder.select(...)` and extended fluently with joins, conditions, ordering, grouping, and pagination.
They can return either mapped entities or `@PDO` projection objects, depending on the target type.
Here are some examples:

### Basic Select
Get all employees from table:
```java
List<Employee> employees =
    session.executeSelect(
        QueryBuilder.select(Employee.class),
        Employee.class
    );
```

---

### Conditions (`WHERE`)
Get all employees with the first name = "John":
```java
import static io.github.savkodinamitas.api.ConditionBuilder.*;

List<Employee> employees =
    session.executeSelect(
        QueryBuilder
            .select(Employee.class)
            .where(field("firstName").eq(lit("John"))),
        Employee.class
    );
```

---

### Joins (Eager Loading)
Get all employees with their departments and projects:
```java
List<Employee> employees =
    session.executeSelect(
        QueryBuilder
            .select(Employee.class)
            .join("department")
            .join("projects"),
        Employee.class
    );
```

---

### Subqueries
Get all departments that have employees with salary greater than 5000:
```java
var sub =
    QueryBuilder.subQuery(
        Employee.class,
        field("department_id")
    )
    .where(field("salary").gt(lit(5000)))
    .distinct();

List<Department> depts =
    session.executeSelect(
        QueryBuilder
            .select(Department.class)
            .where(field("id").in(sub)),
        Department.class
    );
```

### `PDO` queries
If some queries don't return entities as result, they can be mapped on their corresponding Plain Data Object:
```java
@PDO
public class DeptStats {
    public String name;
    public long employeeCount;
}
```

```java
import static io.github.savkodinamitas.api.ConditionBuilder.*;

List<DeptStats> stats =
    session.executePDOSelect(
        QueryBuilder.select(
            Department.class,
            aliasedColumn(field("name"), "name"),
            aliasedColumn(count(field("employees.id")), "employeeCount")
        )
        .join("employees")
        .groupBy(field("name"))
        .having(count(field("employees.id")).gt(lit(5))),
        DeptStats.class
    );

```

---

### Raw SQL queries
In case of missing SQL functions, `Session` can execute raw SQL queries which column aliases are in specific format:
```java
List<Employee> list =
    session.executeSelect(
        """
        SELECT e.id AS "%root.id",
               e.first_name AS "%root.firstName",
               d.id AS "%root.department.id",
               d.name AS "%root.department.name"
        FROM employees e
        JOIN departments d ON e.department_id = d.id
        """,
        Employee.class
    );
```
All columns must be aliased to dot-separated relation paths from root object (which is the one in FROM clause) in order for relation mapping to work.
Each such path consists of dot-separated components in the following order:
1. The special `%root` component, indicating the object of the type passed to `executeSelect`,
2. Zero or more *relation names*, which allow descending into related objects, and
3. The field name this column populates.

For instance, the column aliased as `%root.department.id` will populate the `id` field of the `department` connected to each `Employee`.

---

## Transactions
EagerORM supports executing multiple methods inside one transaction, or on one connection.
If a body of a transaction throws an exception, transaction is rolled back and DB is returned to the state before the transaction beginning.
Example of transaction:
```java
Employee emp1 = new Employee("John", "Smith");
Employee emp2 = 
        session.transaction(conn -> {
            session.insert(empl1);
            Employee n = new Employee("Anna", "Brown");
            return session.insert(empl2);
        });
```
If more control over connection is wanted, there is `withConnection(...)` method:
```java
Employee emp1 = new Employee("John", "Smith");
Employee emp2 = 
        session.withConnection(conn -> {
            //change connection as wanted
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            session.insert(empl1);
            Employee n = new Employee("Anna", "Brown");
            return session.insert(empl2);
        });
```

---

## Notes

- Relations are loaded only if explicitly joined.
- No lazy loading, no proxies.
- PDO classes map only direct fields.
- SQL is generated explicitly.
- Dialect is auto-detected unless provided.

---

## License

GPL-3.0-or-later. See `COPYING` for details.

## Bug reports
Please report any bugs or unexpected behavior by opening an [issue](https://github.com/SavkoDinamitas/EagerORM/issues) in this repository. 
Include a clear description, steps to reproduce, and any relevant code or error messages.

