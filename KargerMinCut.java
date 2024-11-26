import java.io.*;
import java.util.*;

public class KargerMinCut {

    // Edge class to represent an edge between two vertices
    static class Edge {
        int u, v;
        public Edge(int u, int v) {
            this.u = u;
            this.v = v;
        }
    }

    // Graph class
    static class Graph {
        int V; // Number of vertices
        List<Edge> edges; // List of edges

        // Constructor
        public Graph(int V) {
            this.V = V;
            edges = new ArrayList<>();
        }

        // Add an edge
        public void addEdge(int u, int v) {
            edges.add(new Edge(u, v));
        }

        // Find with path compression
        public int find(int[] parent, int i) {
            if (parent[i] == i) {
                return i;
            }
            return parent[i] = find(parent, parent[i]);
        }

        // Union by rank
        public void union(int[] parent, int[] rank, int x, int y) {
            int rootX = find(parent, x);
            int rootY = find(parent, y);

            if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else {
                parent[rootY] = rootX;
                rank[rootX]++;
            }
        }

        // Karger's Min-Cut algorithm
        public int kargerMinCut() {
            int[] parent = new int[V];
            int[] rank = new int[V];
            int vertices = V;

            // Initialize parent and rank arrays
            for (int i = 0; i < V; i++) {
                parent[i] = i;
                rank[i] = 0;
            }

            // While more than 2 vertices are left
            while (vertices > 2) {
                // Pick a random edge
                int randomEdgeIndex = (int)(Math.random() * edges.size());
                Edge edge = edges.get(randomEdgeIndex);

                // Find roots of the two vertices of the picked edge
                int rootU = find(parent, edge.u);
                int rootV = find(parent, edge.v);

                // If both vertices are in the same subset, ignore this edge
                if (rootU == rootV) continue;

                // Otherwise, contract the edge (combine the two subsets)
                union(parent, rank, rootU, rootV);
                vertices--;

                // Remove the edge from the graph
                edges.remove(randomEdgeIndex);
            }

            // Calculate the cut size (number of edges between the two subsets)
            int cutEdges = 0;
            for (Edge edge : edges) {
                if (find(parent, edge.u) != find(parent, edge.v)) {
                    cutEdges++;
                }
            }

            return cutEdges;
        }
    }

    public static void main(String[] args) throws IOException {
        // File containing the dataset
        String filename = "output_locations8.txt";
        
        // Output file for edges
        BufferedWriter edgeWriter = new BufferedWriter(new FileWriter("edges_output2.txt"));
        
        // Read the dataset from the file
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        
        // Store component names and their coordinates
        Map<String, double[]> components = new HashMap<>();
        List<String> componentNames = new ArrayList<>(); // Store names for indexing
        
        int lineCount = 0; // Counter for lines processed

        while ((line = br.readLine()) != null) {
            lineCount++;
            // Split the line by spaces or tabs
            String[] parts = line.trim().split("\\s+");
            if (parts.length != 4) {
                System.err.println("Invalid line format (line " + lineCount + "): " + line);
                continue; // Skip invalid lines
            }
            String name = parts[0];  // Name of the component
            String type = parts[1];  // Type of the component (e.g., FDRE, LUT2, etc.)
            try {
                double x = Double.parseDouble(parts[2]);
                double y = Double.parseDouble(parts[3]);
                components.put(name, new double[]{x, y});
                componentNames.add(name); // Add name to list for indexing
                System.out.println("Component added: " + name + " at (" + x + ", " + y + ")");
            } catch (NumberFormatException e) {
                System.err.println("Error parsing coordinates for " + name + " (line " + lineCount + "): " + e.getMessage());
            }
        }
        br.close();
        
        System.out.println("Total components loaded: " + components.size());

        // Number of components (vertices)
        int V = components.size();
        if (V == 0) {
            System.out.println("No components found, exiting.");
            edgeWriter.close();
            return;
        }

        Graph graph = new Graph(V);
        
        // Create edges between components (e.g., based on some distance or connectivity logic)
        for (int i = 0; i < V; i++) {
            for (int j = i + 1; j < V; j++) {
                // Add edge between component i and j
                graph.addEdge(i, j);
                // Write the edge details to the output file
                double[] coord1 = components.get(componentNames.get(i));
                double[] coord2 = components.get(componentNames.get(j));
                edgeWriter.write("Edge between " + componentNames.get(i) + " (" + coord1[0] + ", " + coord1[1] + ") and " + componentNames.get(j) + " (" + coord2[0] + ", " + coord2[1] + ")\n");
            }
        }
        
        // Perform Karger's Min-Cut algorithm multiple times to improve the result
        int minCut = Integer.MAX_VALUE;
        int iterations = 100; // Adjust this value for better results
        System.out.println("Running Karger's Min-Cut Algorithm...");
        for (int i = 0; i < iterations; i++) {
            int cut = graph.kargerMinCut();
            System.out.println("Iteration " + (i+1) + " - Cut size: " + cut);
            if (cut < minCut) {
                minCut = cut;
            }
        }
        
        System.out.println("Minimum Cut found: " + minCut);

        // Write final result to the output file
        edgeWriter.write("Minimum Cut found: " + minCut + "\n");
        edgeWriter.close();
    }
}
