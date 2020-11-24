import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class TripRunner {

    public static void main(String[] args) throws Exception {
        RoadMap rm = new RoadMap("attractions.csv", "roads.csv");
        int root = rm.lookUpCity("San Francisco CA");
        List<String> routes = new ArrayList<>(3);
        routes.add("Grand Canyon");
        routes.add("USS Midway Museum");
        routes.add("Haystack Rock");
        System.out.println(rm.route("San Francisco CA", "Sacramento CA", routes));
    }
}
