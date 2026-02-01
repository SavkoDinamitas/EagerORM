package layering;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raf.thesis.metadata.annotations.Entity;
import raf.thesis.metadata.annotations.Id;
import raf.thesis.metadata.annotations.ManyToOne;

@Entity(tableName = "shipment_items")
@NoArgsConstructor@AllArgsConstructor@Getter@Setter
public class ShipmentItem {
    @Id
    private int id;
    @ManyToOne
    private Shipment shipment;
    @ManyToOne
    private ItemCode item;
    private int quantity;
}
