package layering;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raf.thesis.metadata.annotations.Column;
import raf.thesis.metadata.annotations.Entity;
import raf.thesis.metadata.annotations.Id;
import raf.thesis.metadata.annotations.ManyToMany;

import java.util.List;

@Entity(tableName = "projects")
@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class Project {
    @Id
    @Column(columnName = "project_id")
    private int projectId;
    @Column(columnName = "project_name")
    private String projectName;
    @ManyToMany(joinedTableName = "employee_projects")
    List<Employee> employees;

    public Project(int projectId, String projectName) {
        this.projectId = projectId;
        this.projectName = projectName;
    }
}
