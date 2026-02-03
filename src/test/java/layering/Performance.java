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
import raf.thesis.metadata.annotations.Column;
import raf.thesis.metadata.annotations.Entity;
import raf.thesis.metadata.annotations.Id;
import raf.thesis.metadata.annotations.OneToOne;

@Entity(tableName = "performances")
@AllArgsConstructor@NoArgsConstructor@Getter@Setter
public class Performance {
    @Id
    @Column(columnName = "performance_id")
    private int performanceId;
    @Column(columnName = "performance_score")
    private double performanceScore;
    @OneToOne(containsFk = true)
    private Employee employee;

    public Performance(int performanceId, double performanceScore) {
        this.performanceId = performanceId;
        this.performanceScore = performanceScore;
    }
}
