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

package util.multidb.apstraction;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SimpleBackend implements DbBackend{
    private final String name;
    private final String jdbcUrl;
    private final String jdbcUser;
    private final String jdbcPass;
    private final String initScript;
    @Override
    public String name() {
        return name;
    }

    @Override
    public void start() {

    }

    @Override
    public String jdbcUrl() {
        return jdbcUrl;
    }

    @Override
    public String jdbcUser() {
        return jdbcUser;
    }

    @Override
    public String jdbcPass() {
        return jdbcPass;
    }

    @Override
    public String initScript() {
        return initScript;
    }

    @Override
    public void stop() {

    }
}
