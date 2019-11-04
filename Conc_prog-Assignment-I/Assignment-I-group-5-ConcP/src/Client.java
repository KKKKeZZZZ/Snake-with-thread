import java.util.UUID;

/**
 * Sends infoStore data to the LoginBuffer
 */
public class Client implements Runnable {
    private static Server server;
    private String usrname;
    private String pssword;

    public Client(Server srvr, String username, String password) {
        //clientSiteBuffer = clientSiteBuffer;
        server = srvr;
        usrname = username;
        pssword = password;
    }

    public void run(){
        // once the thread is runing. it will generate an UUID for this inforstroe and sent it to the buffer
        InfoStore myInfoStore = new InfoStore(UUID.randomUUID().toString(), usrname, pssword);
        long threadId = Thread.currentThread().getId();

        System.out.println("Running Client ID: " + threadId);
        server.getServerSiteBuffer().loginRequest(myInfoStore);
    }

}

