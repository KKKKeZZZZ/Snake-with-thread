import org.junit.Assert;
import org.junit.Test;

public class SnakeTest {
    //Instantiating essential variables.
    BorderMap mapTest = new BorderMap(50,50);
    Server testServer = new Server(5);
    Snake snakeTest = new Snake("123456", testServer);


    @Test
    public void testSnakePopulationOnMap(){
        mapTest.populateCell(snakeTest);

        Assert.assertEquals("X must be 50",50, mapTest.getBorderX(),1);
        Assert.assertEquals("Y must be 50",50, mapTest.getBorderY(),1);

        Assert.assertEquals("Should be 123456", true, mapTest.getLocationMap().contains("123456"));
    }

    @Test
    public void snakeTestConcurrency(){
        Thread t1 = new Thread(new Snake("1234", testServer));
        t1.run();
    }

    @Test
    public void testProducerConsumer(){
        System.out.println("Current Size of the collection on the Server: " + testServer.getSnakes().size());
        System.out.println(testServer.getBorderMap().toString());

        Thread client1 = new Thread(new Client(testServer, "test1", "abc123"));
        Thread client2 = new Thread(new Client(testServer, "test2", "abc456"));
        Thread client3 = new Thread(new Client(testServer, "test2", "abc456"));
        Thread client4 = new Thread(new Client(testServer, "test2", "abc456"));

        Thread serverHelper1 = new Thread(new ServerHelperClass(testServer));
        //Thread serverHelper2 = new Thread(new ServerHelperClass(testServer));

        client1.run();
        client2.run();
        client3.run();
        client4.run();

        serverHelper1.run();
        //serverHelper2.run();
        try{
            client1.join();
            client2.join();
            client3.join();
            client4.join();

            serverHelper1.join();
            //serverHelper2.join();
        }catch(Exception e){e.printStackTrace();}

        System.out.println("Current Size of the collection on the Server: " + testServer.getSnakes().size());
        System.out.println(testServer.getBorderMap().toString());

        for(Snake x : testServer.getSnakes().values()){
            System.out.println("Current snake: " + x.getCredentials());
        }
    }
}
