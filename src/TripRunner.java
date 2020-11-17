import java.util.ArrayList;
import java.util.HashMap;

public class TripRunner {

    public static void main(String[] args) throws Exception {
        DestinationParser dp = new DestinationParser();
        HashMap<String, String> attractions = dp.getAttractions("attractions.csv");
        ArrayList<HashMap<String, Object>> roads = dp.getRoads("roads.csv");
        RoadMap rm = new RoadMap(roads);
        int city = rm.lookUpCity("San Francisco CA");
        rm.dijkstra(city);
    }
}
