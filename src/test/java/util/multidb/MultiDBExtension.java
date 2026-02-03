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

import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Stream;

public class MultiDBExtension implements ClassTemplateInvocationContextProvider {

    private static final Namespace NAMESPACE =
            Namespace.create(MultiDBExtension.class);

    private ClassTemplateInvocationContext invocationFor(DbUnderTest db) {
        return new ClassTemplateInvocationContext() {

            @Override
            public String getDisplayName(int invocationIndex) {
                return db.name();
            }

            @Override
            public List<Extension> getAdditionalExtensions() {
                return List.of(new DbParameterResolver(db), new BeforeEachCallback() {
                    @Override
                    public void beforeEach(ExtensionContext context) throws Exception {
                        try (Connection conn = db.connectionSupplier().getConnection();
                             Statement stmt = conn.createStatement()) {
                            stmt.execute(db.initScript());
                        }
                    }
                });
            }

        };
    }

    private List<DbUnderTest> getOrCreateDatabases(ExtensionContext context) {
        ExtensionContext.Store store = context
                .getRoot()
                .getStore(NAMESPACE);

        return store.getOrComputeIfAbsent(
                "dbs",
                k -> new DBHarness(),
                DBHarness.class
        ).getDbs();
    }


    @Override
    public boolean supportsClassTemplate(ExtensionContext context) {
        return true;
    }

    @Override
    public Stream<? extends ClassTemplateInvocationContext> provideClassTemplateInvocationContexts(ExtensionContext context) {
        List<DbUnderTest> dbs = getOrCreateDatabases(context);

        return dbs.stream().map(this::invocationFor);
    }
}
