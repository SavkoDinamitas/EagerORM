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

package layering;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import io.github.savkodinamitas.metadata.annotations.Column;
import io.github.savkodinamitas.metadata.annotations.Entity;
import io.github.savkodinamitas.metadata.annotations.Id;
import io.github.savkodinamitas.metadata.annotations.ManyToMany;

import java.util.List;

@Entity(tableName = "projects")
@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class Project {
    @Id(generated = true)
    @Column(columnName = "project_id")
    private int projectId;
    @Column(columnName = "project_name")
    private String projectName;
    @ManyToMany(joinedTableName = "employee_projects")
    List<Employee> employees;

    public Project(int projectId, String projectName) {
        this.projectId = projectId;
        this.projectName = projectName;
    }
}
