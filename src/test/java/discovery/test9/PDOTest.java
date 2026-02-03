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

package discovery.test9;

import raf.thesis.metadata.internal.ColumnMetadata;
import raf.thesis.metadata.internal.EntityMetadata;
import raf.thesis.metadata.annotations.Column;
import raf.thesis.metadata.annotations.PDO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@PDO
public class PDOTest {
    @Column(columnName = "department_id")
    private int departmentId;
    private int maxSalary;

    public static EntityMetadata getMetadata() throws NoSuchFieldException {
        Map<String, ColumnMetadata> columns = new HashMap<>();
        columns.put("department_id", new ColumnMetadata("department_id", PDOTest.class.getDeclaredField("departmentId")));
        columns.put("maxsalary", new ColumnMetadata("maxsalary", PDOTest.class.getDeclaredField("maxSalary")));
        return new EntityMetadata(null, PDOTest.class, new ArrayList<>(), columns, new HashMap<>(), new ArrayList<>());
    }
}
