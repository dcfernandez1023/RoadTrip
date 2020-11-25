import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/*
    This class directly accesses the data sources 'attractions.csv' and 'roads.csv' and pipelines the data from those two sources into
    Java data structures for RoadMap.java to interpret and use.
*/

public class LocationParser {

    private static final String[] ROADFIELDS = {"Start", "End", "Miles", "Minutes"}; //static fields to construct roads HashMap

    //extracts and transforms the data from attractions.csv to a HashMap<String, String>
    public HashMap<String, String> getAttractions(String fileName) throws FileNotFoundException, Exception {
        File f = new File(fileName);
        Scanner s = new Scanner(f);
        HashMap<String, String> attractions = new HashMap<>();
        //read line by line
        while(s.hasNextLine()) {
            String line = s.nextLine();
            //skip empty lines
            if(line.trim().length() == 0) {
                continue;
            }
            //get first and second values from csv record (attraction, city)
            String[] arr = line.split(",");
            attractions.put(arr[0], arr[1]);
        }
        return attractions;
    }

    //extracts and transforms the data from roads.csv to a List<HashMap<String, Object>>, which will later be converted to an int[][] in RoadMap.java
    public List<HashMap<String, Object>> getRoads(String fileName) throws FileNotFoundException, Exception {
        File f = new File(fileName);
        Scanner s = new Scanner(f);
        LinkedList<HashMap<String, Object>> roads = new LinkedList<>();
        //read line by line
        while(s.hasNextLine()) {
            HashMap<String, Object> route = new HashMap<>();
            String line = s.nextLine();
            //skip empty lines
            if(line.trim().length() == 0) {
                continue;
            }
            //construct HashMap using static ROADFIELDS data member
            String[] arr = line.split(",");
            for(int i = 0; i < ROADFIELDS.length; i++) {
                route.put(ROADFIELDS[i], arr[i]);
            }
            //add HashMap to the List
            roads.add(route);
        }
        return roads;
    }
}