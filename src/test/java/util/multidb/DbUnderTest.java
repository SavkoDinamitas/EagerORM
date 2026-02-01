package util.multidb;

import raf.thesis.api.ConnectionSupplier;

public record DbUnderTest(
        String name,
        ConnectionSupplier connectionSupplier,
        Runnable closeFunction,
        String initScript
) {
}
