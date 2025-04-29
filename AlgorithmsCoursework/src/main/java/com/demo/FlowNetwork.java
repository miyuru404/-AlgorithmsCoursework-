package com.demo;

import java.util.*;


public class FlowNetwork {

    private final int numNodes;
    private final List<List<Edge>> adjacencyList;     // Forward edges
    private final List<List<Edge>> reverseAdjList;    // Reverse edges (for residual graph)
    private final Map<Integer, Map<Integer, Edge>> edgeMap;  // Quick edge lookup


    public FlowNetwork(int numNodes) {
        this.numNodes = numNodes;

        // Initialize adjacency lists
        adjacencyList = new ArrayList<>(numNodes);
        reverseAdjList = new ArrayList<>(numNodes);
        for (int i = 0; i < numNodes; i++) {
            adjacencyList.add(new ArrayList<>());
            reverseAdjList.add(new ArrayList<>());
        }

        // Initialize edge map for quick lookups
        edgeMap = new HashMap<>();
        for (int i = 0; i < numNodes; i++) {
            edgeMap.put(i, new HashMap<>());
        }
    }


    public void addEdge(int from, int to, int capacity) {
        validateNode(from);
        validateNode(to);

        Edge edge = new Edge(from, to, capacity);
        adjacencyList.get(from).add(edge);
        reverseAdjList.get(to).add(edge);
        edgeMap.get(from).put(to, edge);
    }


    public Edge getEdge(int from, int to) {
        validateNode(from);
        validateNode(to);
        return edgeMap.get(from).get(to);
    }


    public List<Edge> getOutgoingEdges(int node) {
        validateNode(node);
        return Collections.unmodifiableList(adjacencyList.get(node));
    }


    public List<Edge> getIncomingEdges(int node) {
        validateNode(node);
        return Collections.unmodifiableList(reverseAdjList.get(node));
    }


    public int getTotalFlow(int source) {
        validateNode(source);
        int totalFlow = 0;
        for (Edge edge : adjacencyList.get(source)) {
            totalFlow += edge.getFlow();
        }
        return totalFlow;
    }


    public boolean isFlowConserved(int source, int sink) {
        for (int node = 0; node < numNodes; node++) {
            if (node == source || node == sink) {
                continue;
            }

            int inFlow = 0;
            for (Edge edge : reverseAdjList.get(node)) {
                inFlow += edge.getFlow();
            }

            int outFlow = 0;
            for (Edge edge : adjacencyList.get(node)) {
                outFlow += edge.getFlow();
            }

            if (inFlow != outFlow) {
                return false;
            }
        }
        return true;
    }


    public int getNumNodes() {
        return numNodes;
    }


    private void validateNode(int node) {
        if (node < 0 || node >= numNodes) {
            throw new IllegalArgumentException("Node index out of range: " + node);
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Flow Network with ").append(numNodes).append(" nodes:\n");

        for (int node = 0; node < numNodes; node++) {
            sb.append("Node ").append(node).append(":\n");

            for (Edge edge : adjacencyList.get(node)) {
                sb.append("  → ").append(edge).append("\n");
            }
        }

        return sb.toString();
    }
}