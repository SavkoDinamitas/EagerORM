package layering;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raf.thesis.metadata.annotations.Column;
import raf.thesis.metadata.annotations.Entity;
import raf.thesis.metadata.annotations.Id;
import raf.thesis.metadata.annotations.OneToOne;

@Entity(tableName = "performances")
@AllArgsConstructor@NoArgsConstructor@Getter@Setter
public class Performance {
    @Id
    @Column(columnName = "performance_id")
    private int performanceId;
    @Column(columnName = "performance_score")
    private double performanceScore;
    @OneToOne(containsFk = true)
    private Employee employee;

    public Performance(int performanceId, double performanceScore) {
        this.performanceId = performanceId;
        this.performanceScore = performanceScore;
    }
}
