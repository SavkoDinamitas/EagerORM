package discovery.test5;

import raf.thesis.metadata.annotations.Entity;
import raf.thesis.metadata.annotations.Id;
import raf.thesis.metadata.annotations.OneToMany;

import java.util.List;

@Entity(tableName = "planes")
public class Plane {
    @Id
    private int id;
    @OneToMany
    private List<Integer> seats;
}
