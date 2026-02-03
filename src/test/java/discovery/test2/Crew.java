/*
 * EagerORM - A predictable object-relation mapper
 * Copyright (C) 2026  Dimitrije Andžić <dimitrije.andzic@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package discovery.test2;

import io.github.savkodinamitas.metadata.annotations.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import io.github.savkodinamitas.metadata.internal.ColumnMetadata;
import io.github.savkodinamitas.metadata.internal.EntityMetadata;
import io.github.savkodinamitas.metadata.internal.RelationMetadata;
import io.github.savkodinamitas.metadata.internal.RelationType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity(tableName = "crews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Crew {
    @Id
    private int crewID;
    @Column(columnName = "crewNumber")
    private int crewSize;
    @OneToOne(containsFk = false)
    private Pilot pilot;
    @OneToMany(relationName = "finished_flights", foreignKey = "fk_flights")
    private List<Flight> flights;

    public static EntityMetadata getMetadata() throws NoSuchFieldException {
        Map<String, ColumnMetadata> cols = new HashMap<>();
        cols.put("crewid", new ColumnMetadata("crewid", Crew.class.getDeclaredField("crewID")));
        cols.put("crewnumber", new ColumnMetadata("crewnumber", Crew.class.getDeclaredField("crewSize")));
        Map<String, RelationMetadata> rel = new HashMap<>();
        rel.put("pilot", new RelationMetadata(Crew.class.getDeclaredField("pilot"), "pilot", RelationType.ONE_TO_ONE, Pilot.class, List.of("crewid"), null, null, null, false));
        rel.put("finished_flights", new RelationMetadata(Crew.class.getDeclaredField("flights"), "finished_flights", RelationType.ONE_TO_MANY, Flight.class, List.of("fk_flights"), null, null, null));
        return new EntityMetadata("crews", Crew.class, List.of(Crew.class.getDeclaredField("crewID")), cols, rel, List.of(false));
    }
}
