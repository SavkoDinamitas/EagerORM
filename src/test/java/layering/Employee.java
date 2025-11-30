package layering;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raf.thesis.metadata.annotations.Column;
import raf.thesis.metadata.annotations.Entity;
import raf.thesis.metadata.annotations.Id;

@Entity(tableName = "employees")
@Getter
@Setter
@NoArgsConstructor
public class Employee {
    @Id
    @Column(columnName = "employee_id")
    private int employeeId;
    @Column(columnName = "first_name")
    private String firstName;
    @Column(columnName = "last_name")
    private String lastName;
}
