package layering;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raf.thesis.metadata.annotations.Column;
import raf.thesis.metadata.annotations.Entity;
import raf.thesis.metadata.annotations.Id;
import raf.thesis.metadata.annotations.ManyToMany;

import java.time.LocalDate;
import java.util.List;

@Entity(tableName = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    @Column(columnName = "employee_id")
    private int employeeId;
    @Column(columnName = "first_name")
    private String firstName;
    @Column(columnName = "last_name")
    private String lastName;
    @Column(columnName = "hire_date")
    private LocalDate hireDate;
    @ManyToMany(joinedTableName = "employee_projects")
    private List<Project> projects;

    public Employee(int employeeId, String firstName, String lastName, LocalDate hireDate) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.hireDate = hireDate;
    }
}
