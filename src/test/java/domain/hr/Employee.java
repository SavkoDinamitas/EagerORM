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

package domain.hr;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
public class Employee {
    private int employee_id;
    private String first_name;
    private String last_name;
    private LocalDate hire_date;
    private Department department;
    private Employee manager;
    private List<Project> projects;

    public Employee(int employee_id, String first_name, String last_name, LocalDate hire_date) {
        this.employee_id = employee_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.hire_date = hire_date;
    }
}
