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
import raf.thesis.metadata.internal.ColumnMetadata;
import raf.thesis.metadata.internal.EntityMetadata;
import raf.thesis.metadata.annotations.Entity;
import raf.thesis.metadata.annotations.Id;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity(tableName = "pilots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pilot {
    @Id
    private int pilotId;
    private String pilotName;

    public static EntityMetadata getMetadata() throws NoSuchFieldException {
        Map<String, ColumnMetadata> cols = new HashMap<>();
        cols.put("pilotid", new ColumnMetadata("pilotid", Pilot.class.getDeclaredField("pilotId")));
        cols.put("pilotname", new ColumnMetadata("pilotname", Pilot.class.getDeclaredField("pilotName")));
        return new EntityMetadata("pilots", Pilot.class, List.of(Pilot.class.getDeclaredField("pilotId")), cols, new HashMap<>(), List.of(false));
    }
}
