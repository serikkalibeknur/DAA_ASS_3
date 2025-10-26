package com.socialnetwork;

import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.Locale;

/**
 * Handles reading and writing JSON files for graph data and results.
 */
public class JSONHandler {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Reads graphs from JSON input file.
     */
    public static List<Graph> readGraphsFromJSON(String filename) throws IOException {
        String jsonContent = Files.readString(Paths.get(filename));
        JsonObject root = JsonParser.parseString(jsonContent).getAsJsonObject();
        JsonArray graphsArray = root.getAsJsonArray("graphs");
        
        List<Graph> graphs = new ArrayList<>();
        
        for (JsonElement graphElement : graphsArray) {
            JsonObject graphObj = graphElement.getAsJsonObject();
            
            int id = graphObj.get("id").getAsInt();
            
            // Read nodes
            JsonArray nodesArray = graphObj.getAsJsonArray("nodes");
            List<String> nodes = new ArrayList<>();
            for (JsonElement nodeElement : nodesArray) {
                nodes.add(nodeElement.getAsString());
            }
            
            // Read edges
            JsonArray edgesArray = graphObj.getAsJsonArray("edges");
            List<Edge> edges = new ArrayList<>();
            for (JsonElement edgeElement : edgesArray) {
                JsonObject edgeObj = edgeElement.getAsJsonObject();
                String from = edgeObj.get("from").getAsString();
                String to = edgeObj.get("to").getAsString();
                int weight = edgeObj.get("weight").getAsInt();
                edges.add(new Edge(from, to, weight));
            }
            
            graphs.add(new Graph(id, nodes, edges));
        }
        
        return graphs;
    }

    /**
     * Writes algorithm results to JSON output file.
     */
    public static void writeResultsToJSON(List<GraphResult> results, String filename) throws IOException {
        JsonObject root = new JsonObject();
        JsonArray resultsArray = new JsonArray();
        
        for (GraphResult result : results) {
            JsonObject graphResult = new JsonObject();
            graphResult.addProperty("graph_id", result.graphId);
            
            // Input stats
            JsonObject inputStats = new JsonObject();
            inputStats.addProperty("vertices", result.vertices);
            inputStats.addProperty("edges", result.edges);
            graphResult.add("input_stats", inputStats);
            
            // Prim results
            JsonObject primResult = createAlgorithmResultJson(result.primResult);
            graphResult.add("prim", primResult);
            
            // Kruskal results
            JsonObject kruskalResult = createAlgorithmResultJson(result.kruskalResult);
            graphResult.add("kruskal", kruskalResult);
            
            resultsArray.add(graphResult);
        }
        
        root.add("results", resultsArray);
        
        // Write to file
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(root, writer);
        }
    }

    private static JsonObject createAlgorithmResultJson(MSTResult result) {
        JsonObject algorithmResult = new JsonObject();
        
        // MST edges
        JsonArray mstEdgesArray = new JsonArray();
        for (Edge edge : result.getMstEdges()) {
            JsonObject edgeObj = new JsonObject();
            edgeObj.addProperty("from", edge.getFrom());
            edgeObj.addProperty("to", edge.getTo());
            edgeObj.addProperty("weight", edge.getWeight());
            mstEdgesArray.add(edgeObj);
        }
        algorithmResult.add("mst_edges", mstEdgesArray);
        
        algorithmResult.addProperty("total_cost", result.getTotalCost());
        algorithmResult.addProperty("operations_count", result.getOperationsCount());
        algorithmResult.addProperty("execution_time_ms", 
            Math.round(result.getExecutionTimeMs() * 100.0) / 100.0);
        
        return algorithmResult;
    }

    /**
     * Writes comparison results to CSV file.
     * Format: One row per graph with both Prim and Kruskal results.
     */
    public static void writeResultsToCSV(List<GraphResult> results, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Header - one row per graph with both algorithms
            writer.println("Graph_ID,Vertices,Edges,Prim_Cost,Prim_Operations,Prim_Time_ms,Kruskal_Cost,Kruskal_Operations,Kruskal_Time_ms,Cost_Match");
            
            // Data rows - one row per graph
            for (GraphResult result : results) {
                // Check if costs match
                String costMatch = (result.primResult.getTotalCost() == result.kruskalResult.getTotalCost()) 
                    ? "YES" : "NO";
                
                // Single row with both Prim and Kruskal data
                writer.printf(Locale.US, "%d,%d,%d,%d,%d,%.2f,%d,%d,%.2f,%s%n",
                    result.graphId,
                    result.vertices,
                    result.edges,
                    result.primResult.getTotalCost(),
                    result.primResult.getOperationsCount(),
                    result.primResult.getExecutionTimeMs(),
                    result.kruskalResult.getTotalCost(),
                    result.kruskalResult.getOperationsCount(),
                    result.kruskalResult.getExecutionTimeMs(),
                    costMatch);
            }
        }
    }

    /**
     * Container class for graph results.
     */
    public static class GraphResult {
        public int graphId;
        public int vertices;
        public int edges;
        public MSTResult primResult;
        public MSTResult kruskalResult;

        public GraphResult(int graphId, int vertices, int edges, 
                          MSTResult primResult, MSTResult kruskalResult) {
            this.graphId = graphId;
            this.vertices = vertices;
            this.edges = edges;
            this.primResult = primResult;
            this.kruskalResult = kruskalResult;
        }
    }
}

