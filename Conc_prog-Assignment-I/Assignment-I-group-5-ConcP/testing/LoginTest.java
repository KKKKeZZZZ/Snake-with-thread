import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoginTest {
    @Mock
    Server server;
    @Mock
    Client client;

    @Test
    public void clientTest(){
        client = new Client(server,"test1","abc123");
        Thread thread1 = new Thread(client);
        thread1.run();
        Assert.assertFalse("Should be false, but ",server.getServerSiteBuffer().isEmpty());
    }
    @Test
    public void helperTest(){
        client = new Client(server,"test1","abc123");
        server = new Server(1);
        Thread clientThread = new Thread(client);
        clientThread.run();
        Thread helperThread = new Thread(new ServerHelperClass(server));
        helperThread.run();
        Assert.assertFalse("Should be false, but ", server.getBorderMap().getMapSnakes().isEmpty());
    }

}
