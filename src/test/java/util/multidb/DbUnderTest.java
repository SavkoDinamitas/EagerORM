package util.multidb;

import raf.thesis.ConnectionSupplier;

public record DbUnderTest(
        String name,
        ConnectionSupplier connectionSupplier,
        Runnable closeFunction,
        String initScript
) {
}
