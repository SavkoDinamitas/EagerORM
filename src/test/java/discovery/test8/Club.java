package discovery.test8;

import raf.thesis.metadata.internal.ColumnMetadata;
import raf.thesis.metadata.internal.EntityMetadata;
import raf.thesis.metadata.internal.RelationMetadata;
import raf.thesis.metadata.internal.RelationType;
import raf.thesis.metadata.annotations.Entity;
import raf.thesis.metadata.annotations.Id;
import raf.thesis.metadata.annotations.OneToMany;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity(tableName = "clubs")
public class Club {
    @Id
    private int id;
    private String name;
    @OneToMany
    private List<Player> players;

    public static EntityMetadata getMetadata() throws NoSuchFieldException {
        Map<String, ColumnMetadata> cols = new HashMap<>();
        cols.put("id", new ColumnMetadata("id", Club.class.getDeclaredField("id")));
        cols.put("name", new ColumnMetadata("name", Club.class.getDeclaredField("name")));
        Map<String, RelationMetadata> rels = new HashMap<>();
        rels.put("players", new RelationMetadata(Club.class.getDeclaredField("players"), "players", RelationType.ONE_TO_MANY, Player.class, List.of("id"), null, null, null));
        return new EntityMetadata("clubs", Club.class, List.of(Club.class.getDeclaredField("id")), cols, rels, List.of(false));
    }
}
