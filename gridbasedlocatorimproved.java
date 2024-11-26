import java.io.*;
import java.util.*;

public class gridbasedlocatorimproved {

    private static final double CELL_SIZE = 1.0; // Size of each cell in the grid
    private static final int GRID_SIZE = 500;
    public static void main(String[] args) {
        String inputFile = "design.nodes"; // Path to your dataset file
        String outputFile = "output_locations10.txt"; // Output file for coordinates

        try {
            // Read dataset
            List<String> chipNames = readDataset(inputFile);

            // Generate coordinates using grid-based placement
            Map<String, double[]> coordinates = generateGridBasedCoordinates(chipNames);

            // Write coordinates to output file
            writeCoordinates(outputFile, coordinates);
        } catch (IOException e) {
            System.err.println("Error handling file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private static List<String> readDataset(String filename) throws IOException {
        List<String> chipNames = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                chipNames.add(line.trim());
            }
        } catch (FileNotFoundException e) {
            System.err.println("Input file not found: " + filename);
            throw e;
        }
        return chipNames;
    }

    private static Map<String, double[]> generateGridBasedCoordinates(List<String> chipNames) {
        Map<String, double[]> coordinates = new HashMap<>();
        int numRows = (int) Math.ceil(Math.sqrt(chipNames.size()));
        int numCols = (int) Math.ceil((double) chipNames.size() / numRows);

        for (int i = 0; i < chipNames.size(); i++) {
            String chipName = chipNames.get(i);
            int row = i / numCols;
            int col = i % numCols;
            double x = col * CELL_SIZE;
            double y = row * CELL_SIZE;
            coordinates.put(chipName, new double[]{x, y});
        }

        return coordinates;
    }

    private static void writeCoordinates(String filename, Map<String, double[]> coordinates) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Map.Entry<String, double[]> entry : coordinates.entrySet()) {
                String id = entry.getKey();
                double[] coords = entry.getValue();
                writer.write(String.format("%s %.6f %.6f%n", id, coords[0], coords[1])); // Format with six decimal places
            }
        } catch (IOException e) {
            System.err.println("Error writing to output file: " + filename);
            throw e;
        }
    }
}

