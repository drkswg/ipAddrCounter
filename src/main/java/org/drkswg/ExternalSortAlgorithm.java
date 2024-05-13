package org.drkswg;

import org.drkswg.exception.GetUniqueLinesCountException;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class ExternalSortAlgorithm implements UniqueCounter {
    private final File file;

    public ExternalSortAlgorithm(File file) {
        this.file = file;
    }

    @Override
    public long getUniquesCount() throws GetUniqueLinesCountException {
        Comparator<String> comparator = Comparator.naturalOrder();
        long lines;

        try {
            List<File> l = sortInBatch(file, comparator);
            lines = mergeSortedFiles(l, comparator);
        } catch (IOException e) {
            throw new GetUniqueLinesCountException("Error while counting unique lines", e);
        }

        return lines;
    }

    public long estimateBestSizeOfChunks(File fileToSort) {
        long fileSize = fileToSort.length();
        final int MAX_TEMP_FILES = 1024;
        long chunkSize = fileSize / MAX_TEMP_FILES ;
        long freeMemory = Runtime.getRuntime().freeMemory();

        if (chunkSize < freeMemory / 2) {
            chunkSize = freeMemory / 2;
        } else {
            if (chunkSize >= freeMemory) System.out.println("We expect to run out of memory!");
        }

        return chunkSize;
    }

    public List<File> sortInBatch(File file, Comparator<String> comparator) throws IOException {
        List<File> files = new ArrayList<>();

        try (BufferedReader fbr = new BufferedReader(new FileReader(file))) {
            long chunkSize = estimateBestSizeOfChunks(file);
            List<String> templist = new ArrayList<>();
            String line = "";

            try {
                while (line != null) {
                    long currentChunkSize = 0;// in bytes

                    while ((currentChunkSize < chunkSize) && ((line = fbr.readLine()) != null)) {
                        templist.add(line);
                        currentChunkSize += line.length();
                    }

                    files.add(sortAndSave(templist, comparator));
                    templist.clear();
                }
            } catch (EOFException oef) {
                if (!templist.isEmpty()) {
                    files.add(sortAndSave(templist, comparator));
                    templist.clear();
                }
            }
        }

        return files;
    }

    public File sortAndSave(List<String> tempList, Comparator<String> comparator) throws IOException  {
        tempList.sort(comparator);
        File newTempFile = File.createTempFile("sortInBatch", "flatFile");
        newTempFile.deleteOnExit();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(newTempFile))) {
            for (String r : tempList) {
                writer.write(r);
                writer.newLine();
            }
        }
        return newTempFile;
    }

    public long mergeSortedFiles(List<File> files, final Comparator<String> comparator) throws IOException {
        PriorityQueue<BinaryFileBuffer> queue = new PriorityQueue<>(
                11,
                (i, j) -> comparator.compare(i.peek(), j.peek())
        );

        for (File f : files) {
            BinaryFileBuffer buffer = new BinaryFileBuffer(f);
            queue.add(buffer);
        }

        String previousLine = null;
        long uniques = 0L;
        try {
            while(!queue.isEmpty()) {
                BinaryFileBuffer buffer = queue.poll();
                String r = buffer.pop();
                if (!r.equals(previousLine)) uniques++;
                previousLine = r;

                if (buffer.empty()) {
                    buffer.getReader().close();
                    Files.deleteIfExists(buffer.getOriginalFile().toPath());
                } else {
                    queue.add(buffer);
                }
            }
        } finally {
            for (BinaryFileBuffer buffer : queue) buffer.close();
        }

        return uniques;
    }
}

class BinaryFileBuffer  {
    private static final int BUFFER_SIZE = 2048;
    private final BufferedReader reader;
    private final File originalFile;
    private String cache;
    private boolean empty;

    public BinaryFileBuffer(File f) throws IOException {
        originalFile = f;
        reader = new BufferedReader(new FileReader(f), BUFFER_SIZE);
        reload();
    }

    public BufferedReader getReader() {
        return reader;
    }

    public File getOriginalFile() {
        return originalFile;
    }

    public boolean empty() {
        return empty;
    }

    private void reload() throws IOException {
        try {
            empty = (this.cache = reader.readLine()) == null;
        } catch (EOFException oef) {
            empty = true;
            cache = null;
        }
    }

    public void close() throws IOException {
        reader.close();
    }

    public String peek() {
        if (empty()) return null;

        return cache;
    }
    public String pop() throws IOException {
        String answer = peek();
        reload();

        return answer;
    }
}
