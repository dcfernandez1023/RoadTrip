import java.util.ArrayList;
import java.util.HashMap;

public class TripRunner {

    public static void main(String[] args) throws Exception {
        DestinationParser dp = new DestinationParser();
        HashMap<String, String> attractions = dp.getAttractions("attractions.csv");
        ArrayList<HashMap<String, Object>> roads = dp.getRoads("roads.csv");
        //System.out.println("Attractions: " + attractions.toString());
        //System.out.println("Roads: " + roads.toString());
        RoadMap rm = new RoadMap(roads);
        rm.saveAdjacencyMatrix("test.csv");
    }
}
