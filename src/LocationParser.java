import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class LocationParser {

    private static final String[] ROADFIELDS = {"Start", "End", "Miles", "Minutes"};

    public HashMap<String, String> getAttractions(String fileName) throws FileNotFoundException, Exception {
        File f = new File(fileName);
        Scanner s = new Scanner(f);
        HashMap<String, String> attractions = new HashMap<>();
        while(s.hasNextLine()) {
            String line = s.nextLine();
            //skip empty lines
            if(line.trim().length() == 0) {
                continue;
            }
            String[] arr = line.split(",");
            attractions.put(arr[0], arr[1]);
        }
        return attractions;
    }

    public LinkedList<HashMap<String, Object>> getRoads(String fileName) throws FileNotFoundException, Exception {
        File f = new File(fileName);
        Scanner s = new Scanner(f);
        LinkedList<HashMap<String, Object>> roads = new LinkedList<>();
        while(s.hasNextLine()) {
            HashMap<String, Object> route = new HashMap<>();
            String line = s.nextLine();
            //skip empty lines
            if(line.trim().length() == 0) {
                continue;
            }
            String[] arr = line.split(",");
            for(int i = 0; i < ROADFIELDS.length; i++) {
                route.put(ROADFIELDS[i], arr[i]);
            }
            roads.add(route);
        }
        return roads;
    }
}