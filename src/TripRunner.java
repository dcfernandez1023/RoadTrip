import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/*
    Driver code for the project.
*/
public class TripRunner {

    public static void main(String[] args) throws Exception {
        RoadMap rm = new RoadMap("attractions.csv", "roads.csv");
        List<String> routes = new ArrayList<>(3);
        routes.add("Portland City Tour");
        routes.add("The Field of Dreams Filming Locale");
        routes.add("Hilton Head");
        List<String> route = rm.route("San Francisco CA", "Abilene TX", routes);
        System.out.println("Route: " + route);
        System.out.println("Distance: " + rm.getDistanceTraveled(route) + " miles");
    }
}
