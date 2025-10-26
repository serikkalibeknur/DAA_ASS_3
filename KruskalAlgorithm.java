package com.socialnetwork;

import java.util.*;

/**
 * Implementation of Kruskal's algorithm for finding Minimum Spanning Tree.
 * Uses Union-Find data structure to detect cycles efficiently.
 */
public class KruskalAlgorithm {
    
    /**
     * Finds MST using Kruskal's algorithm.
     * @param graph The input graph
     * @return MSTResult containing MST edges, cost, operations count, and execution time
     */
    public static MSTResult findMST(Graph graph) {
        long startTime = System.nanoTime();
        int operationsCount = 0;
        
        List<String> nodes = graph.getNodes();
        List<Edge> edges = graph.getEdges();
        
        // Handle empty graph
        if (nodes.isEmpty() || edges.isEmpty()) {
            long endTime = System.nanoTime();
            double executionTimeMs = (endTime - startTime) / 1_000_000.0;
            return new MSTResult(new ArrayList<>(), 0, operationsCount, executionTimeMs);
        }
        
        // Sort edges by weight
        List<Edge> sortedEdges = new ArrayList<>(edges);
        sortedEdges.sort(Comparator.comparingInt(Edge::getWeight));
        operationsCount += edges.size() * (int)(Math.log(edges.size()) / Math.log(2)); // Sorting operations estimate
        
        // Initialize Union-Find
        UnionFind uf = new UnionFind();
        for (String node : nodes) {
            uf.makeSet(node);
            operationsCount++; // makeSet operation
        }
        
        List<Edge> mstEdges = new ArrayList<>();
        int totalCost = 0;
        
        // Process edges in sorted order
        for (Edge edge : sortedEdges) {
            operationsCount++; // Edge iteration
            
            // Check if adding this edge creates a cycle
            if (uf.union(edge.getFrom(), edge.getTo())) {
                mstEdges.add(edge);
                totalCost += edge.getWeight();
                operationsCount += 2; // Add edge and update cost
                
                // Stop if we have V-1 edges
                if (mstEdges.size() == nodes.size() - 1) {
                    break;
                }
            }
        }
        
        // Add Union-Find operations to total count
        operationsCount += uf.getOperationsCount();
        
        long endTime = System.nanoTime();
        double executionTimeMs = (endTime - startTime) / 1_000_000.0;
        
        return new MSTResult(mstEdges, totalCost, operationsCount, executionTimeMs);
    }
}

