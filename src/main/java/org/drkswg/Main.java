package org.drkswg;

import java.io.*;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Path to file was not provided!");
            System.exit(1);
        }

        File file = new File(args[0]);
        UniqueCounter counter = CounterFactory.getProperCounter(file);

        long start = System.currentTimeMillis();
        long uniques = counter.getUniquesCount();
        long end = System.currentTimeMillis();
        int timeInSeconds = (int) (end - start) / 1000;

        System.out.println("Uniques: " + uniques);
        System.out.println("Time: " + timeInSeconds + " seconds");
    }
}