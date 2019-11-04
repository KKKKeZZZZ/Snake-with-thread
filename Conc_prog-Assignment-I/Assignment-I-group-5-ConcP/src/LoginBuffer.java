import java.util.Stack;

public class LoginBuffer {
    private static int maxSize = 200;
    private Stack<InfoStore> loginQueue = new Stack<InfoStore>();

    public LoginBuffer() {
        //loginQueue.setSize(maxSize);s
    }
    // put the login request into the stack of the buffer
    public synchronized void loginRequest(InfoStore credentials){
        try{
            System.out.println("Current stack size is: " + loginQueue.size());
            while(loginQueue.size() == maxSize){// if the request is full. wait
                //delay();
                wait();
            }
        }catch(Exception e) { e.printStackTrace(); }
        loginQueue.push(credentials);

        this.notifyAll();
    }
    // process the login request in the buffer
    public synchronized InfoStore processLoginRequest(){
        try{
            // if no request in the buffer, wait
            System.out.println("Extracting Data from stack: " + loginQueue.size());
            while(loginQueue.size() == 0){
                System.out.println("Restart the program.");
                wait();
            }
        }catch (Exception e ) { e.printStackTrace();}
        //temp variable
        InfoStore temp = loginQueue.pop();
        // notify all
        notifyAll();
        //return
        return temp;
    }

    public boolean isEmpty(){
        return loginQueue.size() == 0;
    }
}
