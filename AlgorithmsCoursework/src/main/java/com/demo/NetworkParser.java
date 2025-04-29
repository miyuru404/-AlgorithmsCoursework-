package com.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// Parser for reading network flow problems from files.
public class NetworkParser {



    public static FlowNetwork parseFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return parseFromReader(reader);
        }
    }

    public static FlowNetwork parseFromInputStream(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return parseFromReader(reader);
        }
    }


    private static FlowNetwork parseFromReader(BufferedReader reader) throws IOException {
        // Parse the number of nodes
        String firstLine = reader.readLine();
        if (firstLine == null) {
            throw new IllegalArgumentException("Empty file");
        }

        int numNodes;
        try {
            numNodes = Integer.parseInt(firstLine.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number of nodes: " + firstLine);
        }

        if (numNodes <= 1) {
            throw new IllegalArgumentException("Number of nodes must be at least 2");
        }

        // Create the flow network
        FlowNetwork network = new FlowNetwork(numNodes);

        // Parse each edge
        String line;
        int lineNumber = 1;  // Already processed the first line

        while ((line = reader.readLine()) != null) {
            lineNumber++;
            line = line.trim();

            // Skip empty lines
            if (line.isEmpty()) {
                continue;
            }

            // Parse the edge: "from to capacity"
            String[] parts = line.split("\\s+");
            if (parts.length != 3) {
                throw new IllegalArgumentException(
                        "Invalid edge format at line " + lineNumber + ": " + line);
            }

            try {
                int from = Integer.parseInt(parts[0]);
                int to = Integer.parseInt(parts[1]);
                int capacity = Integer.parseInt(parts[2]);

                if (from < 0 || from >= numNodes) {
                    throw new IllegalArgumentException(
                            "Invalid source node at line " + lineNumber + ": " + from);
                }

                if (to < 0 || to >= numNodes) {
                    throw new IllegalArgumentException(
                            "Invalid destination node at line " + lineNumber + ": " + to);
                }

                if (capacity < 0) {
                    throw new IllegalArgumentException(
                            "Negative capacity at line " + lineNumber + ": " + capacity);
                }

                network.addEdge(from, to, capacity);

            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Invalid number format at line " + lineNumber + ": " + line);
            }
        }

        return network;
    }
}