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
import io.github.savkodinamitas.metadata.internal.RelationMetadata;
import io.github.savkodinamitas.metadata.internal.RelationType;
import io.github.savkodinamitas.metadata.annotations.Entity;
import io.github.savkodinamitas.metadata.annotations.Id;
import io.github.savkodinamitas.metadata.annotations.OneToMany;

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
