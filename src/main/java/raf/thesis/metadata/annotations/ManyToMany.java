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

package raf.thesis.metadata.annotations;

import raf.thesis.api.QueryBuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a {@code List} field in an {@link Entity} class to be mapped as a many-to-many (n:m)
 * relationship in the database.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ManyToMany {
    /**
     * Relationship names are used in making paths for {@link QueryBuilder} join methods.
     * Default relationship name is the name of the field.
     */
    String relationName() default "";

    /**
     * Joined table name of this many-to-many (n:m) relationship in DB.
     * Must not be empty.
     */
    String joinedTableName();

    /**
     * Names of the columns in the joined table that act as foreign keys referencing
     * the table of this object.
     * By default, foreign key column names in the joined table match the primary key
     * column names of this table.
     */
    String[] myKey() default {};

    /**
     * Names of the columns in the joined table that act as foreign keys referencing
     * the table of the related object.
     * By default, foreign key column names in the joined table match the primary key
     * column names of the related object's table.
     */
    String[] theirKey() default {};
}
