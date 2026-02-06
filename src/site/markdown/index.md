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
