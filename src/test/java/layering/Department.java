package layering;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raf.thesis.metadata.annotations.Column;
import raf.thesis.metadata.annotations.Entity;
import raf.thesis.metadata.annotations.Id;
import raf.thesis.metadata.annotations.OneToMany;

import java.util.List;

@Entity(tableName = "departments")
@NoArgsConstructor@Getter@Setter
public class Department {
    @Id
    @Column(columnName = "department_id")
    private int departmentId;
    @Column(columnName = "department_name")
    private String departmentName;
    @OneToMany
    private List<Employee> employees;

    public Department(int departmentId, String departmentName) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
    }
}
