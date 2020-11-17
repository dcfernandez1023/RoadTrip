import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class RoadMap {

    private int[][] graph;
    //these two hashmaps act as a bimap (both keys and values are unique to one another)
    private HashMap<String, Integer> graphLookUpTable;
    private HashMap<Integer, String> cityLookUpTable;
    private int numCities = 0;

    public RoadMap(ArrayList<HashMap<String, Object>> roads) {
        this.constructLookUpTables(roads);
        this.constructGraph(roads);
    }

    public int[][] getGraph() {
        return this.graph;
    }

    public int lookUpCity(String cityName) {
        return this.graphLookUpTable.get(cityName);
    }

    //saves pure adjacency graph in CSV format (without city headers)
    public void savePureGraph(String fileName) throws IOException {
        File f = new File(fileName);
        FileWriter fWriter = new FileWriter(fileName);
        for(int i = 0; i < this.graph.length; i++) {
            String line = "";
            int[] arr = this.graph[i];
            for (int j = 0; j < arr.length; j++) {
                if (j == arr.length - 1) {
                    line = line + arr[j] + "\n";
                } else {
                    line = line + arr[j] + ",";
                }
            }
            fWriter.write(line);
        }
        fWriter.close();
    }

    //saves adjacency graph to a file in CSV format with city headers
    public void saveGraph(String fileName) throws IOException {
        File f = new File(fileName);
        FileWriter fWriter = new FileWriter(fileName);
        String colHeaders = " ,";
        for(int i = 0 ; i < this.graph.length; i++) {
            if(i == this.graph.length - 1) {
                colHeaders = colHeaders + "\n";
            }
            else {
                colHeaders = colHeaders + this.cityLookUpTable.get(i) + ",";
            }
        }
        fWriter.write(colHeaders);
        for(int i = 0; i < this.graph.length; i++) {
            String line = "" + this.cityLookUpTable.get(i) + ",";
            int[] arr = this.graph[i];
            for (int j = 0; j < arr.length; j++) {
                if (j == arr.length - 1) {
                    line = line + arr[j] + "\n";
                } else {
                    line = line + arr[j] + ",";
                }
            }
            fWriter.write(line);
        }
        fWriter.close();
    }

    private int findNextVertex(int[] cities, Boolean[] known) {
        int min = Integer.MAX_VALUE;
        int minIndex = -1;
        for(int v = 0; v < this.numCities; v++) {
            if(!known[v] && cities[v] <= min) {
                min = cities[v];
                minIndex = v;
            }
        }
        return minIndex;
    }

    private void printDijkstraSolution(int[] cities, int root) {
        String rootCity = this.cityLookUpTable.get(root);
        System.out.println("Dijkstra Results");
        for(int j = 0; j < "Dijkstra Results".length(); j++) {
            System.out.print("-");
        }
        System.out.print("\n");
        for (int i = 0; i < this.numCities; i++) {
            if(cities[i] == Integer.MAX_VALUE) {
                System.out.println(this.cityLookUpTable.get(i) + "  to  " + rootCity + ": " + -1 + " miles");
                continue;
            }
            System.out.println(this.cityLookUpTable.get(i) + "  to  " + rootCity + ": " + cities[i] + " miles");
        }
    }

    public void dijkstra(int root) {
        int[] cities = new int[this.numCities];
        Boolean[] known = new Boolean[this.numCities];
        for(int i = 0; i < this.numCities; i++) {
            cities[i] = Integer.MAX_VALUE;
            known[i] = false;
        }
        cities[root] = 0;
        for(int count = 0; count < this.numCities - 1; count++) {
            int chosenVertex = findNextVertex(cities, known);
            known[chosenVertex] = true;
            for(int v = 0; v < this.numCities; v++) {
                if(!known[v] && this.graph[chosenVertex][v] != 0 && cities[chosenVertex] != Integer.MAX_VALUE && cities[chosenVertex] + graph[chosenVertex][v] < cities[v]) {
                    cities[v] = cities[chosenVertex] + this.graph[chosenVertex][v];
                }
            }
        }
        this.printDijkstraSolution(cities, root);
    }

    private void constructLookUpTables(ArrayList<HashMap<String, Object>> roads) {
        this.graphLookUpTable = new HashMap<>();
        this.cityLookUpTable = new HashMap<>();
        for (HashMap<String, Object> road : roads) {
            String start = (String) road.get("Start");
            String end = (String) road.get("End");
            if(this.graphLookUpTable.get(start) == null) {
                this.graphLookUpTable.put(start, this.numCities);
                this.cityLookUpTable.put(this.numCities++, start);

            }
            if(this.graphLookUpTable.get(end) == null) {
                this.graphLookUpTable.put(end, this.numCities);
                this.cityLookUpTable.put(this.numCities++, end);
            }
        }
    }

    private void constructGraph(ArrayList<HashMap<String, Object>> roads) {
        int size = roads.size();
        this.graph = new int[this.numCities][this.numCities];
        for(int i = 0; i < size; i++) {
            HashMap<String, Object> route = roads.get(i);
            String startCity = (String) route.get("Start");
            String endCity = (String) route.get("End");
            int start = this.graphLookUpTable.get(startCity);
            int end = this.graphLookUpTable.get(endCity);
            this.graph[start][end] = Integer.parseInt((String)route.get("Miles"));
            this.graph[end][start] = Integer.parseInt((String)route.get("Miles"));
        }
    }
}
