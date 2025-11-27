package discovery.test7;

import raf.thesis.metadata.annotations.Entity;
import raf.thesis.metadata.annotations.Id;
import raf.thesis.metadata.annotations.OneToMany;

import java.util.List;

@Entity(tableName = "professors")
public class Professor {
    @Id
    private int id;
    private String name;
    @OneToMany()
    private List<Subject> subjects;
    @OneToMany(relationName = "subjects")
    private List<Subject> old_subjects;
}
