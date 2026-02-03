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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import io.github.savkodinamitas.metadata.internal.ColumnMetadata;
import io.github.savkodinamitas.metadata.internal.EntityMetadata;
import io.github.savkodinamitas.metadata.internal.RelationMetadata;
import io.github.savkodinamitas.metadata.internal.RelationType;
import io.github.savkodinamitas.metadata.annotations.Entity;
import io.github.savkodinamitas.metadata.annotations.Id;
import io.github.savkodinamitas.metadata.annotations.ManyToMany;
import io.github.savkodinamitas.metadata.annotations.ManyToOne;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(tableName = "flights")
public class Flight {
    @Id
    private String flightNumber;
    private String flightType;
    @ManyToOne
    private Crew crew;
    @ManyToMany(joinedTableName = "airplanes_flights")
    private List<Airplane> airplanes;

    public static EntityMetadata getMetadata() throws NoSuchFieldException {
        Map<String, ColumnMetadata> cols = new HashMap<>();
        cols.put("flightnumber", new ColumnMetadata("flightnumber", Flight.class.getDeclaredField("flightNumber")));
        cols.put("flighttype", new ColumnMetadata("flighttype", Flight.class.getDeclaredField("flightType")));
        Map<String, RelationMetadata> rel = new HashMap<>();
        rel.put("crew", new RelationMetadata(Flight.class.getDeclaredField("crew"), "crew", RelationType.MANY_TO_ONE, Crew.class, List.of("crewid"), null, null, null));
        rel.put("airplanes", new RelationMetadata(Flight.class.getDeclaredField("airplanes"), "airplanes", RelationType.MANY_TO_MANY, Airplane.class, List.of("id"), "airplanes_flights", List.of("flightnumber"), null));
        return new EntityMetadata("flights", Flight.class, List.of(Flight.class.getDeclaredField("flightNumber")), cols, rel, List.of(false));
    }
}
