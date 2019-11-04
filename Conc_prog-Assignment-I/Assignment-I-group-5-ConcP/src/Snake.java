import java.util.LinkedList;
import java.util.Random;

public class Snake implements Runnable {
    private String credentials;
    private LinkedList<BodyPart> bodyParts = new LinkedList<>();
    private static Server server;
    private boolean deadSnake = false;
    private int lastMove = 5;
    private static final int UP = 1;
    private static final int DOWN = 2;
    private static final int LEFT = 3;
    private static final int RIGHT = 4;


    //getters
    public LinkedList<BodyPart> getBodyParts(){
        return bodyParts;
    }

    public String getCredentials() {
        return credentials;
    }

    //constructor
    public Snake(String credentials, Server srvr) {
        Random randomColour= new Random();

        this.credentials = credentials;

        server = srvr;
    }


    /**
     * adds a body part to the Linked List of bParts.
     * Probably need to sync it as well.
     * @param map
     */
    //+++++++++++++++ functionality methods +++++++++++++++
    public void addBodyPart(BorderMap map){
        int xPos = bodyParts.getLast().getCurrentLocation().getX();
        int yPos = bodyParts.getLast().getCurrentLocation().getY();

        if(map.getLocationMap().get(new Node(xPos+1,yPos)) == null) System.out.println("I am null: " + xPos +" "+ yPos);

        //NOTE: SOMETIMES THROWS A NULL POINTER EXCEPTION!!!
        if(xPos+1 <= map.getBorderX() && map.getLocationMap().get(new Node(xPos+1,yPos)).equals("0")){
            bodyParts.addLast(new BodyPart(false, new Node(xPos+1, yPos)));
            return;
        }

        if(map.getLocationMap().get(new Node(xPos,yPos-1)).equals("0") && yPos-1 >= 0){
            bodyParts.addLast(new BodyPart(false, new Node(xPos, yPos-1)));
            return;
        }

        if(map.getLocationMap().get(new Node(xPos, yPos+1)).equals("0") && yPos+1 <= map.getBorderY()){
            bodyParts.addLast(new BodyPart(false, new Node(xPos, yPos+1)));
            return;
        }

        //if every single cell is occupied then just don't add a body part.
    }

    /**
     * removes a body part that matches a specified location, if no body part holds said location, then just do not remove
     * anything.
     * QUESTION: could this potentially cause concurrency issues? Such as : A snake is about to eat another snake but
     * during iteration through body parts, a snake moved. Hence updating all the location variables of all body parts.
     * Could that be a potential issue?
     * Solution: it's all fine. Call wait in move method after the collisions are done, all of it would be accomodated
     * for.
     *
     * @param occNode
     */
    public void removeBodyPart(Node occNode){
        BodyPart toRemove = null;
        for(BodyPart iter : bodyParts){
            if(iter.getCurrentLocation().equals(occNode)) toRemove = iter;
        }

        bodyParts.remove(toRemove);
    }


    //TESTING:
    public void move(){

        Random ranGen = new Random();

        int currentDirection = ranGen.nextInt(4) + 1;
        // most of the time. the snake will keep last direction
        if (currentDirection > 1){
            currentDirection = lastMove;
        }
        else{
            currentDirection = ranGen.nextInt(4)+ 1;
        }

        if (lastMove == 5) lastMove = currentDirection;

        if (currentDirection == lastMove){
            //System.out.println("keep going");
        }else{
            // move opposite is not allowed
            switch (lastMove){
                case UP:
                    while (currentDirection == DOWN){
                        currentDirection = ranGen.nextInt(4) + 1;
                    }
                    break;

                case DOWN:
                    while(currentDirection == UP){
                        currentDirection = ranGen.nextInt(4) + 1;
                    }
                    break;
                case LEFT:
                    while(currentDirection == RIGHT){
                        currentDirection = ranGen.nextInt(4) + 1;
                    }
                    break;
                case RIGHT:
                    while(currentDirection == LEFT){
                        currentDirection = ranGen.nextInt(4) + 1;
                    }
                    break;
            }
        }

        switch (currentDirection){
            case UP:
                // check for death. our rules is touch your own body, you will not die
                if(server.getBorderMap().getLocationMap().get(new Node(this.getBodyParts().getFirst().getCurrentLocation().getX(),
                        this.getBodyParts().getFirst().getCurrentLocation().getY() - 1)) != "0" &&
                        server.getBorderMap().getLocationMap().get(new Node(this.getBodyParts().getFirst().getCurrentLocation().getX(),
                                this.getBodyParts().getFirst().getCurrentLocation().getY() - 1)) != this.getCredentials()){
                    // remove from bordermap chm
                        ///////////Grow test//////////
                    try{
                        // the snake who was collied will grow a body part
                        server.getSnakes().get(server.getBorderMap().getLocationMap().get(new Node(this.getBodyParts().getFirst().getCurrentLocation().getX(),
                                this.getBodyParts().getFirst().getCurrentLocation().getY() - 1))).addBodyPart(server.getBorderMap());
                    }catch (Exception e){

                    }

                    server.getBorderMap().getMapSnakes().remove(this.getCredentials());

                    // remove from node map
                    for(BodyPart part : this.getBodyParts()){
                        server.getBorderMap().getLocationMap().remove(part.getCurrentLocation());
                        server.getBorderMap().getLocationMap().put(part.getCurrentLocation(), "0");
                    }

                    //remove from the server snake
                    server.removeFromHashMap(this.getCredentials());

                    //kill a snake
                    deadSnake = true;
                }
                else{
                    for (int i = this.getBodyParts().size()-1; i >= 0; i--) {
//                        y = this.getBodyParts().get(i).getCurrentLocation().getY();
//                        x = this.getBodyParts().get(i).getCurrentLocation().getX() + 1;
                        // the tail
                        if (i == this.getBodyParts().size() - 1){
                            server.getBorderMap().getLocationMap().remove(this.getBodyParts().get(i));
                            server.getBorderMap().getLocationMap().put(new Node(this.getBodyParts().get(i).getCurrentLocation().getX(),
                                    this.getBodyParts().get(i).getCurrentLocation().getY()),"0");
                            this.getBodyParts().get(i).setCurrentLocation(this.getBodyParts().get(i - 1).getCurrentLocation());
                        }
                        // the head
                        else if (i == 0){
                            server.getBorderMap().getLocationMap().put(new Node(this.getBodyParts().get(i).getCurrentLocation().getX(),
                                    this.getBodyParts().get(i).getCurrentLocation().getY() - 1),this.getCredentials());
                            this.getBodyParts().get(i).setCurrentLocation(new Node(this.getBodyParts().get(i).getCurrentLocation().getX(),
                                    this.getBodyParts().get(i).getCurrentLocation().getY() - 1));

                        }
                        else{
                            this.getBodyParts().get(i).setCurrentLocation(this.getBodyParts().get(i - 1).getCurrentLocation());
                        }

                    }
                    server.getBorderMap().getMapSnakes().remove(this.getCredentials());
                    server.getBorderMap().getMapSnakes().put(this.getCredentials(), this.getBodyParts());
                }
                break;
            case LEFT:
                if(server.getBorderMap().getLocationMap().get(new Node(this.getBodyParts().getFirst().getCurrentLocation().getX() - 1,
                        this.getBodyParts().getFirst().getCurrentLocation().getY())) != "0"&& server.getBorderMap().getLocationMap().get(new Node(this.getBodyParts().getFirst().getCurrentLocation().getX() - 1,
                        this.getBodyParts().getFirst().getCurrentLocation().getY())) != this.getCredentials()){
                    //should death
                    // remove from bordermap chm

                    try {
                        server.getSnakes().get(server.getBorderMap().getLocationMap().get(new Node(this.getBodyParts().getFirst().getCurrentLocation().getX() - 1,
                                this.getBodyParts().getFirst().getCurrentLocation().getY()))).addBodyPart(server.getBorderMap());
                    }catch (Exception e){

                    }
                    // remove from node map
                server.getBorderMap().getMapSnakes().remove(this.getCredentials());
                for(BodyPart part : this.getBodyParts()){
                        server.getBorderMap().getLocationMap().remove(part.getCurrentLocation());
                        server.getBorderMap().getLocationMap().put(part.getCurrentLocation(), "0");
                    }

                    //remove from the server snake
                    server.removeFromHashMap(this.getCredentials());
                    deadSnake = true;
                }
                else{
                    for (int i = this.getBodyParts().size()-1; i >= 0; i--) {
                        if (i == this.getBodyParts().size() - 1){
                            server.getBorderMap().getLocationMap().remove(this.getBodyParts().get(i));
                            server.getBorderMap().getLocationMap().put(new Node(this.getBodyParts().get(i).getCurrentLocation().getX(),
                                    this.getBodyParts().get(i).getCurrentLocation().getY()),"0");
                            this.getBodyParts().get(i).setCurrentLocation(this.getBodyParts().get(i - 1).getCurrentLocation());
                        }
                        // the head
                        else if (i == 0){
                            server.getBorderMap().getLocationMap().put(new Node(this.getBodyParts().get(i).getCurrentLocation().getX() -1,
                                    this.getBodyParts().get(i).getCurrentLocation().getY()),this.getCredentials());
                            this.getBodyParts().get(i).setCurrentLocation(new Node(this.getBodyParts().get(i).getCurrentLocation().getX() - 1,
                                    this.getBodyParts().get(i).getCurrentLocation().getY()));

                        }
                        else{
                            this.getBodyParts().get(i).setCurrentLocation(this.getBodyParts().get(i - 1).getCurrentLocation());
                        }

                    }
                    server.getBorderMap().getMapSnakes().remove(this.getCredentials());
                    server.getBorderMap().getMapSnakes().put(this.getCredentials(), this.getBodyParts());
                }
                break;
            case RIGHT:
                if(server.getBorderMap().getLocationMap().get(new Node(this.getBodyParts().getFirst().getCurrentLocation().getX() + 1,
                        this.getBodyParts().getFirst().getCurrentLocation().getY())) != "0"&& server.getBorderMap().getLocationMap().get(new Node(this.getBodyParts().getFirst().getCurrentLocation().getX() + 1,
                        this.getBodyParts().getFirst().getCurrentLocation().getY())) != this.getCredentials()){
                    //should death
                    // remove from bordermap chm

                    try {
                        server.getSnakes().get(server.getBorderMap().getLocationMap().get(new Node(this.getBodyParts().getFirst().getCurrentLocation().getX() + 1,
                                this.getBodyParts().getFirst().getCurrentLocation().getY()))).addBodyPart(server.getBorderMap());
                    }catch (Exception e){

                    }
                    server.getBorderMap().getMapSnakes().remove(this.getCredentials());
                    // remove from node map
                    for(BodyPart part : this.getBodyParts()){
                        server.getBorderMap().getLocationMap().remove(part.getCurrentLocation());
                        server.getBorderMap().getLocationMap().put(part.getCurrentLocation(), "0");
                    }

                    //remove from the server snake
                    server.removeFromHashMap(this.getCredentials());
                    deadSnake = true;
                }
                else{
                    for (int i = this.getBodyParts().size()-1; i >= 0; i--) {
                        // the tail
                        if (i == this.getBodyParts().size() - 1){
                            server.getBorderMap().getLocationMap().remove(this.getBodyParts().get(i));
                            server.getBorderMap().getLocationMap().put(new Node(this.getBodyParts().get(i).getCurrentLocation().getX(),
                                    this.getBodyParts().get(i).getCurrentLocation().getY()),"0");
                            this.getBodyParts().get(i).setCurrentLocation(this.getBodyParts().get(i - 1).getCurrentLocation());
                        }
                        // the head
                        else if (i == 0){
                            server.getBorderMap().getLocationMap().put(new Node(this.getBodyParts().get(i).getCurrentLocation().getX() + 1,
                                    this.getBodyParts().get(i).getCurrentLocation().getY()),this.getCredentials());
                            this.getBodyParts().get(i).setCurrentLocation(new Node(this.getBodyParts().get(i).getCurrentLocation().getX() + 1,
                                    this.getBodyParts().get(i).getCurrentLocation().getY()));

                        }
                        else{
                            this.getBodyParts().get(i).setCurrentLocation(this.getBodyParts().get(i - 1).getCurrentLocation());
                        }

                    }
                    server.getBorderMap().getMapSnakes().remove(this.getCredentials());
                    server.getBorderMap().getMapSnakes().put(this.getCredentials(), this.getBodyParts());
                }
                break;
            case DOWN:
                if(server.getBorderMap().getLocationMap().get(new Node(this.getBodyParts().getFirst().getCurrentLocation().getX(),
                        this.getBodyParts().getFirst().getCurrentLocation().getY() + 1)) != "0"  && server.getBorderMap().getLocationMap().get(new Node(this.getBodyParts().getFirst().getCurrentLocation().getX(),
                        this.getBodyParts().getFirst().getCurrentLocation().getY() + 1)) != this.getCredentials()){
                    //should death
                    // remove from bordermap chm



                    ///////////Grow test//////////
                    try {
                        server.getSnakes().get(server.getBorderMap().getLocationMap().get(new Node(this.getBodyParts().getFirst().getCurrentLocation().getX(),
                                this.getBodyParts().getFirst().getCurrentLocation().getY() + 1))).addBodyPart(server.getBorderMap());
                    }catch (Exception e){
                        //////////////////////////////
                    }
                    // remove from node map
                    server.getBorderMap().getMapSnakes().remove(this.getCredentials());
                    for(BodyPart part : this.getBodyParts()){
                        server.getBorderMap().getLocationMap().remove(part.getCurrentLocation());
                        server.getBorderMap().getLocationMap().put(part.getCurrentLocation(), "0");
                    }

                    //remove from the server snake
                    server.removeFromHashMap(this.getCredentials());
                    deadSnake = true;
                }
                else{
                    for (int i = this.getBodyParts().size()-1; i >= 0; i--) {
                        // the tail
                        if (i == this.getBodyParts().size() - 1){
                            server.getBorderMap().getLocationMap().remove(this.getBodyParts().get(i));
                            server.getBorderMap().getLocationMap().put(new Node(this.getBodyParts().get(i).getCurrentLocation().getX(),
                                    this.getBodyParts().get(i).getCurrentLocation().getY()),"0");
                            this.getBodyParts().get(i).setCurrentLocation(this.getBodyParts().get(i - 1).getCurrentLocation());
                        }
                        // the head
                        else if (i == 0){
                            server.getBorderMap().getLocationMap().put(new Node(this.getBodyParts().get(i).getCurrentLocation().getX(),
                                    this.getBodyParts().get(i).getCurrentLocation().getY() + 1),this.getCredentials());
                            this.getBodyParts().get(i).setCurrentLocation(new Node(this.getBodyParts().get(i).getCurrentLocation().getX(),
                                    this.getBodyParts().get(i).getCurrentLocation().getY() + 1));

                        }
                        else{
                            this.getBodyParts().get(i).setCurrentLocation(this.getBodyParts().get(i - 1).getCurrentLocation());
                        }

                    }
                    server.getBorderMap().getMapSnakes().remove(this.getCredentials());
                    server.getBorderMap().getMapSnakes().put(this.getCredentials(), this.getBodyParts());
                }
                break;
        }

        //assign last move
        lastMove = currentDirection;
    }

    //END TESTING

    //+++++++++++++++ concurrency +++++++++++++++
    public void run(){
        while(!deadSnake){
            try{
                Thread.sleep(200);
            }catch (InterruptedException e){
                System.out.println(e);
            }
            this.move();

        }
        System.out.println(Thread.currentThread().getId()+ "I am dead");


    }

}
