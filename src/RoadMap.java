import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RoadMap {

    private int[][] matrix;
    private HashMap<String, Integer> lookUpTable;
    private int count = 0;

    public RoadMap(ArrayList<HashMap<String, Object>> roads) {
        this.constructLookUpTable(roads);
        this.constructAdjacencyMatrix(roads);
    }

    //saves adjacency matrix to a file in CSV format
    public void saveAdjacencyMatrix(String fileName) throws IOException {
        File f = new File(fileName);
        FileWriter fWriter = new FileWriter(fileName);
        if(f.createNewFile()) {
            for(int i = 0; i < this.matrix.length; i++) {
                String line = "";
                int[] arr = this.matrix[i];
                for(int j = 0; j < arr.length; j++) {
                    if(j == arr.length - 1) {
                        line = line + arr[j] + "\n";
                    }
                    else {
                        line = line + arr[j] + ",";
                    }
                }
                fWriter.write(line);
            }
        }
        else {
            for(int i = 0; i < this.matrix.length; i++) {
                String line = "";
                int[] arr = this.matrix[i];
                for(int j = 0; j < arr.length; j++) {
                    if(j == arr.length - 1) {
                        line = line + arr[j] + "\n";
                    }
                    else {
                        line = line + arr[j] + ",";
                    }
                }
                fWriter.write(line);
            }
        }
        fWriter.close();
    }

    private void constructLookUpTable(ArrayList<HashMap<String, Object>> roads) {
        this.lookUpTable = new HashMap<>();
        for (HashMap<String, Object> road : roads) {
            String start = (String) road.get("Start");
            String end = (String) road.get("End");
            if(this.lookUpTable.get(start) == null) {
                this.lookUpTable.put(start, this.count++);
            }
            if(this.lookUpTable.get(end) == null) {
                this.lookUpTable.put(end, this.count++);
            }
        }
        System.out.println("Count: " + count);
        System.out.println("Lookup size: " + this.lookUpTable.size());
        System.out.println("Matrix Lookup: " + this.lookUpTable.toString());
    }

    private void constructAdjacencyMatrix(ArrayList<HashMap<String, Object>> roads) {
        int size = roads.size();
        this.matrix = new int[this.count][this.count];
        for(int i = 0; i < size; i++) {
            HashMap<String, Object> route = roads.get(i);
            String startCity = (String) route.get("Start");
            String endCity = (String) route.get("End");
            int start = this.lookUpTable.get(startCity);
            int end = this.lookUpTable.get(endCity);
            this.matrix[start][end] = Integer.parseInt((String)route.get("Miles"));
        }
    }
}
