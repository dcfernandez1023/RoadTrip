import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class RoadMap {

    private int[][] graph;
    private HashMap<String, String> attractions;
    private HashMap<String, Integer> graphLookUpTable;
    private HashMap<Integer, String> cityLookUpTable;
    private int numCities = 0;

    public RoadMap(String attractionsFile, String roadsFile) throws Exception {
        LocationParser lp = new LocationParser();
        LinkedList<HashMap<String, Object>> roads = lp.getRoads(roadsFile);
        this.attractions = lp.getAttractions(attractionsFile);
        this.constructLookUpTables(roads);
        this.constructGraph(roads);
    }

    public int lookUpCity(String cityName) {
        return this.graphLookUpTable.get(cityName);
    }

    public List<String> route(String startCity, String endCity, List<String> attractions) {
        int size = attractions.size() + 2;
        List<String> visiting = new ArrayList<>(size);
        List<String> visited = new ArrayList<>(size);
        visiting.add(startCity);
        visited.add(startCity);
        for (String attraction : attractions) {
            visiting.add(this.attractions.get(attraction));
        }
        List<String> route = new ArrayList<>(size);
        int root = this.graphLookUpTable.get(startCity);
        for(int i = 0; i < size - 1; i++) {
            Map<Integer, int[][]> m = this.dijkstra(root, visiting, visited);
            if(m == null) {
                break;
            }
            int nextCity = m.entrySet().iterator().next().getKey();
            int[][] results = m.entrySet().iterator().next().getValue();
            if(nextCity != -1) {
                List<String> r = this.citiesInRoute(results, root, nextCity);
                System.out.println("Route from " + this.cityLookUpTable.get(root) + " to " + this.cityLookUpTable.get(nextCity) + ": " + r);
                if(i == 0) {
                    for (int j = r.size() - 1; j >= 0; j--) {
                        route.add(r.get(j));
                    }
                }
                else {
                    for (int j = r.size() - 2; j >=0; j--) {
                        route.add(r.get(j));
                    }
                }
                root = nextCity;
                visited.add(this.cityLookUpTable.get(nextCity));
            }
        }
        visiting.add(endCity);
        Map<Integer, int[][]> m = this.dijkstra(root, visiting, visited);
        int nextCity = m.entrySet().iterator().next().getKey();
        int[][] results = m.entrySet().iterator().next().getValue();
        List<String> r = this.citiesInRoute(results, root, nextCity);
        System.out.println("Route from " + this.cityLookUpTable.get(root) + " to " + this.cityLookUpTable.get(nextCity) + ": " + r);
        for(int j = r.size()-2; j >= 0; j--) {
            route.add(r.get(j));
        }
        return route;
    }

    private List<String> citiesInRoute(int[][] results, int root, int endVertex) {
        List<String> routeOrder = new LinkedList<>();
        routeOrder.add(this.cityLookUpTable.get(endVertex));
        int currentVertex = endVertex;
        for(int i = 0; i < this.numCities; i++) {
            if(currentVertex == root) {
                return routeOrder;
            }
            currentVertex = results[currentVertex][2];
            routeOrder.add(this.cityLookUpTable.get(currentVertex));
        }
        return routeOrder;
    }

    public HashMap<Integer, int[][]> dijkstra(int root, List<String> visiting, List<String> visited) {
        int[][] results = new int[this.numCities][4];
        for(int i = 0; i < results.length; i++) {
            int[] arr = results[i];
            arr[0] = i;
            arr[1] = 0;
            arr[2] = -1;
            arr[3] = -1;
        }
        HashMap<Integer, int[][]> nextCityAndRoute = new HashMap<>();
        int[] cities = new int[this.numCities];
        boolean[] known = new boolean[this.numCities];
        for(int i = 0; i < this.numCities; i++) {
            cities[i] = Integer.MAX_VALUE;
            known[i] = false;
        }
        cities[root] = 0;
        for(int count = 0; count < this.numCities - 1; count++) {
            int chosenVertex = findNextVertex(cities, known);
            if(chosenVertex != Integer.MAX_VALUE && chosenVertex != root && visiting.contains(this.cityLookUpTable.get(chosenVertex)) && !visited.contains(this.cityLookUpTable.get(chosenVertex))) {
                nextCityAndRoute.put(chosenVertex, results);
                return nextCityAndRoute;
            }
            known[chosenVertex] = true;
            results[chosenVertex][1] = 1;
            for(int v = 0; v < this.numCities; v++) {
                if(!known[v] && this.graph[chosenVertex][v] != 0 && cities[chosenVertex] != Integer.MAX_VALUE && cities[chosenVertex] + graph[chosenVertex][v] < cities[v]) {
                    cities[v] = cities[chosenVertex] + this.graph[chosenVertex][v];
                    results[v][2] = chosenVertex;
                    results[v][3] = cities[v];
                }
            }
        }
        //printDijkstraSolution(cities, root);
        return null;
    }

    //saves pure adjacency graph in CSV format (without city headers)
    public void savePureGraph(String fileName) throws IOException {
        File f = new File(fileName);
        FileWriter fWriter = new FileWriter(fileName);
        for (int[] ints : this.graph) {
            String line = "";
            int[] arr = ints;
            for (int j = 0; j < arr.length; j++)
            {
                if (j == arr.length - 1)
                {
                    line = line + arr[j] + "\n";
                } else
                {
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

    private int findNextVertex(int[] cities, boolean[] known) {
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
                System.out.println(rootCity + "  to  " + this.cityLookUpTable.get(i) + ": " + -1 + " miles");
                continue;
            }
            System.out.println(rootCity + "  to  " + this.cityLookUpTable.get(i) + ": " + cities[i] + " miles");
        }
    }

    private void constructLookUpTables(LinkedList<HashMap<String, Object>> roads) {
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

    private void constructGraph(LinkedList<HashMap<String, Object>> roads) {
        this.graph = new int[this.numCities][this.numCities];
        for (HashMap<String, Object> route : roads) {
            String startCity = (String) route.get("Start");
            String endCity = (String) route.get("End");
            int start = this.graphLookUpTable.get(startCity);
            int end = this.graphLookUpTable.get(endCity);
            this.graph[start][end] = Integer.parseInt((String) route.get("Miles"));
            this.graph[end][start] = Integer.parseInt((String) route.get("Miles"));
        }
    }
}