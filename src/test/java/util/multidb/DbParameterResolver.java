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

