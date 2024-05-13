package org.drkswg;

import java.io.File;

public class CounterFactory {
    private CounterFactory() {}

    public static UniqueCounter getProperCounter(File file) {
        double fileSizeInMegaBytes = getFileSizeInMb(file);

        if (fileSizeInMegaBytes <= 512.0d) {
            return new ImprovedNaiveAlgorithm(file);
        } else {
            return new ExternalSortAlgorithm(file);
        }
    }

    private static double getFileSizeInMb(File file) {
        return file.length() / (1024.0 * 1024.0);
    }
}
