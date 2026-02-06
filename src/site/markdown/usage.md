# Usage
Add EagerORM dependency to your project:
## Maven:
```xml
<dependency>
    <groupId>io.github.savkodinamitas</groupId>
    <artifactId>EagerORM</artifactId>
    <version>1.0.0</version>
</dependency>
```
## Gradle
```
implementation 'io.github.savkodinamitas:EagerORM:1.0.0'
```
For Module users, inside `module-info.java` add:
```java
requires EagerORM;
open package_names;
```
where `package_names` are packages that contain entity marked classes.
This is needed for metadata scan to work.