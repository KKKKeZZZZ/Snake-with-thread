import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class BorderMap {
    // store each snakes' location as a concurrent object
    private ConcurrentHashMap<String, LinkedList<BodyPart>> mapSnakes = new ConcurrentHashMap<>();
    // store each node in the map as a concurrent object
    private ConcurrentHashMap<Node, String> locationMap = new ConcurrentHashMap<>(); //if value is 0 then there's no one in that node.
    private int borderX;
    private int borderY;

    /**
     * Getter methods for x and y axis
     *
     */
    public int getBorderX() {
        return borderX;
    }

    public int getBorderY() {
        return borderY;
    }

    /**
     * Getter methods for collections that store snake location
     *
     */
    public ConcurrentHashMap<String, LinkedList<BodyPart>> getMapSnakes() {
        return mapSnakes;
    }

    public ConcurrentHashMap<Node, String> getLocationMap() {
        return locationMap;
    }


    /**
     * Constructor, populates LocationMap collection during construction with dummy variables
     * @param xCoords
     * @param yCoords
     */
    public BorderMap(int xCoords, int yCoords) {
        borderX = xCoords;
        borderY = yCoords;
        // range the map. if the location is not used. has the id as "0"
        for(int i = 0; i < yCoords; i++){
            locationMap.put(new Node(i,0), "0");
            for(int k = 0; k < xCoords; k++){
                locationMap.put(new Node(i,k),"0");
            }
        }


    }

    /**
     * Populates collections with snake
     * @param playerID
     */
    public void populateCell(Snake playerID){
        Random randomGenerator = new Random();

        int x = randomGenerator.nextInt(this.getBorderX()-15) + 1;
        int y = randomGenerator.nextInt(this.getBorderY()-15) + 1;

        System.out.println("\n\n\n\nCurrent x is: " + x + " Current y is: " + y + "\n\n\n\n\n");

        BodyPart headPart = new BodyPart(true, new Node(x, y));
        playerID.getBodyParts().add(headPart);
        // if the body part of the snake will place to a location that is used. re place it
        if(locationMap.get(new Node(x-1,y-1)) != "0") populateCell(playerID);

        //four bodypart for each snake
        playerID.addBodyPart(this);
        playerID.addBodyPart(this);
        playerID.addBodyPart(this);
//        playerID.addBodyPart(this);

        //put the location of snake's bodypart to the concurrent hash map with id as the key
        locationMap.put(new Node(x,y), playerID.getCredentials());
        locationMap.put(new Node(playerID.getBodyParts().get(1).getCurrentLocation().getX(),
                            playerID.getBodyParts().get(1).getCurrentLocation().getY()),
                                playerID.getCredentials());
        // put the whole body of a snake to the concurrent hash map with its id as a key
        mapSnakes.put(playerID.getCredentials(), playerID.getBodyParts());
    };

    @Override
    public String toString() {
        return "BorderMap{ " +
                "mapSnakes Size: " + mapSnakes.size() + " }";
    }
}

//question, Does locationMap need to be concurrent so players could interact with it? Like concurrentHashMap and etc.