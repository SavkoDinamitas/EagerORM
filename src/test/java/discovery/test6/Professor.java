package discovery.test6;

import raf.thesis.metadata.annotations.Entity;
import raf.thesis.metadata.annotations.Id;
import raf.thesis.metadata.annotations.OneToMany;

@Entity(tableName = "professors")
public class Professor {
    @Id
    private int id;
    @OneToMany
    Subject subject;
}
