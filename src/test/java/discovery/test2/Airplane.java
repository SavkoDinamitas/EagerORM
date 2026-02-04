/*
 * EagerORM - A predictable object-relation mapper
 * Copyright (C) 2026  Dimitrije Andžić <dimitrije.andzic@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity(tableName = "airplanes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Airplane {
    @Id
    int id;
    String name;
    @ManyToMany(joinedTableName = "airplanes_flights")
    private List<Flight> flights;

    public static EntityMetadata getMetadata() throws NoSuchFieldException {
        Map<String, ColumnMetadata> cols = new HashMap<>();
        cols.put("id", new ColumnMetadata("id", Airplane.class.getDeclaredField("id")));
        cols.put("name", new ColumnMetadata("name", Airplane.class.getDeclaredField("name")));
        Map<String, RelationMetadata> rel = new HashMap<>();
        rel.put("flights", new RelationMetadata(Airplane.class.getDeclaredField("flights"), "flights", RelationType.MANY_TO_MANY, Flight.class, List.of("flightnumber"), "airplanes_flights", List.of("id"), null));
        return new EntityMetadata("airplanes", Airplane.class, List.of(Airplane.class.getDeclaredField("id")), cols, rel, List.of(false));
    }
}
