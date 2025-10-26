package com.socialnetwork;

import java.util.*;

/**
 * Implementation of Prim's algorithm for finding Minimum Spanning Tree.
 * Uses a priority queue to efficiently select the minimum weight edge.
 */
public class PrimAlgorithm {
    
    /**
     * Finds MST using Prim's algorithm.
     * @param graph The input graph
     * @return MSTResult containing MST edges, cost, operations count, and execution time
     */
    public static MSTResult findMST(Graph graph) {
        long startTime = System.nanoTime();
        int operationsCount = 0;
        
        List<String> nodes = graph.getNodes();
        Map<String, List<Edge>> adjacencyList = graph.getAdjacencyList();
        
        // Handle empty or disconnected graphs
        if (nodes.isEmpty() || !graph.isConnected()) {
            long endTime = System.nanoTime();
            double executionTimeMs = (endTime - startTime) / 1_000_000.0;
            return new MSTResult(new ArrayList<>(), 0, operationsCount, executionTimeMs);
        }
        
        List<Edge> mstEdges = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        PriorityQueue<EdgeWithNode> pq = new PriorityQueue<>();
        
        // Start from the first node
        String startNode = nodes.get(0);
        visited.add(startNode);
        operationsCount++; // Add to visited set
        
        // Add all edges from start node to priority queue
        for (Edge edge : adjacencyList.get(startNode)) {
            pq.offer(new EdgeWithNode(edge, edge.getTo()));
            operationsCount++; // Offer to priority queue
        }
        
        int totalCost = 0;
        
        // Continue until we have V-1 edges or queue is empty
        while (!pq.isEmpty() && mstEdges.size() < nodes.size() - 1) {
            EdgeWithNode current = pq.poll();
            operationsCount++; // Poll from priority queue
            
            String toNode = current.toNode;
            
            operationsCount++; // Check if visited
            if (visited.contains(toNode)) {
                continue;
            }
            
            // Add edge to MST
            mstEdges.add(current.edge);
            totalCost += current.edge.getWeight();
            visited.add(toNode);
            operationsCount += 3; // Add edge, update cost, mark visited
            
            // Add all edges from newly added node
            for (Edge edge : adjacencyList.get(toNode)) {
                operationsCount++; // Check edge
                if (!visited.contains(edge.getTo())) {
                    pq.offer(new EdgeWithNode(edge, edge.getTo()));
                    operationsCount++; // Offer to priority queue
                }
            }
        }
        
        long endTime = System.nanoTime();
        double executionTimeMs = (endTime - startTime) / 1_000_000.0;
        
        return new MSTResult(mstEdges, totalCost, operationsCount, executionTimeMs);
    }
    
    /**
     * Helper class to store edge with destination node for priority queue.
     */
    private static class EdgeWithNode implements Comparable<EdgeWithNode> {
        Edge edge;
        String toNode;
        
        EdgeWithNode(Edge edge, String toNode) {
            this.edge = edge;
            this.toNode = toNode;
        }
        
        @Override
        public int compareTo(EdgeWithNode other) {
            return Integer.compare(this.edge.getWeight(), other.edge.getWeight());
        }
    }
}

