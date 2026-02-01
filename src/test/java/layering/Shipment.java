package layering;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raf.thesis.metadata.annotations.Entity;
import raf.thesis.metadata.annotations.Id;

import java.time.LocalDate;

@Entity(tableName = "shipments")
@NoArgsConstructor@AllArgsConstructor@Getter@Setter
public class Shipment {
    @Id
    private String country_code;
    @Id
    private int shipment_no;
    private LocalDate shipped_at;
}
