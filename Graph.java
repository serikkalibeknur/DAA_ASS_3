package com.socialnetwork;

import java.util.*;

/**
 * Represents a weighted undirected graph for city transportation network.
 * Vertices represent city districts and edges represent potential roads with construction costs.
 */
public class Graph {
    private final int id;
    private final List<String> nodes;
    private final List<Edge> edges;
    private final Map<String, List<Edge>> adjacencyList;

    public Graph(int id, List<String> nodes, List<Edge> edges) {
        this.id = id;
        this.nodes = new ArrayList<>(nodes);
        this.edges = new ArrayList<>(edges);
        this.adjacencyList = new HashMap<>();
        buildAdjacencyList();
    }

    private void buildAdjacencyList() {
        // Initialize adjacency list for all nodes
        for (String node : nodes) {
            adjacencyList.put(node, new ArrayList<>());
        }
        
        // Add edges to adjacency list (undirected graph)
        for (Edge edge : edges) {
            adjacencyList.get(edge.getFrom()).add(edge);
            // For undirected graph, add reverse edge
            adjacencyList.get(edge.getTo()).add(new Edge(edge.getTo(), edge.getFrom(), edge.getWeight()));
        }
    }

    public int getId() {
        return id;
    }

    public List<String> getNodes() {
        return new ArrayList<>(nodes);
    }

    public List<Edge> getEdges() {
        return new ArrayList<>(edges);
    }

    public Map<String, List<Edge>> getAdjacencyList() {
        return adjacencyList;
    }

    public int getVertexCount() {
        return nodes.size();
    }

    public int getEdgeCount() {
        return edges.size();
    }

    /**
     * Checks if the graph is connected using BFS.
     */
    public boolean isConnected() {
        if (nodes.isEmpty()) return true;
        
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        
        queue.offer(nodes.get(0));
        visited.add(nodes.get(0));
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            for (Edge edge : adjacencyList.get(current)) {
                if (!visited.contains(edge.getTo())) {
                    visited.add(edge.getTo());
                    queue.offer(edge.getTo());
                }
            }
        }
        
        return visited.size() == nodes.size();
    }

    @Override
    public String toString() {
        return String.format("Graph{id=%d, vertices=%d, edges=%d}", id, nodes.size(), edges.size());
    }
}

