import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class TripRunner {

    public static void main(String[] args) throws Exception {
        RoadMap rm = new RoadMap("attractions.csv", "roads.csv");
        List<String> routes = new ArrayList<>(3);
        routes.add("Portland City Tour");
        routes.add("The Field of Dreams Filming Locale");
        routes.add("Hilton Head");
        List<String> route = rm.route("San Francisco CA", "Abilene TX", routes);
        System.out.println(route);
        System.out.println(rm.getDistanceTraveled(route));
    }
}
