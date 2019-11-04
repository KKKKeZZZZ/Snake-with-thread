import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.*;

public class Server implements Runnable{
    private static LoginBuffer serverSiteBuffer = new LoginBuffer();
    private static BorderMap map = new BorderMap(250,250);
    private final ExecutorService pool;
    private int generateTotalSnakes = 0;
    private HashMap<String, Snake> snakes = new HashMap();
    // the database creating
    private static DB db = DBMaker.newFileDB(new File("loginDetails"))
            .closeOnJvmShutdown()
            .encryptionEnable("password")
            .make();

    public Server(int numberOfSnakes) {
        serverSiteBuffer = new LoginBuffer();
        ConcurrentNavigableMap<String, String> loginData = db.getTreeMap("myLoginDetails");
        loginData.put("test1", "abc123");
        loginData.put("test2", "abc456");
        db.commit();

        generateTotalSnakes = numberOfSnakes >= 201 ? 300 : numberOfSnakes;

        pool = Executors.newFixedThreadPool(400);
    }

    public static LoginBuffer getServerSiteBuffer() {
        return serverSiteBuffer;
    }

    public static DB getDb(){ return db; }

    public synchronized HashMap<String, Snake> getSnakes() {
        return snakes;
    }

    public BorderMap getBorderMap() {
        return map;
    }
    // once snake is dead
    public synchronized void removeFromHashMap(String credentials){
        snakes.remove(credentials);

    }


    public void run(){
        for(int i = 0; i < generateTotalSnakes; i++){
            pool.execute(new Client(this,"test1", "abc123"));
        }

        ArrayList<Future> listOfFutures = new ArrayList<Future>();

        listOfFutures.add(pool.submit(new ServerHelperClass(this)));
        listOfFutures.add(pool.submit(new ServerHelperClass(this)));
        //listOfFutures.add(pool.submit(new ServerHelperClass(this)));

        for(Future iter : listOfFutures){
            try {
                iter.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        GameState gs = new GameState(this);

        gs.init();
        gs.mainLoop();
    }
}



