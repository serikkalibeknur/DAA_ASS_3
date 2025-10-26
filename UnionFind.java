package com.socialnetwork;

import java.util.HashMap;
import java.util.Map;

/**
 * Union-Find (Disjoint Set Union) data structure with path compression and union by rank.
 * Used in Kruskal's algorithm to detect cycles.
 */
public class UnionFind {
    private final Map<String, String> parent;
    private final Map<String, Integer> rank;
    private int operationsCount;

    public UnionFind() {
        this.parent = new HashMap<>();
        this.rank = new HashMap<>();
        this.operationsCount = 0;
    }

    /**
     * Adds a new element to the union-find structure.
     */
    public void makeSet(String x) {
        if (!parent.containsKey(x)) {
            parent.put(x, x);
            rank.put(x, 0);
            operationsCount += 2; // Put operations
        }
    }

    /**
     * Finds the representative (root) of the set containing x.
     * Uses path compression for optimization.
     */
    public String find(String x) {
        operationsCount++; // Find operation
        if (!parent.get(x).equals(x)) {
            parent.put(x, find(parent.get(x))); // Path compression
            operationsCount++; // Recursive find
        }
        return parent.get(x);
    }

    /**
     * Unites the sets containing x and y.
     * Uses union by rank for optimization.
     * @return true if union was performed, false if already in same set
     */
    public boolean union(String x, String y) {
        String rootX = find(x);
        String rootY = find(y);
        
        operationsCount++; // Comparison
        if (rootX.equals(rootY)) {
            return false; // Already in same set
        }

        // Union by rank
        operationsCount++; // Rank comparison
        if (rank.get(rootX) < rank.get(rootY)) {
            parent.put(rootX, rootY);
            operationsCount++; // Update parent
        } else if (rank.get(rootX) > rank.get(rootY)) {
            parent.put(rootY, rootX);
            operationsCount++; // Update parent
        } else {
            parent.put(rootY, rootX);
            rank.put(rootX, rank.get(rootX) + 1);
            operationsCount += 2; // Update parent and rank
        }
        
        return true;
    }

    public int getOperationsCount() {
        return operationsCount;
    }

    public void resetOperationsCount() {
        this.operationsCount = 0;
    }
}

