package org.drkswg;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class ImprovedNaiveAlgorithm implements UniqueCounter {
    private final File file;

    public ImprovedNaiveAlgorithm(File file) {
        this.file = file;
    }

    public long getUniquesCount() {
        Map<String, Object> uniques = new ConcurrentHashMap<>();

        try (Stream<String> lines = Files.lines(file.toPath())) {
            lines.parallel().forEach(line -> uniques.put(line, new Object()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return uniques.size();
    }
}
