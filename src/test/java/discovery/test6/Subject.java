package discovery.test6;

import raf.thesis.metadata.annotations.Entity;
import raf.thesis.metadata.annotations.Id;

@Entity(tableName = "subjects")
public class Subject {
    @Id
    private int id;
    private String name;
}
