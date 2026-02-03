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

package raf.thesis.metadata.internal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;


/**
 * Column metadata store.
 */
@Getter @Setter @NoArgsConstructor
public class ColumnMetadata {
    private String columnName;
    private Field field;

    public ColumnMetadata(String columnName, Field field) {
        this.columnName = columnName.toLowerCase();
        this.field = field;
    }

    public void setColumnName(String name){
        columnName = name.toLowerCase();
    }
}
