package com.demo;


public class Edge {
    private final int from;      // Source node
    private final int to;        // Destination node
    private final int capacity;  // Maximum capacity of the edge
    private int flow;            // Current flow through the edge


    // Creates a new edge with the given source, destination, and capacity.

    public Edge(int from, int to, int capacity) {
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.flow = 0;
    }


    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getFlow() {
        return flow;
    }


    public void addFlow(int additionalFlow) {
        if (flow + additionalFlow > capacity || flow + additionalFlow < 0) {
            throw new IllegalArgumentException(
                    "Resulting flow would be invalid: " + (flow + additionalFlow));
        }
        this.flow += additionalFlow;
    }

    public int getResidualCapacity() {
        return capacity - flow;
    }

    //@Override
    //public String toString() {
      //  return String.format("Edge(%d→%d, flow=%d/%d)", from, to, flow, capacity);
    //}
}