import org.junit.Test;

public class UITests {
    Server testServer = new Server(5);

    @Test
    public void testDrawUI(){
        Thread client1 = new Thread(new Client(testServer, "test1", "abc123"));
        Thread client2 = new Thread(new Client(testServer, "test2", "abc456"));
        Thread client3 = new Thread(new Client(testServer, "test2", "abc456"));
        Thread client4 = new Thread(new Client(testServer, "test2", "abc456"));
        Thread client5 = new Thread(new Client(testServer, "test2", "abc456"));
        Thread client6 = new Thread(new Client(testServer, "test2", "abc456"));
        Thread client7 = new Thread(new Client(testServer, "test2", "abc456"));
        Thread client8 = new Thread(new Client(testServer, "test2", "abc456"));
        Thread client9 = new Thread(new Client(testServer, "test2", "abc456"));
        Thread client10 = new Thread(new Client(testServer, "test2", "abc456"));
        Thread client11 = new Thread(new Client(testServer, "test2", "abc456"));
        Thread client12 = new Thread(new Client(testServer, "test2", "abc456"));
        Thread client13 = new Thread(new Client(testServer, "test2", "abc456"));

        //Thread ui = new Thread(new MapUI(testServer));

        Thread serverHelper1 = new Thread(new ServerHelperClass(testServer));
        //Thread serverHelper2 = new Thread(new ServerHelperClass(testServer));

        client1.start();
        client2.start();
        client3.start();
        client4.start();
        client5.start();
        client6.start();
        client7.start();
        client8.start();
        client9.start();
        client10.start();
        client11.start();
        client12.start();
        client13.start();


        serverHelper1.start();
        GameState gs = new GameState(testServer);

        gs.init();
        gs.mainLoop();

        //serverHelper2.run();
        try{
            client1.join();
            client2.join();
            client3.join();
            client4.join();
            client5.join();
            client6.join();
            client7.join();
            client8.join();
            client9.join();
            client10.join();
            client11.join();
            client12.join();
            client13.join();

            serverHelper1.join();
            //ui.join();
            //serverHelper2.join();
        }catch(Exception e){e.printStackTrace();}

    }

}
