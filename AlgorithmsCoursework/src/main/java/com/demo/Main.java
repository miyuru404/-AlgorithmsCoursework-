package com.demo;

import java.io.IOException;


public class Main {

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Usage: java networkflow.Main <input-file>");
            System.exit(1);
        }

        String inputFile = args[0];

        try {
            // Parse the network from the input file
            System.out.println("Parsing network from file: " + inputFile);
            FlowNetwork network = NetworkParser.parseFromFile(inputFile);

            // Run the Edmonds-Karp algorithm
            System.out.println("\nRunning Edmonds-Karp algorithm...");
            EdmondsKarp edmondsKarp = new EdmondsKarp();
            int source = 0;
            int sink = network.getNumNodes() - 1;

            // Start timing
            long startTime = System.nanoTime();

            int maxFlow = edmondsKarp.findMaxFlow(network, source, sink);

            // End timing
            long endTime = System.nanoTime();

            // Calculate duration in milliseconds
            double durationMs = (endTime - startTime) / 1_000_000.0;


            System.out.println("\n----- EXECUTION DETAILS -----");
            for (String step : edmondsKarp.getExecutionSteps()) {
                System.out.println(step);
            }


            System.out.println("\n----- SUMMARY -----");
            System.out.println("Maximum Flow: " + maxFlow);
            System.out.println("Total Iterations: " + edmondsKarp.getIterations());
            System.out.println("Total Execution Time: " + String.format("%.3f", durationMs) + " ms");
            System.out.println("Average Time per Iteration: " + String.format("%.3f", edmondsKarp.getAverageIterationTimeMs()) + " ms");


            boolean conserved = network.isFlowConserved(source, sink);
            if (!conserved) {
                System.err.println("WARNING: Flow conservation property is violated!");
            }

        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("Error in input file format: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}