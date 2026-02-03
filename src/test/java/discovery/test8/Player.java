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

package discovery.test8;

import io.github.savkodinamitas.metadata.internal.ColumnMetadata;
import io.github.savkodinamitas.metadata.internal.EntityMetadata;
import io.github.savkodinamitas.metadata.annotations.Entity;
import io.github.savkodinamitas.metadata.annotations.Id;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity(tableName = "players")
public class Player {
    @Id
    private int number;
    @Id
    private String clas;
    private String name;

    public static EntityMetadata getMetadata() throws NoSuchFieldException {
        Map<String, ColumnMetadata> cols = new HashMap<>();
        cols.put("number", new ColumnMetadata("number", Player.class.getDeclaredField("number")));
        cols.put("clas", new ColumnMetadata("clas", Player.class.getDeclaredField("clas")));
        cols.put("name", new ColumnMetadata("name", Player.class.getDeclaredField("name")));
        return new EntityMetadata("players", Player.class, List.of(Player.class.getDeclaredField("number"), Player.class.getDeclaredField("clas")), cols, new HashMap<>(), List.of(false, false));
    }
}
