import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/*
    This class further pipelines the data obtained from LocationParser.java and constructs additional data structures to facilitate the
    process of using Dijkstra's algorithm to find the shortest path between the start city, attractions, and end city.  This class uses
    a modified version of Dijkstra's algorithm to find the shortest path by stopping once the next nearest city/attraction has been found
    and then setting the root vertex to that city and repeating the algorithm with that new city as the root vertex.
*/

public class RoadMap {

    private int[][] graph; //the matrix used to perform Dijkstra's algorithm
    private HashMap<String, String> attractions; //HashMap of attractions in the format: {attraction: city}
    private HashMap<String, Integer> graphLookUpTable; //HashMap look up table for cities in the format: {city: index on graph}
    private HashMap<Integer, String> cityLookUpTable; //HashMap look up table for cities in the format: {index on graph: city}
    private int numCities = 0;

    //constructor; instantiates a LocationParser object to get data from roads.csv and attractions.csv; also initializes data members
    public RoadMap(String attractionsFile, String roadsFile) throws Exception {
        LocationParser lp = new LocationParser();
        List<HashMap<String, Object>> roads = lp.getRoads(roadsFile);
        this.attractions = lp.getAttractions(attractionsFile);
        this.constructLookUpTables(roads);
        this.constructGraph(roads);
    }

    //finds the shortest path from the start city, attractions, and end city by using a modified version of Dijkstra's algorithm
    //calls a number of private helper functions defined within this class
    //if the names of the cities or attractions do not match the names from roads.csv or attractions.csv, this method terminates
    public List<String> route(String startCity, String endCity, List<String> attractions) {
        //validate inputs
        if(!this.isValidInputs(startCity, endCity, attractions)) {
            return null;
        }
        //IF START CITY AND END CITY ARE THE SAME AND THERE ARE NO ATTRACTIONS, RETURN ROUTE WITH ONLY THE START CITY/END CITY
        if(startCity.equals(endCity) && attractions.size() == 0) {
            List<String> route = new ArrayList<>(1);
            route.add(startCity);
            return route;
        }
        //INITIALIZE VARIABLES
        int size = attractions.size() + 2;
        List<String> visiting = new ArrayList<>(size); //array of cities to visit
        List<String> visited = new ArrayList<>(size); //array of cities that have been visited
        visiting.add(startCity);
        if(!startCity.equals(endCity)) {
            visited.add(startCity);
        }
        //add attractions to visiting list
        for (String attraction : attractions) {
            this.graphLookUpTable.get(attraction);
            visiting.add(this.attractions.get(attraction));
        }
        List<String> route = new ArrayList<>(size); //variable to store the fastest route
        int root = this.graphLookUpTable.get(startCity);
        //STEP 1: FIND FASTEST ROUTE BETWEEN START CITY AND ALL ATTRACTIONS
        for(int i = 0; i < size - 2; i++) {
            Map<Integer, int[][]> m = this.dijkstra(root, visiting, visited);
            //if m is null, then no path has been found
            if(m == null) {
                System.out.println("Could not find a route from " + startCity + " to " + endCity);
                return null;
            }
            int nextCity = m.entrySet().iterator().next().getKey(); //get index of next city
            int[][] results = m.get(nextCity); //get result table from dijkstra's algorithm
            if(nextCity != -1) {
                Stack<String> inRoute = this.citiesInRoute(results, root, nextCity); //find cities visited from the root vertex to the vertex of nextCity
                //System.out.println("Route from " + this.cityLookUpTable.get(root) + " to " + this.cityLookUpTable.get(nextCity) + ": " + inRoute);
                //add cities that were visited from dijkstra's algorithm to the route (dijkstra's algorithm ensures that these cities are the shortest path)
                while(!inRoute.empty()){
                    route.add(inRoute.pop());
                }
                root = nextCity; //set root to nextCity (by setting the root, this ensures that dijkstra's algorithm will be performed on the next city/attraction)
                visited.add(this.cityLookUpTable.get(nextCity)); //add nextCity to the visited list to ensure that dijkstra's algorithm does not attempt to find a path to the root vertex
            }
        }
        //STEP 2: FIND FASTEST ROUTE BETWEEN LAST ATTRACTION AND END CITY
        visiting.add(endCity); //add endCity to the visiting list so dijkstra's algorithm will visit it and return once it gets visited
        Map<Integer, int[][]> m = this.dijkstra(root, visiting, visited);
        //if m is null, then no path was found
        if(m == null) {
            System.out.println("Could not find a route from " + startCity + " to " + endCity);
            return null;
        }
        int nextCity = m.entrySet().iterator().next().getKey();
        int[][] results = m.get(nextCity);
        Stack<String> inRoute = this.citiesInRoute(results, root, nextCity);
        //System.out.println("Route from " + this.cityLookUpTable.get(root) + " to " + this.cityLookUpTable.get(nextCity) + ": " + inRoute);
        while(!inRoute.empty()){
            route.add(inRoute.pop());
        }
        route.add(endCity); //finally, add endCity before returning the route
        return route;
    }

    //finds the distance traveled from the given route (which is expected to be a list of cities)
    //this method is intended to be called after the method 'route()' to find the distance traveled from the list that 'route()' returns
    public int getDistanceTraveled(List<String> route) {
        try {
            int distance = 0;
            //loop through the list of cities and add to distance by getting the location of the two cities using the look up tables and getting their distance/weight from the matrix
            for (int i = 0; i < route.size() - 1; i++) {
                int city = this.graphLookUpTable.get(route.get(i));
                int nextCity = this.graphLookUpTable.get(route.get(i + 1));
                distance = distance + this.graph[city][nextCity];
            }
            return distance;
        }
        //input validation -- if any of the cities in the list are invalid (don't match the cities in the lookup tables), then terminate
        catch(Exception e) {
            System.out.println("Route contains invalid cities. Could not calculate distance");
        }
        return -1;
    }

    //validates inputs -- if the startCity, endCity, or any of the attractions are not found in the look up tables, then do not proceed, as the cities cannot be interpreted by the program
    private boolean isValidInputs(String startCity, String endCity, List<String> attractions) {
        try {
            int c = this.graphLookUpTable.get(startCity);
        }
        catch(Exception e) {
            System.out.println("'" + startCity + "'" + " is not a valid city. Cannot proceed");
            return false;
        }
        try {
            int c = this.graphLookUpTable.get(endCity);
        }
        catch(Exception e) {
            System.out.println("'" + endCity + "'" + " is not a valid city. Cannot proceed");
            return false;
        }
        for (String attraction : attractions) {
            String s = this.attractions.get(attraction);
            if(s == null) {
                System.out.println("'" + attraction + "'" + " is not a valid attraction. Cannot proceed");
                return false;
            }
        }
        return true;
    }

    //finds the shortest path of cities that were visited during dijkstra's algorithm from the root vertex to the endVertex
    //the 'int[][] results' parameter is the results table from dijkstra's algorithm;
    /*
        the columns (0-3) of 'int[][] results' are:
            0 - vertex
            1 - known
            2 - path
            3 - cost
    */
    private Stack<String> citiesInRoute(int[][] results, int root, int endVertex) {
        Stack<String> routeOrder = new Stack<>(); //using a stack because it is an efficient data structure to track the path (bottom of stack will be the endVertex, top will be the root vertex)
        if(root == endVertex) {
            return routeOrder;
        }
        int currentVertex = endVertex;
        for(int i = 0; i < this.numCities; i++) {
            currentVertex = results[currentVertex][2]; //get the previous vertex that the current vertex came from (backtracking dijkstra's algorithm)
            //if currentVertex == root, then dijkstra's algorithm has successfully been backtracked and the order of cities to the endVertex has been found
            if(currentVertex == root) {
                routeOrder.push(this.cityLookUpTable.get(currentVertex));
                return routeOrder;
            }
            routeOrder.push(this.cityLookUpTable.get(currentVertex));
        }
        return routeOrder;
    }

    //modified version of dijkstra's algorithm that stops once the closest city from the 'visiting' parameter has been visited
    //returns a HashMap<Integer, int[][]>, where the Integer is the index of the closest city from the 'visiting' parameter, and the int[][] is the results table, as described above the signature for the function 'citiesInRoute()'
    private HashMap<Integer, int[][]> dijkstra(int root, List<String> visiting, List<String> visited) {
        //initialize results table
        int[][] results = new int[this.numCities][4];
        for(int i = 0; i < results.length; i++) {
            int[] arr = results[i];
            arr[0] = i; //0 - vertex
            arr[1] = 0; //1 - known (0 = false, 1 = true)
            arr[2] = -1; //2 - path
            arr[3] = -1; //3 - cost
        }
        HashMap<Integer, int[][]> nextCityAndRoute = new HashMap<>();
        int[] cities = new int[this.numCities]; //array to help with dijkstra's algorithm, keeps track of cities and their weights
        boolean[] known = new boolean[this.numCities]; //array to help with dijkstra's algorithm, keeps track of visited cities
        for(int i = 0; i < this.numCities; i++) {
            cities[i] = Integer.MAX_VALUE; //use Integer.MAX_VALUE to represent 'infinite' cost to this vertex
            known[i] = false;
        }
        cities[root] = 0; //the cost to the root vertex from the root vertex is 0
        for(int count = 0; count < this.numCities - 1; count++) {
            int chosenVertex = findNextVertex(cities, known); //find least cost unknown vertex
            /*
                this is the modified part of dijkstra's algorithm:
                    if:
                        chosenVertex does not have an infinite cost
                        chosenVertex is not the root vertex
                        chosenVertex is a vertex to be visited
                        chosenVertex has not yet been visited
                    then:
                        return this vertex and the results table
            */
            if(chosenVertex != Integer.MAX_VALUE && chosenVertex != root && visiting.contains(this.cityLookUpTable.get(chosenVertex)) && !visited.contains(this.cityLookUpTable.get(chosenVertex))) {
                nextCityAndRoute.put(chosenVertex, results);
                return nextCityAndRoute;
            }
            known[chosenVertex] = true; //chosenVertex has been visited (in order to continue dijkstra's algorithm)
            results[chosenVertex][1] = 1; //chosenVertex has been visited (for results table)
            //update paths and costs of adjacent verticies to chosenVertex
            for(int v = 0; v < this.numCities; v++) {
                /*
                    if:
                        selected vertex is not known
                        selected vertex does not have a cost of 0 (meaning it would be the root)
                        chosenVertex does not have an 'infinite' cost
                        cost of chosenVertex + cost of chosenVertex to selected vertex < cost of selected vertex
                    then:
                        selected vertex is an adjacent vertex whose path and distance should be updated
                */
                if(!known[v] && this.graph[chosenVertex][v] != 0 && cities[chosenVertex] != Integer.MAX_VALUE && cities[chosenVertex] + graph[chosenVertex][v] < cities[v]) {
                    cities[v] = cities[chosenVertex] + this.graph[chosenVertex][v];
                    results[v][2] = chosenVertex;
                    results[v][3] = cities[v];
                }
            }
        }
        //return null if no path to any of the verticies from the 'visiting' list is found (indicates that modified version of dijkstra's did not find the desired city)
        return null;
    }

    //private helper method to find the least cost unknown vertex
    //'int[] cities' represents the cost to get to each city from the root (0-N verticies, where each value of index 0-N is the cost to that vertex)
    //'boolean[] known' represents cities that have been visited (0-N verticies, where each value of index 0-N is whether or not that vertex has been visited)
    private int findNextVertex(int[] cities, boolean[] known) {
        int min = Integer.MAX_VALUE;
        int minIndex = -1;
        for(int v = 0; v < this.numCities; v++) {
            //if vertex has not yet been visited and its cost is the most minimal, then set min and minIndex to those minimum values
            if(!known[v] && cities[v] <= min) {
                min = cities[v];
                minIndex = v;
            }
        }
        return minIndex;
    }

    //private helper method to print the solution to dijkstra's algorithm (only used it for testing early on in the project)
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

    /*
        Takes in a list of hashmaps, where each hashmap represents a road from one city to another: (ex: {Start: San Francisco CA, End: Stockton CA, Miles: 88, Minutes: 40})
        and constructs the data members 'graphLookUpTable' and 'cityLookUpTable', which are used to facilitate finding the name of a city given its index or finding the
        index of a city given its name.  These lookup tables make it easier to locate the path between two cities on the graph/matrix.
    */
    private void constructLookUpTables(List<HashMap<String, Object>> roads) {
        this.graphLookUpTable = new HashMap<>();
        this.cityLookUpTable = new HashMap<>();
        for (HashMap<String, Object> road : roads) {
            String start = (String) road.get("Start"); //get start city
            String end = (String) road.get("End"); //get end city
            //if graphLookUpTable does not contain start city yet, then put start city into it and into cityLookUp table as well
            //this ensures that the keys and values of the hashmap are unique (unique city names and verticies for those cities)
            if(this.graphLookUpTable.get(start) == null) {
                this.graphLookUpTable.put(start, this.numCities);
                this.cityLookUpTable.put(this.numCities++, start);

            }
            //if graphLookUpTable does not contain end city yet, then put end city into it and into cityLookUp table as well
            //this ensures that the keys and values of the hashmap are unique (unique city names and verticies for those cities)
            if(this.graphLookUpTable.get(end) == null) {
                this.graphLookUpTable.put(end, this.numCities);
                this.cityLookUpTable.put(this.numCities++, end);
            }
        }
    }

    /*
        constructs the data member 'graph' given a List of hashmaps, where each hashmap represents a road
        from one city to another: (ex: {Start: San Francisco CA, End: Stockton CA, Miles: 88, Minutes: 40})
    */
    private void constructGraph(List<HashMap<String, Object>> roads) {
        this.graph = new int[this.numCities][this.numCities];
        for (HashMap<String, Object> route : roads) {
            String startCity = (String) route.get("Start");
            String endCity = (String) route.get("End");
            int start = this.graphLookUpTable.get(startCity); //using lookup tables to place the verticies in their proper positions on the matrix (the lookup tables allows this method to populate the matrix in a O(n) runtime instead of O(n^2)
            int end = this.graphLookUpTable.get(endCity); //using lookup tables to place the verticies in their proper positions on the matrix (the lookup tables allows this method to populate the matrix in a O(n) runtime instead of O(n^2)
            this.graph[start][end] = Integer.parseInt((String) route.get("Miles")); //place weight on the graph/matrix from start city to end city
            this.graph[end][start] = Integer.parseInt((String) route.get("Miles")); //place weight on the graph/matrix from end city to start city
        }
    }

    //saves pure adjacency matrix in CSV format (without city headers)
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

    //saves adjacency matrix to a file in CSV format with city headers
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
}