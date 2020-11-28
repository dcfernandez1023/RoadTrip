import java.util.*;

/*
    Driver code for the project.
*/
public class TripRunner {

    public static void main(String[] args) throws Exception {
        RoadMap rm = new RoadMap("attractions.csv", "roads.csv");
        LinkedList<String> attractions = new LinkedList<>();
        Scanner s = new Scanner(System.in);

        System.out.print("Enter a start city: ");
        String startCity = s.nextLine().trim();

        System.out.print("Enter an end city: ");
        String endCity = s.nextLine().trim();

        System.out.println("Enter attractions you want to visit. When finished, enter 'done'.");
        String attraction = "";
        while(!attraction.equals("done")) {
            System.out.print("Add Attraction: ");
            attraction = s.nextLine().trim();
            if(!attraction.equals("done")) {
                attractions.add(attraction);
                System.out.println("Current attractions: " + attractions);
            }
        }

        //List<String> routes = new ArrayList<>(3);
        //routes.add("Portland City Tour");
        //routes.add("The Field of Dreams Filming Locale");
        //routes.add("Hilton Head");
        System.out.println("---- Results ----");
        List<String> route = rm.route(startCity, endCity, attractions);
        System.out.println("Route: " + route);
        System.out.println("Distance: " + rm.getDistanceTraveled(route) + " miles");
    }
}
