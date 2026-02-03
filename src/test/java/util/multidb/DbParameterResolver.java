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

package util.multidb;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import raf.thesis.api.ConnectionSupplier;
import raf.thesis.api.Session;

class DbParameterResolver implements ParameterResolver {

    private final DbUnderTest db;

    DbParameterResolver(DbUnderTest db) {
        this.db = db;
    }

    @Override
    public boolean supportsParameter(
            ParameterContext pc,
            ExtensionContext ec
    ) {
        Class<?> t = pc.getParameter().getType();
        return t == ConnectionSupplier.class
                || t == Session.class;
    }

    @Override
    public Object resolveParameter(
            ParameterContext pc,
            ExtensionContext ec
    ) {
        return switch (pc.getParameter().getType().getSimpleName()) {
            case "Session" -> new Session(db.connectionSupplier(), "layering");
            case "ConnectionSupplier" -> db.connectionSupplier();
            default -> throw new AssertionError("Not supported");
        };
    }
}

