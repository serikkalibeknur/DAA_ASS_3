package com.socialnetwork;

import java.util.List;

/**
 * Stores the result of MST algorithm execution.
 */
public class MSTResult {
    private final List<Edge> mstEdges;
    private final int totalCost;
    private final int operationsCount;
    private final double executionTimeMs;

    public MSTResult(List<Edge> mstEdges, int totalCost, int operationsCount, double executionTimeMs) {
        this.mstEdges = mstEdges;
        this.totalCost = totalCost;
        this.operationsCount = operationsCount;
        this.executionTimeMs = executionTimeMs;
    }

    public List<Edge> getMstEdges() {
        return mstEdges;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public int getOperationsCount() {
        return operationsCount;
    }

    public double getExecutionTimeMs() {
        return executionTimeMs;
    }

    @Override
    public String toString() {
        return String.format("MSTResult{edges=%d, cost=%d, ops=%d, time=%.2fms}",
                mstEdges.size(), totalCost, operationsCount, executionTimeMs);
    }
}

