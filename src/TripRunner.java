import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class TripRunner {

    public static void main(String[] args) throws Exception {
        DestinationParser dp = new DestinationParser();
        HashMap<String, String> attractions = dp.getAttractions("attractions.csv");
        LinkedList<HashMap<String, Object>> roads = dp.getRoads("roads.csv");
        RoadMap rm = new RoadMap(roads);
        int city = rm.lookUpCity("San Francisco CA");
        rm.dijkstra(city);
    }
}
