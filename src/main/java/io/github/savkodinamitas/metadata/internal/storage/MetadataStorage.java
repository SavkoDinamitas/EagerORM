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

package io.github.savkodinamitas.metadata.internal.storage;

import io.github.savkodinamitas.metadata.internal.EntityMetadata;

import java.util.HashMap;
import java.util.Map;

public class MetadataStorage {
    private static final Map<Class<?>, EntityMetadata> ENTITIES = new HashMap<>();

    public static void register(EntityMetadata metadata) {
        ENTITIES.put(metadata.getEntityClass(), metadata);
    }
    public static EntityMetadata get(Class<?> clazz) {
        return ENTITIES.get(clazz);
    }
    public static boolean contains(Class<?> clazz) {
        return ENTITIES.containsKey(clazz);
    }
    public static Map<Class<?>, EntityMetadata> getAllData(){
        return ENTITIES;
    }
    public static void removeAll(){
        ENTITIES.clear();
    }
}
