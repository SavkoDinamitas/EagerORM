package layering;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raf.thesis.metadata.annotations.Entity;
import raf.thesis.metadata.annotations.Id;

@Entity(tableName = "item_codes")
@AllArgsConstructor@NoArgsConstructor@Getter@Setter
public class ItemCode {
    @Id
    private String code_prefix;
    @Id
    private int code_number;
    private String description;
}
