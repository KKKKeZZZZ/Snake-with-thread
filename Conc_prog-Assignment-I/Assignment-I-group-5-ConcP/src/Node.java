public class Node {
    private int x;
    private int y;
    //private String occupantID; //i don't think we need an occupant ID

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    //copy constructor
    public Node(Node copy){
        this.x = copy.getX();
        this.y = copy.getY();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    //equals method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;

        Node node = (Node) o;

        if (x != node.x) return false;
        return y == node.y;

    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return "Node{" + "x=" + x + ", y=" + y + "} \n";
    }
}
