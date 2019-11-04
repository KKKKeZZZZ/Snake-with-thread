public class ServerHelperClass implements Runnable {
    private static Server server;

    public ServerHelperClass(Server srvr){
        server = srvr;
    }

    //Delay method
    private boolean delay() {
        int actualDelay;
        try {
            // thread to sleep for random milliseconds
            actualDelay = randomWithRange(0,5000);
            //System.out.println("Actual delay is "+actualDelay);
            Thread.sleep(actualDelay);

        } catch (Exception e) {
            System.out.println(e);
        }

        return server.getServerSiteBuffer().isEmpty();

    }

    private int randomWithRange(int min, int max)
    {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }

    public void run(){ //Q5: is this even correct?
        InfoStore check;
        boolean emptyBuffCheck = false;

        while(!emptyBuffCheck){
            check = server.getServerSiteBuffer().processLoginRequest();
            // check if the login request match the infomation in the db
            if(server.getDb().getTreeMap("myLoginDetails").containsKey(check.getLogin()) &&
                    server.getDb().getTreeMap("myLoginDetails").get(check.getLogin()).equals(check.getPassword())){
                Snake newPlayer = new Snake(check.getId(), server);

                server.getBorderMap().populateCell(newPlayer);
                server.getSnakes().put(check.getId(), newPlayer);

                System.out.println("Running Server Helper ID: " + Thread.currentThread().getId());
            }

            if(server.getServerSiteBuffer().isEmpty()){
                emptyBuffCheck = delay();

            }
        }

        System.out.println("Done running, SHC ID: " + Thread.currentThread().getId());
    }

    //it authenticates and pulls data out of the buffer and instantiates snake objects
}
