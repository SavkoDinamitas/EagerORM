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

package util.multidb.apstraction;

import lombok.AllArgsConstructor;
import org.testcontainers.containers.JdbcDatabaseContainer;
@AllArgsConstructor
public class TestContainerDb implements DbBackend{
    private final JdbcDatabaseContainer<?> tc;
    private final String name;
    private final String initScript;
    private String connectionArgs = "";

    public TestContainerDb(JdbcDatabaseContainer<?> tc, String name, String initScript) {
        this.tc = tc;
        this.name = name;
        this.initScript = initScript;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void start() {
        tc.start();
    }

    @Override
    public String jdbcUrl() {
        return tc.getJdbcUrl() + connectionArgs;
    }

    @Override
    public String jdbcUser() {
        return tc.getUsername();
    }

    @Override
    public String jdbcPass() {
        return tc.getPassword();
    }

    @Override
    public String initScript() {
        return initScript;
    }

    @Override
    public void stop() {
        tc.stop();
    }
}
