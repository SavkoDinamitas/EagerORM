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

package io.github.savkodinamitas.metadata.annotations;

import io.github.savkodinamitas.api.QueryBuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field in an {@link Entity} class to be mapped as a one-to-one (1:1)
 * relationship in the database.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OneToOne {
    /**
     * Relationship names are used in making paths for {@link QueryBuilder} join methods.
     * Default relationship name is the name of the field.
     */
    String relationName() default "";

    /**
     * Names of columns that act as a foreign key in this relationship.
     * Depending on the {@link OneToOne#containsFk()} field, foreign key
     * column names match the primary key of the other object's table by default.
     */
    String[] foreignKey() default {};

    /**
     * Determines if foreign key columns are positioned in this object's table or in a related object's table.
     *
     * @return {@code true} if foreign key is in this object's table, {@code false} if foreign key is in related object's table
     */
    boolean containsFk();
}
