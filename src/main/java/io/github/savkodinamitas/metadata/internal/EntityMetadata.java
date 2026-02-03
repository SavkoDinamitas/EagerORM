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

package io.github.savkodinamitas.metadata.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import io.github.savkodinamitas.metadata.exception.DuplicateRelationNamesException;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Entity metadata store.
 */
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class EntityMetadata {
    private String tableName;
    private Class<?> entityClass;
    private List<Field> idFields = new ArrayList<>();
    private Map<String, ColumnMetadata> columns = new LinkedHashMap<>();
    private Map<String, RelationMetadata> relations = new HashMap<>();
    private List<Boolean> generatedId = new ArrayList<>();

    public void addRelation(RelationMetadata relation) {
        if(relations.put(relation.getRelationName().toLowerCase(), relation) != null) {
            throw new DuplicateRelationNamesException("Multiple relations with name '" + relation.getRelationName() + "' in class "
                    + entityClass.getSimpleName() + ". Relation names must be unique inside class!");
        }
    }
}
