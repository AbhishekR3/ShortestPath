import java.util.*;

import java.lang.Math;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.LineNumberReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;

import java.nio.file.*;


public class ShortestDistanceDijkstra { 
	//Graph linking
	public static List<List<Node>> adjacentCities; //Adjacent values (Overall Graph)
	private int dist[]; //Distance from source
	private Set<Integer> settled; //Values settled with
	private PriorityQueue<Node> pq; //Priority Queue
	private static int V; // Number of vertices (#Cities) -- 291 in the dataset

	//Attraction, Location
	public static ArrayList<ArrayList<String>> tAttractions = new ArrayList<ArrayList<String>>();
	public static ArrayList<String> attractionElement;

	//Initial Location, Final Location, Distance, Time
	public static ArrayList<ArrayList<String>> tRoads = new ArrayList<ArrayList<String>>();
	public static ArrayList<String> roadElement;
	//public static ArrayList<String> roadElementSwap;

	//Unique Cities
	static ArrayList<String> uniqueCitiesList = new ArrayList<String>();

	//Number of Paths
	public static int countRoadPath;
  
	public ShortestDistanceDijkstra(int V) { 
		this.V = V; 
		dist = new int[V]; 
		settled = new HashSet<Integer>(); 
		pq = new PriorityQueue<Node>(V, new Node()); 
	} 
  
	//Place Road Values into list
	public static void roadPath() throws java.io.IOException {
		Path fileRoadPath = Paths.get("roads.csv");
		long fileRoadPathLength = Files.lines(fileRoadPath).count();
		countRoadPath = Math.toIntExact(fileRoadPathLength);

		try (BufferedReader reader = new BufferedReader(new FileReader("roads.csv"))) {
			for (int i = 0; i < countRoadPath; i++) {
				String line = Files.readAllLines(Paths.get("roads.csv")).get(i);
				roadElement = new ArrayList<>(Arrays.asList(line.split(",")));
				tRoads.add(roadElement);
			}
		} 

		catch (IOException e) {
			e.printStackTrace();
		}
	}

	//Place Attraction Values into list
	public static void placesVisitng() throws java.io.IOException {
		Path filePlacesVisiting = Paths.get("attractions.csv");
		long placesVisitingLength = Files.lines(filePlacesVisiting).count();
		int countPlacesVisitng = Math.toIntExact(placesVisitingLength);

		try (BufferedReader reader = new BufferedReader(new FileReader("attractions.csv"))) {
			for (int i = 1; i < countPlacesVisitng; i++) {
				String line = Files.readAllLines(Paths.get("attractions.csv")).get(i);

				attractionElement = new ArrayList<>(Arrays.asList(line.split(",")));
				tAttractions.add(attractionElement);
			}
		} 

		catch (IOException e) {
			e.printStackTrace();
		}
	}

	//Get City Value for a certain City
	private static int cityVal(String city, ArrayList<String> uniqueList) {
		for (int ithVal = 0; ithVal < uniqueList.size(); ithVal++) {
			if (city.equals(uniqueList.get(ithVal))) {
					return ithVal;
				}
		}

		return -1;
	}

	//Get city location of an attraction
	static String attractionLocation(String attraction){
		for (int i = 0; i < tAttractions.size(); i++){
			if (attraction.equals(tAttractions.get(i).get(0))){
				return tAttractions.get(i).get(1);
			}
		}

		return null;
	}

	//Distance betweeen start city and end city
	static int distanceValue(String startCity, String endCity){
		for (int i = 0; i < tRoads.size(); i++){
			if (startCity.equals(tRoads.get(i).get(0))){
					if (endCity.equals(tRoads.get(i).get(1))){
						return Integer.parseInt(tRoads.get(i).get(2));
					}
			}
		}
		return -1;
	}

	// Function for Dijkstra's Algorithm 
	public void dijkstra(List<List<Node>> adjacentCities, int initialCity) 
	{ 
		this.adjacentCities = adjacentCities; 
  
		for (int i = 0; i < V; i++) {
			dist[i] = Integer.MAX_VALUE;
		}

		// Add source node to the priority queue 
		pq.add(new Node(initialCity, 0)); 
  
		// Distance to the source is 0 
		dist[initialCity] = 0; 

		while (pq.size() != 0) {
			int cityNode = pq.remove().node;
			settled.add(cityNode); 
			city_Neighbours(cityNode);
		}
	} 
  
	// Neighbours of the passed node 
	private void city_Neighbours(int u) 
	{ 
		int edgeDistance = -1; 
		int newDistance = -1; 

		// All the neighbors of a certain vertex
		for (int i = 0; i < adjacentCities.get(u).size(); i++) { 
			Node v = adjacentCities.get(u).get(i); 
  
			// Process node if not processed before
			if (!settled.contains(v.node)) { 
				edgeDistance = v.distance; 
				newDistance = dist[u] + edgeDistance; 
  
				// If new distance is lesss than the original distance, replace with the smaller distance
				if (newDistance < dist[v.node]) 
					dist[v.node] = newDistance; 
  
				// Add the current node with x distance to the queue 
				pq.add(new Node(v.node, dist[v.node])); 
			} 
		} 
	} 

	// Main code 
	public static void main(String arg[]) throws java.io.IOException { 
		roadPath();
		placesVisitng();

		//Starting City, Ending City, Attractions to Visit
		String beginning = "Las Vegas NV";
		String end = "San Jose CA";
		String[] attracts = new String[]{"Alcatraz"};

		//Locations of the Cities and Attraction
		String[] requiredCityLocations = new String[attracts.length+2];

		requiredCityLocations[0] = beginning;
		requiredCityLocations[requiredCityLocations.length-1] = end;
		
		for (int iAttraction = 0; iAttraction < attracts.length; iAttraction++){
			requiredCityLocations[iAttraction+1] = attractionLocation(attracts[iAttraction]);
		}
 
		//Cities List -- uniqueCitiesList
		ArrayList citiesList = new ArrayList();
		for (int nn=0; nn < tRoads.size(); nn++){
			citiesList.add(tRoads.get(nn).get(0));
			citiesList.add(tRoads.get(nn).get(1));
		}

		Set<String> uCitiesList = new HashSet<String>(citiesList);
		ArrayList<String> uniqueCitiesList = new ArrayList<String>(uCitiesList.size()); 
		for (String x : uCitiesList){
			uniqueCitiesList.add(x);
		}

		//Set number of vertices as the number of cities
		int V = uniqueCitiesList.size();

		// Adjacency list representation of the connected edges 
		List<List<Node>> adj = new ArrayList<List<Node>>(); 

		// Initialize list for every node 
		for (int i = 0; i < V; i++) { 
			List<Node> item = new ArrayList<Node>(); 
			adj.add(item); 
		}

		// Inputs for the Dijkstra graph
		for (int placement = 0; placement < tRoads.size(); placement++){
			String initialCityVal = tRoads.get(placement).get(0);
			String finalCityVal = tRoads.get(placement).get(1);


			int initially = cityVal(initialCityVal, uniqueCitiesList);
			int finall = cityVal(finalCityVal, uniqueCitiesList);

			int distanceBetweenCities = distanceValue(initialCityVal,finalCityVal);

			//Add nodes so it can go both ways
			adj.get(initially).add(new Node((finall), distanceBetweenCities));
			//adj.get(finall).add(new Node((initially), distanceBetweenCities));
		}

		//Integer values of the attractions
		int[] requiredCityLocationsVals = new int[requiredCityLocations.length];

		for (int vals = 0; vals < requiredCityLocations.length; vals++){
			requiredCityLocationsVals[vals] = cityVal(requiredCityLocations[vals], uniqueCitiesList);
		}

		//Cities to pass through and its respective integer values
		System.out.println("Cities visiting from start to end: " + Arrays.toString(requiredCityLocations));
		
		// Calculate the single source shortest path 
		int tDistance = 0;
		
		ShortestDistanceDijkstra dpq = new ShortestDistanceDijkstra(V); 
		dpq.dijkstra(adj, requiredCityLocationsVals[0]);
		
		//For each city to visit in the list, increase distance travelled
		

		for (int tD = 0; tD < requiredCityLocationsVals.length; tD++){
			int ithValuer = requiredCityLocationsVals[tD];

			//If city(value) doesn't exist, route doesn't exist
			if (dpq.dist[ithValuer] == Integer.MAX_VALUE){
				System.out.println("Can't find a route. Try different cities.");
				System.exit(0);
			}

			// Increase distance travelled by x
			else{
				tDistance = tDistance + dpq.dist[ithValuer];
			}
		}

		System.out.println("The shortest path distance is " + tDistance + " miles");
	}
}

// Class to represent a node in the graph 
class Node implements Comparator<Node> { 
	public int node; 
	public int distance; 

	public Node() { 
	} 

	public Node(int node, int distance) { 
		this.node = node; 
		this.distance = distance; 
	} 
  
	@Override
	public int compare(Node node1, Node node2) { 
		if (node1.distance < node2.distance) 
			return -1; 
		if (node1.distance > node2.distance) 
			return 1; 
		return 0; 
	} 
} 