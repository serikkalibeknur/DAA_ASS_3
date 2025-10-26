package com.socialnetwork;

import com.google.gson.*;
import java.io.*;
import java.util.*;

/**
 * Generates test graphs of various sizes for testing MST algorithms.
 */
public class GraphGenerator {
    private static final Random random = new Random(42); // Fixed seed for reproducibility

    /**
     * Generates a random connected graph.
     * @param id Graph ID
     * @param nodeCount Number of nodes
     * @param density Edge density (0.0 to 1.0, where 1.0 means complete graph)
     */
    public static Graph generateGraph(int id, int nodeCount, double density) {
        List<String> nodes = new ArrayList<>();
        
        // Generate node names
        for (int i = 0; i < nodeCount; i++) {
            nodes.add("N" + i);
        }
        
        // First, create a spanning tree to ensure connectivity
        List<Edge> edges = new ArrayList<>();
        for (int i = 1; i < nodeCount; i++) {
            int parent = random.nextInt(i);
            int weight = random.nextInt(100) + 1; // Weight between 1 and 100
            edges.add(new Edge(nodes.get(parent), nodes.get(i), weight));
        }
        
        // Add additional edges based on density
        int maxEdges = nodeCount * (nodeCount - 1) / 2; // Complete graph
        int currentEdges = nodeCount - 1; // Edges from spanning tree
        int targetEdges = Math.min(maxEdges, (int)(maxEdges * density));
        
        Set<String> existingEdges = new HashSet<>();
        for (Edge e : edges) {
            existingEdges.add(getEdgeKey(e.getFrom(), e.getTo()));
        }
        
        // Add random edges until we reach target density
        while (currentEdges < targetEdges) {
            int from = random.nextInt(nodeCount);
            int to = random.nextInt(nodeCount);
            
            if (from != to) {
                String key = getEdgeKey(nodes.get(from), nodes.get(to));
                if (!existingEdges.contains(key)) {
                    int weight = random.nextInt(100) + 1;
                    edges.add(new Edge(nodes.get(from), nodes.get(to), weight));
                    existingEdges.add(key);
                    currentEdges++;
                }
            }
        }
        
        return new Graph(id, nodes, edges);
    }

    private static String getEdgeKey(String from, String to) {
        // For undirected graph, normalize edge key
        if (from.compareTo(to) < 0) {
            return from + "-" + to;
        } else {
            return to + "-" + from;
        }
    }

    /**
     * Generates complete test dataset with graphs of all required sizes.
     */
    public static void generateTestDataset(String filename) throws IOException {
        List<Graph> allGraphs = new ArrayList<>();
        int graphId = 1;
        
        System.out.println("Generating test datasets...");
        
        // Small graphs: 5 graphs with ~30 nodes
        System.out.println("Generating small graphs (5 graphs, ~30 nodes each)...");
        for (int i = 0; i < 5; i++) {
            int nodes = 25 + random.nextInt(11); // 25-35 nodes
            double density = 0.2 + random.nextDouble() * 0.3; // 0.2-0.5 density
            allGraphs.add(generateGraph(graphId++, nodes, density));
        }
        
        // Medium graphs: 10 graphs with ~300 nodes
        System.out.println("Generating medium graphs (10 graphs, ~300 nodes each)...");
        for (int i = 0; i < 10; i++) {
            int nodes = 280 + random.nextInt(41); // 280-320 nodes
            double density = 0.1 + random.nextDouble() * 0.2; // 0.1-0.3 density
            allGraphs.add(generateGraph(graphId++, nodes, density));
        }
        
        // Large graphs: 10 graphs with ~1000 nodes
        System.out.println("Generating large graphs (10 graphs, ~1000 nodes each)...");
        for (int i = 0; i < 10; i++) {
            int nodes = 950 + random.nextInt(101); // 950-1050 nodes
            double density = 0.05 + random.nextDouble() * 0.1; // 0.05-0.15 density
            allGraphs.add(generateGraph(graphId++, nodes, density));
        }
        
        // Extra large graphs: 3 graphs with 1300, 1600, 2000 nodes
        System.out.println("Generating extra large graphs (3 graphs: 1300, 1600, 2000 nodes)...");
        allGraphs.add(generateGraph(graphId++, 1300, 0.05));
        allGraphs.add(generateGraph(graphId++, 1600, 0.04));
        allGraphs.add(generateGraph(graphId++, 2000, 0.03));
        
        // Write all graphs to single JSON file
        writeGraphsToJSON(allGraphs, filename);
        System.out.println("Generated " + allGraphs.size() + " graphs and saved to " + filename);
    }

    /**
     * Writes list of graphs to JSON file.
     */
    private static void writeGraphsToJSON(List<Graph> graphs, String filename) throws IOException {
        JsonObject root = new JsonObject();
        JsonArray graphsArray = new JsonArray();
        
        for (Graph graph : graphs) {
            JsonObject graphObj = new JsonObject();
            graphObj.addProperty("id", graph.getId());
            
            // Add nodes
            JsonArray nodesArray = new JsonArray();
            for (String node : graph.getNodes()) {
                nodesArray.add(node);
            }
            graphObj.add("nodes", nodesArray);
            
            // Add edges
            JsonArray edgesArray = new JsonArray();
            for (Edge edge : graph.getEdges()) {
                JsonObject edgeObj = new JsonObject();
                edgeObj.addProperty("from", edge.getFrom());
                edgeObj.addProperty("to", edge.getTo());
                edgeObj.addProperty("weight", edge.getWeight());
                edgesArray.add(edgeObj);
            }
            graphObj.add("edges", edgesArray);
            
            graphsArray.add(graphObj);
        }
        
        root.add("graphs", graphsArray);
        
        // Write to file with pretty printing
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(root, writer);
        }
    }
}

