public class Main {
    public static void main(String[] args) {
        System.out.println("Initiating the server...");
        Thread serverThread = new Thread(new Server(96));

        serverThread.start();
    }
}
