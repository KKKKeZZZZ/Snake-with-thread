public class BodyPart {
    private boolean head;
    private Node currentLocation;

    /**
     * Body part of a snake that builds a snake.
     * @param head
     * @param currentLocation
     */
    public BodyPart(boolean head, Node currentLocation) {
        this.head = head;
        this.currentLocation = currentLocation;
    }

    public boolean isHead() {
        return head;
    }

    public Node getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Node currentLocation) {
        this.currentLocation = currentLocation;
    }
}
