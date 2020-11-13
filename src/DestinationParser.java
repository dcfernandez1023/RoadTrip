import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class DestinationParser {

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

    public ArrayList<HashMap<String, Object>> getRoads(String fileName) throws FileNotFoundException, Exception {
        File f = new File(fileName);
        ArrayList<String> test = new ArrayList<>();
        Scanner s = new Scanner(f);
        ArrayList<HashMap<String, Object>> roads = new ArrayList<>(100);
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
            String start = arr[0];
            String end = arr[1];
            if(!test.contains(start)) {
                test.add(start);
            }
            if(!test.contains(end)) {
                test.add(end);
            }
            roads.add(route);
        }
        System.out.println("Test size: " + test.size());
        return roads;
    }
}
