package discovery.test3;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raf.thesis.metadata.annotations.Entity;

@Entity(tableName = "dummies")
@Getter@Setter@AllArgsConstructor@NoArgsConstructor
public class Dummy {
    private int id;
    private String name;
}
