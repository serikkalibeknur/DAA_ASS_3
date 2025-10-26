package com.socialnetwork;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * Comprehensive JUnit tests for MST algorithms.
 * Tests both correctness and performance aspects of Prim's and Kruskal's algorithms.
 */
public class MSTAlgorithmsTest {
    
    private Graph simpleGraph;
    private Graph mediumGraph;
    
    @BeforeEach
    public void setUp() {
        // Create a simple test graph
        List<String> nodes = Arrays.asList("A", "B", "C", "D", "E");
        List<Edge> edges = Arrays.asList(
            new Edge("A", "B", 4),
            new Edge("A", "C", 3),
            new Edge("B", "C", 2),
            new Edge("B", "D", 5),
            new Edge("C", "D", 7),
            new Edge("C", "E", 8),
            new Edge("D", "E", 6)
        );
        simpleGraph = new Graph(1, nodes, edges);
        
        // Create a medium test graph
        List<String> nodes2 = Arrays.asList("A", "B", "C", "D");
        List<Edge> edges2 = Arrays.asList(
            new Edge("A", "B", 1),
            new Edge("A", "C", 4),
            new Edge("B", "C", 2),
            new Edge("C", "D", 3),
            new Edge("B", "D", 5)
        );
        mediumGraph = new Graph(2, nodes2, edges2);
    }
    
    // ========== CORRECTNESS TESTS ==========
    
    @Test
    @DisplayName("Both algorithms should produce same MST cost")
    public void testBothAlgorithmsProduceSameCost() {
        MSTResult primResult = PrimAlgorithm.findMST(simpleGraph);
        MSTResult kruskalResult = KruskalAlgorithm.findMST(simpleGraph);
        
        assertEquals(primResult.getTotalCost(), kruskalResult.getTotalCost(),
            "Prim and Kruskal should produce MSTs with same total cost");
    }
    
    @Test
    @DisplayName("MST should have V-1 edges for connected graph")
    public void testMSTEdgeCount() {
        MSTResult primResult = PrimAlgorithm.findMST(simpleGraph);
        MSTResult kruskalResult = KruskalAlgorithm.findMST(simpleGraph);
        
        int expectedEdges = simpleGraph.getVertexCount() - 1;
        
        assertEquals(expectedEdges, primResult.getMstEdges().size(),
            "Prim's MST should have V-1 edges");
        assertEquals(expectedEdges, kruskalResult.getMstEdges().size(),
            "Kruskal's MST should have V-1 edges");
    }
    
    @Test
    @DisplayName("MST should be acyclic")
    public void testMSTIsAcyclic() {
        MSTResult primResult = PrimAlgorithm.findMST(simpleGraph);
        MSTResult kruskalResult = KruskalAlgorithm.findMST(simpleGraph);
        
        assertTrue(isAcyclic(primResult.getMstEdges(), simpleGraph.getNodes()),
            "Prim's MST should be acyclic");
        assertTrue(isAcyclic(kruskalResult.getMstEdges(), simpleGraph.getNodes()),
            "Kruskal's MST should be acyclic");
    }
    
    @Test
    @DisplayName("MST should connect all vertices")
    public void testMSTConnectsAllVertices() {
        MSTResult primResult = PrimAlgorithm.findMST(simpleGraph);
        MSTResult kruskalResult = KruskalAlgorithm.findMST(simpleGraph);
        
        assertTrue(isConnected(primResult.getMstEdges(), simpleGraph.getNodes()),
            "Prim's MST should connect all vertices");
        assertTrue(isConnected(kruskalResult.getMstEdges(), simpleGraph.getNodes()),
            "Kruskal's MST should connect all vertices");
    }
    
    @Test
    @DisplayName("Disconnected graph should be handled gracefully")
    public void testDisconnectedGraph() {
        // Create a disconnected graph
        List<String> nodes = Arrays.asList("A", "B", "C", "D");
        List<Edge> edges = Arrays.asList(
            new Edge("A", "B", 1),
            new Edge("C", "D", 2)
        );
        Graph disconnectedGraph = new Graph(3, nodes, edges);
        
        assertFalse(disconnectedGraph.isConnected(),
            "Graph should be detected as disconnected");
        
        MSTResult primResult = PrimAlgorithm.findMST(disconnectedGraph);
        MSTResult kruskalResult = KruskalAlgorithm.findMST(disconnectedGraph);
        
        // For disconnected graph, MST should not have V-1 edges
        assertTrue(primResult.getMstEdges().size() < nodes.size() - 1,
            "Prim should not produce full MST for disconnected graph");
        assertTrue(kruskalResult.getMstEdges().size() < nodes.size() - 1,
            "Kruskal should not produce full MST for disconnected graph");
    }
    
    @Test
    @DisplayName("Empty graph should be handled")
    public void testEmptyGraph() {
        Graph emptyGraph = new Graph(4, new ArrayList<>(), new ArrayList<>());
        
        MSTResult primResult = PrimAlgorithm.findMST(emptyGraph);
        MSTResult kruskalResult = KruskalAlgorithm.findMST(emptyGraph);
        
        assertEquals(0, primResult.getTotalCost());
        assertEquals(0, kruskalResult.getTotalCost());
        assertEquals(0, primResult.getMstEdges().size());
        assertEquals(0, kruskalResult.getMstEdges().size());
    }
    
    @Test
    @DisplayName("Single vertex graph should be handled")
    public void testSingleVertexGraph() {
        List<String> nodes = Arrays.asList("A");
        List<Edge> edges = new ArrayList<>();
        Graph singleVertex = new Graph(5, nodes, edges);
        
        MSTResult primResult = PrimAlgorithm.findMST(singleVertex);
        MSTResult kruskalResult = KruskalAlgorithm.findMST(singleVertex);
        
        assertEquals(0, primResult.getTotalCost());
        assertEquals(0, kruskalResult.getTotalCost());
        assertEquals(0, primResult.getMstEdges().size());
        assertEquals(0, kruskalResult.getMstEdges().size());
    }
    
    @Test
    @DisplayName("Test known MST cost for simple graph")
    public void testKnownMSTCost() {
        // For the simple graph, expected MST cost is 16
        // Edges: B-C(2), A-C(3), B-D(5), D-E(6)
        MSTResult primResult = PrimAlgorithm.findMST(simpleGraph);
        MSTResult kruskalResult = KruskalAlgorithm.findMST(simpleGraph);
        
        assertEquals(16, primResult.getTotalCost(),
            "Prim should produce correct MST cost for simple graph");
        assertEquals(16, kruskalResult.getTotalCost(),
            "Kruskal should produce correct MST cost for simple graph");
    }
    
    @Test
    @DisplayName("Test known MST cost for medium graph")
    public void testKnownMSTCostMedium() {
        // For the medium graph, expected MST cost is 6
        // Edges: A-B(1), B-C(2), C-D(3)
        MSTResult primResult = PrimAlgorithm.findMST(mediumGraph);
        MSTResult kruskalResult = KruskalAlgorithm.findMST(mediumGraph);
        
        assertEquals(6, primResult.getTotalCost(),
            "Prim should produce correct MST cost for medium graph");
        assertEquals(6, kruskalResult.getTotalCost(),
            "Kruskal should produce correct MST cost for medium graph");
    }
    
    // ========== PERFORMANCE TESTS ==========
    
    @Test
    @DisplayName("Execution time should be non-negative")
    public void testExecutionTimeIsNonNegative() {
        MSTResult primResult = PrimAlgorithm.findMST(simpleGraph);
        MSTResult kruskalResult = KruskalAlgorithm.findMST(simpleGraph);
        
        assertTrue(primResult.getExecutionTimeMs() >= 0,
            "Prim execution time should be non-negative");
        assertTrue(kruskalResult.getExecutionTimeMs() >= 0,
            "Kruskal execution time should be non-negative");
    }
    
    @Test
    @DisplayName("Operation count should be non-negative")
    public void testOperationCountIsNonNegative() {
        MSTResult primResult = PrimAlgorithm.findMST(simpleGraph);
        MSTResult kruskalResult = KruskalAlgorithm.findMST(simpleGraph);
        
        assertTrue(primResult.getOperationsCount() >= 0,
            "Prim operations count should be non-negative");
        assertTrue(kruskalResult.getOperationsCount() >= 0,
            "Kruskal operations count should be non-negative");
    }
    
    @Test
    @DisplayName("Results should be reproducible")
    public void testResultsAreReproducible() {
        // Run Prim twice
        MSTResult prim1 = PrimAlgorithm.findMST(simpleGraph);
        MSTResult prim2 = PrimAlgorithm.findMST(simpleGraph);
        
        assertEquals(prim1.getTotalCost(), prim2.getTotalCost(),
            "Prim should produce same cost on repeated runs");
        assertEquals(prim1.getMstEdges().size(), prim2.getMstEdges().size(),
            "Prim should produce same number of edges on repeated runs");
        
        // Run Kruskal twice
        MSTResult kruskal1 = KruskalAlgorithm.findMST(simpleGraph);
        MSTResult kruskal2 = KruskalAlgorithm.findMST(simpleGraph);
        
        assertEquals(kruskal1.getTotalCost(), kruskal2.getTotalCost(),
            "Kruskal should produce same cost on repeated runs");
        assertEquals(kruskal1.getMstEdges().size(), kruskal2.getMstEdges().size(),
            "Kruskal should produce same number of edges on repeated runs");
    }
    
    @Test
    @DisplayName("Performance test on larger graph")
    public void testPerformanceOnLargerGraph() {
        // Generate a larger graph for performance testing
        Graph largeGraph = GraphGenerator.generateGraph(100, 100, 0.3);
        
        long primStart = System.nanoTime();
        MSTResult primResult = PrimAlgorithm.findMST(largeGraph);
        long primTime = System.nanoTime() - primStart;
        
        long kruskalStart = System.nanoTime();
        MSTResult kruskalResult = KruskalAlgorithm.findMST(largeGraph);
        long kruskalTime = System.nanoTime() - kruskalStart;
        
        // Both should complete in reasonable time (less than 1 second)
        assertTrue(primTime < 1_000_000_000L,
            "Prim should complete in less than 1 second for 100-node graph");
        assertTrue(kruskalTime < 1_000_000_000L,
            "Kruskal should complete in less than 1 second for 100-node graph");
        
        // Both should produce same cost
        assertEquals(primResult.getTotalCost(), kruskalResult.getTotalCost(),
            "Both algorithms should produce same cost for large graph");
    }
    
    // ========== HELPER METHODS ==========
    
    /**
     * Checks if the given edges form an acyclic graph using Union-Find.
     */
    private boolean isAcyclic(List<Edge> edges, List<String> nodes) {
        UnionFind uf = new UnionFind();
        
        for (String node : nodes) {
            uf.makeSet(node);
        }
        
        for (Edge edge : edges) {
            String root1 = uf.find(edge.getFrom());
            String root2 = uf.find(edge.getTo());
            
            if (root1.equals(root2)) {
                return false; // Cycle detected
            }
            
            uf.union(edge.getFrom(), edge.getTo());
        }
        
        return true;
    }
    
    /**
     * Checks if the given edges connect all vertices using BFS.
     */
    private boolean isConnected(List<Edge> edges, List<String> nodes) {
        if (nodes.isEmpty()) return true;
        
        // Build adjacency list from edges
        Map<String, List<String>> adj = new HashMap<>();
        for (String node : nodes) {
            adj.put(node, new ArrayList<>());
        }
        
        for (Edge edge : edges) {
            adj.get(edge.getFrom()).add(edge.getTo());
            adj.get(edge.getTo()).add(edge.getFrom());
        }
        
        // BFS from first node
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        
        queue.offer(nodes.get(0));
        visited.add(nodes.get(0));
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            for (String neighbor : adj.get(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }
        
        return visited.size() == nodes.size();
    }
}

