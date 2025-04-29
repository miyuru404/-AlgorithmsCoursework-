package com.demo;

import java.util.*;


public class EdmondsKarp {
    // Store path information
    private final List<String> executionSteps;
    private int iterations;
    private final List<Long> iterationTimes; // Store time taken for each iteration
    private final boolean detailedLogging;
    private static final int progress = 100;
    private static final int maxPathLength = 10; // Max number of nodes to show in path


    public EdmondsKarp() {
        this(true);
    }


    public EdmondsKarp(boolean detailedLogging) {
        this.detailedLogging = detailedLogging;
        this.executionSteps = new ArrayList<>();
        this.iterationTimes = new ArrayList<>();
        this.iterations = 0;
    }


    // Computes the maximum flow from source to sink in the given flow network.

    public int findMaxFlow(FlowNetwork network, int source, int sink) {
        executionSteps.clear();
        iterationTimes.clear();
        iterations = 0;

        int maxFlow = 0;

        if (detailedLogging) {
            executionSteps.add("Starting Edmonds-Karp algorithm with source=" + source + ", sink=" + sink);
        } else {
            System.out.println("Starting Edmonds-Karp algorithm (minimal logging mode)");
        }

        //  Find augmenting paths and push flow along them
        while (true) {
            iterations++;
            long iterationStartTime = System.nanoTime();

            // Progress reporting for large networks
            if (iterations % progress == 0 && !detailedLogging) {
                System.out.println("Completed " + iterations + " iterations. Current max flow: " + maxFlow);
            }

            // Find an augmenting path using BFS
            List<Edge> path = findAugmentingPath(network, source, sink);

            // If no augmenting path exists
            if (path == null) {
                if (detailedLogging) {
                    executionSteps.add("Iteration " + iterations + ": No augmenting path found. Algorithm terminates.");
                }
                break;
            }

            // Find the bottleneck capacity
            int bottleneckCapacity = findBottleneckCapacity(path);

            augmentFlow(path, bottleneckCapacity);

            maxFlow += bottleneckCapacity;

            // Record time taken for iteration
            long iterationEndTime = System.nanoTime();
            long iterationDuration = iterationEndTime - iterationStartTime;
            iterationTimes.add(iterationDuration);


            if (detailedLogging) {
                executionSteps.add("Iteration " + iterations + ":");
                // For long paths, only show a summary
                if (path.size() > maxPathLength) {
                    int from = path.get(0).getFrom();
                    int to = path.get(path.size()-1).getTo();
                    executionSteps.add("  Found augmenting path: " + from + " → ... → " + to +
                            " (" + path.size() + " edges)");
                } else {
                    executionSteps.add("  Found augmenting path: " + formatPathConcise(path));
                }
                executionSteps.add("  Bottleneck capacity: " + bottleneckCapacity);
                executionSteps.add("  Current max flow: " + maxFlow);
                executionSteps.add("  Iteration time: " + String.format("%.3f", iterationDuration / 1_000_000.0) + " ms");
            }
        }

        if (!detailedLogging) {
            System.out.println("Algorithm completed after " + iterations + " iterations. Max flow: " + maxFlow);
            // Store just the final summary
            executionSteps.add("Edmonds-Karp completed after " + iterations + " iterations");
            executionSteps.add("Maximum flow: " + maxFlow);
        }

        return maxFlow;
    }


     // Finds an augmenting path from source to sink using BFS.

    private List<Edge> findAugmentingPath(FlowNetwork network, int source, int sink) {
        int n = network.getNumNodes();

        // Track visited nodes and the edge that led to each node
        boolean[] visited = new boolean[n];
        Edge[] edgeTo = new Edge[n];

        // BFS queue
        Queue<Integer> queue = new LinkedList<>();
        queue.add(source);
        visited[source] = true;

        // Run BFS
        while (!queue.isEmpty() && !visited[sink]) {
            int node = queue.poll();

            for (Edge edge : network.getOutgoingEdges(node)) {
                int to = edge.getTo();


                if (edge.getResidualCapacity() > 0 && !visited[to]) {
                    edgeTo[to] = edge;
                    visited[to] = true;
                    queue.add(to);
                }
            }


            for (Edge edge : network.getIncomingEdges(node)) {
                int from = edge.getFrom();


                if (edge.getFlow() > 0 && !visited[from]) {
                    edgeTo[from] = edge;
                    visited[from] = true;
                    queue.add(from);
                }
            }
        }


        if (!visited[sink]) {
            return null;
        }

        // Reconstruct the path
        List<Edge> path = new ArrayList<>();
        for (int node = sink; node != source; ) {
            Edge edge = edgeTo[node];
            path.add(edge);
            node = edge.getFrom();  // Move backward in the path
        }

        // The path is from sink to source, so reverse it
        Collections.reverse(path);

        return path;
    }


    private int findBottleneckCapacity(List<Edge> path) {
        int bottleneckCapacity = Integer.MAX_VALUE;

        for (Edge edge : path) {
            bottleneckCapacity = Math.min(bottleneckCapacity, edge.getResidualCapacity());
        }

        return bottleneckCapacity;
    }

    private void augmentFlow(List<Edge> path, int flowValue) {
        for (Edge edge : path) {
            edge.addFlow(flowValue);
        }
    }


    public List<String> getExecutionSteps() {
        return Collections.unmodifiableList(executionSteps);
    }


    public int getIterations() {
        return iterations;
    }


    public List<Long> getIterationTimes() {
        return Collections.unmodifiableList(iterationTimes);
    }


    public double getAverageIterationTime() {
        if (iterationTimes.isEmpty()) {
            return 0.0;
        }

        long sum = 0;
        for (Long time : iterationTimes) {
            sum += time;
        }

        return (double) sum / iterationTimes.size();
    }


    public double getAverageIterationTimeMs() {
        return getAverageIterationTime() / 1_000_000.0;
    }

    private String formatPathConcise(List<Edge> path) {
        if (path.isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(path.get(0).getFrom());

        for (Edge edge : path) {
            sb.append(" → ").append(edge.getTo());
        }

        return sb.toString();
    }
}