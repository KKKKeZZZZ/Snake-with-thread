import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * @author Peuch
 */
public class GameState implements KeyListener, WindowListener {
    //Variable declaration:

//  Executor
    private final ExecutorService pool;

//     KEYS MAP
    public final static int UP = 0;
    public final static int DOWN = 1;
    public final static int LEFT = 2;
    public final static int RIGHT = 3;

    // GRID CONTENT
    public final static int EMPTY = 0;
    public final static int SNAKE = 4;
    public final static int WALL = 5;


    //player snakes
    ConcurrentHashMap<String, ArrayList<Node>> playerSnakesLocs = new ConcurrentHashMap<>();

    //player snake directions
    private int direction_p1 = -1;
    private int next_direction_p1 = -1;
    private int direction_p2 = -1;
    private int next_direction_p2 = -1;
    private int direction_p3 = -1;
    private int next_direction_p3 = -1;
    private int direction_p4 = -1;
    private int next_direction_p4 = -4;
    private int height = 760;
    private int width = 760;
    private int gameSize = 250;
    private int player = 4;
    private long speed = 150;
    private Frame frame = null;
    private Canvas canvas = null;
    private Graphics graph = null;
    private BufferStrategy strategy = null;
    private boolean game_over = false;
    private Server server;

    private int second,minute, millisecond = 0; // Clock values
    private long cycleTime = 0;
    private long sleepTime = 0;

    ArrayList<Node> snake1 = new ArrayList<>();
    ArrayList<Node> snake2 = new ArrayList<>();
    ArrayList<Node> snake3 = new ArrayList<>();
    ArrayList<Node> snake4 = new ArrayList<>();

    public GameState(Server test) {
        super();
        server = test;
        frame = new Frame();
        canvas = new Canvas();
        pool = Executors.newFixedThreadPool(400);
        //gameSize = server.getBorderMap().getBorderX();
    }

    public void init() {
        frame.setSize(width + 7, height + 27);
        frame.setResizable(false);
        frame.setLocationByPlatform(true);
        canvas.setSize(width + 7, height + 27);
        frame.add(canvas);
        canvas.addKeyListener(this);
        frame.addWindowListener(this);
        frame.dispose();
        frame.validate();
        frame.setTitle("Snake Game");
        frame.setVisible(true);
        canvas.setIgnoreRepaint(true);
        canvas.setBackground(Color.cyan);

        canvas.createBufferStrategy(2);

        strategy = canvas.getBufferStrategy();
        graph = strategy.getDrawGraphics();

        populateMapWPlayers(gameSize/2);

        renderGame();
    }
    // put the controllable snake to the map
    public void populateMapWPlayers(int middleArea){
        boolean p1 = server.getBorderMap().getLocationMap().get(new Node(middleArea,middleArea)) != "0";
        boolean p2 = server.getBorderMap().getLocationMap().get(new Node(middleArea+4, middleArea)) != "0";
        boolean p3 = server.getBorderMap().getLocationMap().get(new Node(middleArea+8, middleArea)) != "0";
        boolean p4 = server.getBorderMap().getLocationMap().get(new Node(middleArea+12, middleArea)) != "0";

        if(p1 || p2 || p3 || p4){
            populateMapWPlayers(middleArea-1);
        }

        snake1.add(new Node(middleArea, middleArea));
        snake1.add(new Node(middleArea+1, middleArea));
        snake1.add(new Node(middleArea+2, middleArea));
        snake1.add(new Node(middleArea+2, middleArea+1));
        snake1.add(new Node(middleArea+2, middleArea+2));
        snake1.add(new Node(middleArea+2, middleArea+3));

        snake2.add(new Node(middleArea+4, middleArea));
        snake2.add(new Node(middleArea+5, middleArea));
        snake2.add(new Node(middleArea+6, middleArea));
        snake2.add(new Node(middleArea+6, middleArea+1));
        snake2.add(new Node(middleArea+6, middleArea+2));
        snake2.add(new Node(middleArea+6, middleArea+3));

        snake3.add(new Node(middleArea+8, middleArea));
        snake3.add(new Node(middleArea+9, middleArea));
        snake3.add(new Node(middleArea+10, middleArea));
        snake3.add(new Node(middleArea+10, middleArea+ 1));
        snake3.add(new Node(middleArea+10, middleArea+ 2));
        snake3.add(new Node(middleArea+10, middleArea+3));

        snake4.add(new Node(middleArea+12, middleArea));
        snake4.add(new Node(middleArea+13, middleArea));
        snake4.add(new Node(middleArea+14, middleArea));
        snake4.add(new Node(middleArea+14, middleArea+1));
        snake4.add(new Node(middleArea+14, middleArea+2));
        snake4.add(new Node(middleArea+14, middleArea+3));

        playerSnakesLocs.put("playerSnake1", snake1);
        playerSnakesLocs.put("playerSnake2", snake2);
        playerSnakesLocs.put("playerSnake3", snake3);
        playerSnakesLocs.put("playerSnake4", snake4);

        for (int i = 0; i < snake1.size(); i++){
            server.getBorderMap().getLocationMap().put(snake1.get(i),"playerSnake1");
            server.getBorderMap().getLocationMap().put(snake2.get(i),"playerSnake2");
            server.getBorderMap().getLocationMap().put(snake3.get(i),"playerSnake3");
            server.getBorderMap().getLocationMap().put(snake4.get(i),"playerSnake4");
        }
    }

    // move funtion for controllable snke
    public void player4Move(ArrayList<Node> snake){
        if (snake.size() == 0){

        }
        else{
            switch (next_direction_p4) {
                case UP:
                    if (server.getBorderMap().getLocationMap().get(new Node(snake.get(0).getX(),
                            snake.get(0).getY() - 1)) != "0") {
                        // remove from node map
                        for (Node part : snake) {
                            server.getBorderMap().getLocationMap().remove(part);
                            server.getBorderMap().getLocationMap().put(part, "0");
                        }
                        //kill a snake
                        player -=1;
                        snake.clear();

                    } else {
                        for (int i = snake.size() - 1; i >= 0; i--) {
                            // the tail
                            if (i == snake.size() - 1) {
                                server.getBorderMap().getLocationMap().remove(snake.get(i));
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX(),
                                        snake.get(i).getY()), "0");
                                try {
                                    snake.set(i, snake.get(i - 1));

                                } catch (Exception e) {

                                }
                            }
                            // the head
                            else if (i == 0) {
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX(),
                                        snake.get(i).getY() - 1), ("playerSnake4"));/////////////////not set id//////////
                                snake.set(i, new Node(snake.get(i).getX(),
                                        snake.get(i).getY() - 1));

                            } else {
                                snake.set(i, snake.get(i - 1));
                            }

                        }
                    }
                    break;
                case LEFT:
                    if (server.getBorderMap().getLocationMap().get(new Node(snake.get(0).getX() - 1,
                            snake.get(0).getY())) != "0") {
                        // remove from node map
                        for (Node part : snake) {
                            server.getBorderMap().getLocationMap().remove(part);
                            server.getBorderMap().getLocationMap().put(part, "0");
                        }
                        //kill a snake
                        player -= 1;
                        snake.clear();
                    } else {
                        for (int i = snake.size() - 1; i >= 0; i--) {
                            // the tail
                            if (i == snake.size() - 1) {
                                server.getBorderMap().getLocationMap().remove(snake.get(i));
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX(),
                                        snake.get(i).getY()), "0");
                                try {
                                    snake.set(i, snake.get(i - 1));

                                } catch (Exception e) {

                                }
                            }
                            // the head
                            else if (i == 0) {
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX() - 1,
                                        snake.get(i).getY()), ("playerSnake4"));/////////////////not set id//////////
                                snake.set(i, new Node(snake.get(i).getX() - 1,
                                        snake.get(i).getY()));

                            } else {
                                snake.set(i, snake.get(i - 1));
                            }

                        }
                    }
                    break;
                case RIGHT:
                    if (server.getBorderMap().getLocationMap().get(new Node(snake.get(0).getX() + 1,
                            snake.get(0).getY())) != "0") {
                        // remove from node map
                        for (Node part : snake) {
                            server.getBorderMap().getLocationMap().remove(part);
                            server.getBorderMap().getLocationMap().put(part, "0");
                        }
                        //kill a snake
                        player -= 1;
                        snake.clear();
                    } else {
                        for (int i = snake.size() - 1; i >= 0; i--) {
                            // the tail
                            if (i == snake.size() - 1) {
                                server.getBorderMap().getLocationMap().remove(snake.get(i));
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX(),
                                        snake.get(i).getY()), "0");
                                try {
                                    snake.set(i, snake.get(i - 1));

                                } catch (Exception e) {

                                }
                            }
                            // the head
                            else if (i == 0) {
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX() + 1,
                                        snake.get(i).getY()), ("playerSnake4"));/////////////////not set id//////////
                                snake.set(i, new Node(snake.get(i).getX() + 1,
                                        snake.get(i).getY()));

                            } else {
                                snake.set(i, snake.get(i - 1));
                            }

                        }
                    }
                    break;
                case DOWN:
                    if (server.getBorderMap().getLocationMap().get(new Node(snake.get(0).getX(),
                            snake.get(0).getY() + 1)) != "0") {
                        // remove from node map
                        for (Node part : snake) {
                            server.getBorderMap().getLocationMap().remove(part);
                            server.getBorderMap().getLocationMap().put(part, "0");
                        }
                        //kill a snake
                        player -= 1;
                        snake.clear();
                    } else {
                        for (int i = snake.size() - 1; i >= 0; i--) {
                            // the tail
                            if (i == snake.size() - 1) {
                                server.getBorderMap().getLocationMap().remove(snake.get(i));
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX(),
                                        snake.get(i).getY()), "0");
                                try {
                                    snake.set(i, snake.get(i - 1));

                                } catch (Exception e) {

                                }
                            }
                            // the head
                            else if (i == 0) {
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX(),
                                        snake.get(i).getY() + 1), ("playerSnake4"));/////////////////not set id//////////
                                snake.set(i, new Node(snake.get(i).getX(),
                                        snake.get(i).getY() + 1));

                            } else {
                                snake.set(i, snake.get(i - 1));
                            }

                        }
                    }
                    break;
            }
        }
    }


    public void player3Move(ArrayList<Node> snake){
        if (snake.size() == 0){

        }
        else{
            switch (next_direction_p3) {
                case UP:
                    if (server.getBorderMap().getLocationMap().get(new Node(snake.get(0).getX(),
                            snake.get(0).getY() - 1)) != "0") {
                        // remove from node map
                        for (Node part : snake) {
                            server.getBorderMap().getLocationMap().remove(part);
                            server.getBorderMap().getLocationMap().put(part, "0");
                        }
                        //kill a snake
                        player -= 1;
                        snake.clear();
                    } else {
                        for (int i = snake.size() - 1; i >= 0; i--) {
                            // the tail
                            if (i == snake.size() - 1) {
                                server.getBorderMap().getLocationMap().remove(snake.get(i));
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX(),
                                        snake.get(i).getY()), "0");
                                try {
                                    snake.set(i, snake.get(i - 1));

                                } catch (Exception e) {

                                }
                            }
                            // the head
                            else if (i == 0) {
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX(),
                                        snake.get(i).getY() - 1), ("playerSnake3"));/////////////////not set id//////////
                                snake.set(i, new Node(snake.get(i).getX(),
                                        snake.get(i).getY() - 1));

                            } else {
                                snake.set(i, snake.get(i - 1));
                            }

                        }
                    }
                    break;
                case LEFT:
                    if (server.getBorderMap().getLocationMap().get(new Node(snake.get(0).getX() - 1,
                            snake.get(0).getY())) != "0") {
                        // remove from node map
                        for (Node part : snake) {
                            server.getBorderMap().getLocationMap().remove(part);
                            server.getBorderMap().getLocationMap().put(part, "0");
                        }
                        //kill a snake
                        player -= 1;
                        snake.clear();
                    } else {
                        for (int i = snake.size() - 1; i >= 0; i--) {
                            // the tail
                            if (i == snake.size() - 1) {
                                server.getBorderMap().getLocationMap().remove(snake.get(i));
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX(),
                                        snake.get(i).getY()), "0");
                                try {
                                    snake.set(i, snake.get(i - 1));

                                } catch (Exception e) {

                                }
                            }
                            // the head
                            else if (i == 0) {
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX() - 1,
                                        snake.get(i).getY()), ("playerSnake3"));/////////////////not set id//////////
                                snake.set(i, new Node(snake.get(i).getX() - 1,
                                        snake.get(i).getY()));

                            } else {
                                snake.set(i, snake.get(i - 1));
                            }

                        }
                    }
                    break;
                case RIGHT:
                    if (server.getBorderMap().getLocationMap().get(new Node(snake.get(0).getX() + 1,
                            snake.get(0).getY())) != "0") {
                        // remove from node map
                        for (Node part : snake) {
                            server.getBorderMap().getLocationMap().remove(part);
                            server.getBorderMap().getLocationMap().put(part, "0");
                        }
                        //kill a snake
                        player -= 1;
                        snake.clear();
                    } else {
                        for (int i = snake.size() - 1; i >= 0; i--) {
                            // the tail
                            if (i == snake.size() - 1) {
                                server.getBorderMap().getLocationMap().remove(snake.get(i));
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX(),
                                        snake.get(i).getY()), "0");
                                try {
                                    snake.set(i, snake.get(i - 1));

                                } catch (Exception e) {

                                }
                            }
                            // the head
                            else if (i == 0) {
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX() + 1,
                                        snake.get(i).getY()), ("playerSnake3"));/////////////////not set id//////////
                                snake.set(i, new Node(snake.get(i).getX() + 1,
                                        snake.get(i).getY()));

                            } else {
                                snake.set(i, snake.get(i - 1));
                            }

                        }
                    }
                    break;
                case DOWN:
                    if (server.getBorderMap().getLocationMap().get(new Node(snake.get(0).getX(),
                            snake.get(0).getY() + 1)) != "0") {
                        // remove from node map
                        for (Node part : snake) {
                            server.getBorderMap().getLocationMap().remove(part);
                            server.getBorderMap().getLocationMap().put(part, "0");
                        }
                        //kill a snake
                        player -= 1;
                        snake.clear();
                    } else {
                        for (int i = snake.size() - 1; i >= 0; i--) {
                            // the tail
                            if (i == snake.size() - 1) {
                                server.getBorderMap().getLocationMap().remove(snake.get(i));
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX(),
                                        snake.get(i).getY()), "0");
                                try {
                                    snake.set(i, snake.get(i - 1));

                                } catch (Exception e) {

                                }
                            }
                            // the head
                            else if (i == 0) {
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX(),
                                        snake.get(i).getY() + 1), ("playerSnake3"));/////////////////not set id//////////
                                snake.set(i, new Node(snake.get(i).getX(),
                                        snake.get(i).getY() + 1));

                            } else {
                                snake.set(i, snake.get(i - 1));
                            }

                        }
                    }
                    break;
            }
        }
    }


    public void player2Move(ArrayList<Node> snake){
        if (snake.size() == 0){

        }
        else{
            switch (next_direction_p2) {
                case UP:
                    if (server.getBorderMap().getLocationMap().get(new Node(snake.get(0).getX(),
                            snake.get(0).getY() - 1)) != "0") {
                        // remove from node map
                        for (Node part : snake) {
                            server.getBorderMap().getLocationMap().remove(part);
                            server.getBorderMap().getLocationMap().put(part, "0");
                        }
                        //kill a snake
                        player -= 1;
                        snake.clear();
                    } else {
                        for (int i = snake.size() - 1; i >= 0; i--) {
                            // the tail
                            if (i == snake.size() - 1) {
                                server.getBorderMap().getLocationMap().remove(snake.get(i));
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX(),
                                        snake.get(i).getY()), "0");
                                try {
                                    snake.set(i, snake.get(i - 1));

                                } catch (Exception e) {

                                }
                            }
                            // the head
                            else if (i == 0) {
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX(),
                                        snake.get(i).getY() - 1), ("playerSnake2"));/////////////////not set id//////////
                                snake.set(i, new Node(snake.get(i).getX(),
                                        snake.get(i).getY() - 1));

                            } else {
                                snake.set(i, snake.get(i - 1));
                            }

                        }
                    }
                    break;
                case LEFT:
                    if (server.getBorderMap().getLocationMap().get(new Node(snake.get(0).getX() - 1,
                            snake.get(0).getY())) != "0") {
                        // remove from node map
                        for (Node part : snake) {
                            server.getBorderMap().getLocationMap().remove(part);
                            server.getBorderMap().getLocationMap().put(part, "0");
                        }
                        //kill a snake
                        player -= 1;
                        snake.clear();
                    } else {
                        for (int i = snake.size() - 1; i >= 0; i--) {
                            // the tail
                            if (i == snake.size() - 1) {
                                server.getBorderMap().getLocationMap().remove(snake.get(i));
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX(),
                                        snake.get(i).getY()), "0");
                                try {
                                    snake.set(i, snake.get(i - 1));

                                } catch (Exception e) {

                                }
                            }
                            // the head
                            else if (i == 0) {
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX() - 1,
                                        snake.get(i).getY()), ("playerSnake2"));/////////////////not set id//////////
                                snake.set(i, new Node(snake.get(i).getX() - 1,
                                        snake.get(i).getY()));

                            } else {
                                snake.set(i, snake.get(i - 1));
                            }

                        }
                    }
                    break;
                case RIGHT:
                    if (server.getBorderMap().getLocationMap().get(new Node(snake.get(0).getX() + 1,
                            snake.get(0).getY())) != "0") {
                        // remove from node map
                        for (Node part : snake) {
                            server.getBorderMap().getLocationMap().remove(part);
                            server.getBorderMap().getLocationMap().put(part, "0");
                        }
                        //kill a snake
                        player -= 1;
                        snake.clear();
                    } else {
                        for (int i = snake.size() - 1; i >= 0; i--) {
                            // the tail
                            if (i == snake.size() - 1) {
                                server.getBorderMap().getLocationMap().remove(snake.get(i));
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX(),
                                        snake.get(i).getY()), "0");
                                try {
                                    snake.set(i, snake.get(i - 1));

                                } catch (Exception e) {

                                }
                            }
                            // the head
                            else if (i == 0) {
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX() + 1,
                                        snake.get(i).getY()), ("playerSnake2"));/////////////////not set id//////////
                                snake.set(i, new Node(snake.get(i).getX() + 1,
                                        snake.get(i).getY()));

                            } else {
                                snake.set(i, snake.get(i - 1));
                            }

                        }
                    }
                    break;
                case DOWN:
                    if (server.getBorderMap().getLocationMap().get(new Node(snake.get(0).getX(),
                            snake.get(0).getY() + 1)) != "0") {
                        // remove from node map
                        for (Node part : snake) {
                            server.getBorderMap().getLocationMap().remove(part);
                            server.getBorderMap().getLocationMap().put(part, "0");
                        }
                        //kill a snake
                        player -= 1;
                        snake.clear();
                    } else {
                        for (int i = snake.size() - 1; i >= 0; i--) {
                            // the tail
                            if (i == snake.size() - 1) {
                                server.getBorderMap().getLocationMap().remove(snake.get(i));
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX(),
                                        snake.get(i).getY()), "0");
                                try {
                                    snake.set(i, snake.get(i - 1));

                                } catch (Exception e) {

                                }
                            }
                            // the head
                            else if (i == 0) {
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX(),
                                        snake.get(i).getY() + 1), ("playerSnake2"));/////////////////not set id//////////
                                snake.set(i, new Node(snake.get(i).getX(),
                                        snake.get(i).getY() + 1));

                            } else {
                                snake.set(i, snake.get(i - 1));
                            }

                        }
                    }
                    break;
            }
        }
    }


    public void playerMove(ArrayList<Node> snake){
        if (snake.size() == 0){

        }
        else {
            switch (next_direction_p1) {
                case UP:
                    if (server.getBorderMap().getLocationMap().get(new Node(snake.get(0).getX(),
                            snake.get(0).getY() - 1)) != "0") {
                        // remove from node map
                        for (Node part : snake) {
                            server.getBorderMap().getLocationMap().remove(part);
                            server.getBorderMap().getLocationMap().put(part, "0");
                        }
                        //kill a snake
                        player -= 1;
                        snake.clear();
                    } else {
                        for (int i = snake.size() - 1; i >= 0; i--) {
                            // the tail
                            if (i == snake.size() - 1) {
                                server.getBorderMap().getLocationMap().remove(snake.get(i));
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX(),
                                        snake.get(i).getY()), "0");
                                try {
                                    snake.set(i, snake.get(i - 1));

                                } catch (Exception e) {

                                }
                            }
                            // the head
                            else if (i == 0) {
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX(),
                                        snake.get(i).getY() - 1), ("playerSnake1"));/////////////////not set id//////////
                                snake.set(i, new Node(snake.get(i).getX(),
                                        snake.get(i).getY() - 1));

                            } else {
                                snake.set(i, snake.get(i - 1));
                            }

                        }
                    }
                    break;
                case LEFT:
                    if (server.getBorderMap().getLocationMap().get(new Node(snake.get(0).getX() - 1,
                            snake.get(0).getY())) != "0") {
                        // remove from node map
                        for (Node part : snake) {
                            server.getBorderMap().getLocationMap().remove(part);
                            server.getBorderMap().getLocationMap().put(part, "0");
                        }
                        //kill a snake
                        player -= 1;
                        snake.clear();
                    } else {
                        for (int i = snake.size() - 1; i >= 0; i--) {
                            // the tail
                            if (i == snake.size() - 1) {
                                server.getBorderMap().getLocationMap().remove(snake.get(i));
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX(),
                                        snake.get(i).getY()), "0");
                                try {
                                    snake.set(i, snake.get(i - 1));

                                } catch (Exception e) {

                                }
                            }
                            // the head
                            else if (i == 0) {
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX() - 1,
                                        snake.get(i).getY()), ("playerSnake1"));/////////////////not set id//////////
                                snake.set(i, new Node(snake.get(i).getX() - 1,
                                        snake.get(i).getY()));

                            } else {
                                snake.set(i, snake.get(i - 1));
                            }

                        }
                    }
                    break;
                case RIGHT:
                    if (server.getBorderMap().getLocationMap().get(new Node(snake.get(0).getX() + 1,
                            snake.get(0).getY())) != "0") {
                        // remove from node map
                        for (Node part : snake) {
                            server.getBorderMap().getLocationMap().remove(part);
                            server.getBorderMap().getLocationMap().put(part, "0");
                        }
                        //kill a snake
                        player -= 1;
                        snake.clear();
                    } else {
                        for (int i = snake.size() - 1; i >= 0; i--) {
                            // the tail
                            if (i == snake.size() - 1) {
                                server.getBorderMap().getLocationMap().remove(snake.get(i));
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX(),
                                        snake.get(i).getY()), "0");
                                try {
                                    snake.set(i, snake.get(i - 1));

                                } catch (Exception e) {

                                }
                            }
                            // the head
                            else if (i == 0) {
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX() + 1,
                                        snake.get(i).getY()), ("playerSnake1"));/////////////////not set id//////////
                                snake.set(i, new Node(snake.get(i).getX() + 1,
                                        snake.get(i).getY()));

                            } else {
                                snake.set(i, snake.get(i - 1));
                            }

                        }
                    }
                    break;
                case DOWN:
                    if (server.getBorderMap().getLocationMap().get(new Node(snake.get(0).getX(),
                            snake.get(0).getY() + 1)) != "0") {
                        // remove from node map
                        for (Node part : snake) {
                            server.getBorderMap().getLocationMap().remove(part);
                            server.getBorderMap().getLocationMap().put(part, "0");
                        }
                        //kill a snake
                        player -= 1;
                        snake.clear();
                    } else {
                        for (int i = snake.size() - 1; i >= 0; i--) {
                            // the tail
                            if (i == snake.size() - 1) {
                                server.getBorderMap().getLocationMap().remove(snake.get(i));
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX(),
                                        snake.get(i).getY()), "0");
                                try {
                                    snake.set(i, snake.get(i - 1));

                                } catch (Exception e) {

                                }
                            }
                            // the head
                            else if (i == 0) {
                                server.getBorderMap().getLocationMap().put(new Node(snake.get(i).getX(),
                                        snake.get(i).getY() + 1), ("playerSnake1"));/////////////////not set id//////////
                                snake.set(i, new Node(snake.get(i).getX(),
                                        snake.get(i).getY() + 1));

                            } else {
                                snake.set(i, snake.get(i - 1));
                            }

                        }
                    }
                    break;
            }

        }
    }

    // if the game is not over. keep call this
    public void mainLoop() {
        System.out.println("I am executing threads!.");
        //Executor
        for(Snake snakeIter: server.getSnakes().values()){
            pool.execute(server.getSnakes().get(snakeIter.getCredentials()));
        }

        while (!game_over) {
            cycleTime = System.currentTimeMillis();

            getTime();

            if (server.getSnakes().size() + player == 1){
                gameOver();
            }

            direction_p1 = next_direction_p1;

            renderGame();

            cycleTime = System.currentTimeMillis() - cycleTime;
            sleepTime = speed - cycleTime;
            if (sleepTime < 0)
                sleepTime = 0;
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null,
                        ex);
            }
        }
        System.out.println("GAME FINISH!!!!!!!");
        System.exit(0);
    }

    // each turn, keep update the game state and the ui
    private void renderGame() {
        int gridUnit = height / gameSize;
        canvas.paint(graph);

        do {
            do {
                graph = strategy.getDrawGraphics();
                // Draw Background
                graph.setColor(Color.WHITE);
                graph.fillRect(0, 0, width, height);

                //String gridCase = "0";

                //where all the drawing happens, substitute the grid with
                for (int i = 0; i < gameSize; i++) {
                    for (int j = 0; j < gameSize; j++) {
                        String gridCase = server.getBorderMap().getLocationMap().get(new Node(i,j));

                        if(gridCase.equals("0")){
                            if(i == 0 || j == gameSize || j == 0 || i == gameSize){
                                graph.drawRect(i * gridUnit, j * gridUnit, gridUnit, gridUnit);
                                graph.setColor(Color.BLACK);
                            }
                        }else if(gridCase.contains("playerSnake")){
                            graph.setColor(gridCase.equals("playerSnake1") ? Color.blue :
                                    gridCase.equals("playerSnake2") ? Color.GREEN :
                                        gridCase.equals("playerSnake3") ? Color.pink :
                                            gridCase.equals("playerSnake4") ? Color.BLACK : null);

                            graph.fillOval(i * gridUnit, j * gridUnit, gridUnit, gridUnit);

                        }else {
                            Color bodyColor = Color.red;
                            graph.setColor(bodyColor);
                            graph.fillOval(i * gridUnit, j * gridUnit, gridUnit, gridUnit);
                        }
                    }
                }

                movePlayerSnakes();

                graph.setFont(new Font(Font.SANS_SERIF, Font.BOLD, height / 40));

                graph.setColor(Color.BLACK);
                graph.drawString("TIME = " + getTime(), 15, 20); //Clock
                graph.dispose();
            } while (strategy.contentsRestored());
            // Draw image from buffer
            strategy.show();
            Toolkit.getDefaultToolkit().sync();
        } while (strategy.contentsLost());
    }

    //for render. move all snake
    private void movePlayerSnakes() {
        playerMove(snake1);
        player2Move(snake2);
        player3Move(snake3);
        player4Move(snake4);
    }

    //timer for the game
    private String getTime() {
        String temps = minute + ":" + second + " Players Left: " + (server.getSnakes().size() + player);

        millisecond++;
        if (millisecond ==14){
            second++;
            millisecond =0;}
        if (second ==60){
            second =0;
            minute++;
        }

        return temps;
    }


    private void gameOver() {
        game_over = true;
    }

    //Delay method
    private void delay() {
        int actualDelay;
        try {
            actualDelay = randomWithRange(0,1000);
            Thread.sleep(actualDelay);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private int randomWithRange(int min, int max)
    {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }

    // IMPLEMENTED FUNCTIONS
    // wasd,tfgh,ijkl and up,down,left,right to contorll all snake. and another keyboard function
    public void keyPressed(KeyEvent ke) {
        int code = ke.getKeyCode();
        Dimension dim;
        switch (code) {
            case KeyEvent.VK_UP:
                if (direction_p1 != DOWN) {
                    next_direction_p1 = UP;
                }

                break;
            case KeyEvent.VK_DOWN:
                if (direction_p1 != UP) {
                    next_direction_p1 = DOWN;
                }

                break;
            case KeyEvent.VK_LEFT:
                if (direction_p1 != RIGHT) {
                    next_direction_p1 = LEFT;
                }

                break;
            case KeyEvent.VK_RIGHT:
                if (direction_p1 != LEFT) {
                    next_direction_p1 = RIGHT;
                }

                break;
            case KeyEvent.VK_W:
                if (direction_p2 != DOWN) {
                    next_direction_p2 = UP;
                }
                break;
            case KeyEvent.VK_A:
                if (direction_p2 != RIGHT) {
                    next_direction_p2 = LEFT;
                }
                break;
            case KeyEvent.VK_S:
                if (direction_p2 != UP) {
                    next_direction_p2 = DOWN;
                }
                break;
            case KeyEvent.VK_D:
                if (direction_p2 != LEFT) {
                    next_direction_p2 = RIGHT;
                }
                break;
            case KeyEvent.VK_T:
                if (direction_p3 != DOWN) {
                    next_direction_p3 = UP;
                }
                break;
            case KeyEvent.VK_F:
                if (direction_p3 != RIGHT) {
                    next_direction_p3 = LEFT;
                }
                break;
            case KeyEvent.VK_G:
                if (direction_p3 != UP) {
                    next_direction_p3 = DOWN;
                }
                break;
            case KeyEvent.VK_H:
                if (direction_p3 != LEFT) {
                    next_direction_p3 = RIGHT;
                }
                break;
            case KeyEvent.VK_I:
                if (direction_p4 != DOWN) {
                    next_direction_p4 = UP;
                }
                break;
            case KeyEvent.VK_J:
                if (direction_p4 != RIGHT) {
                    next_direction_p4 = LEFT;
                }
                break;
            case KeyEvent.VK_K:
                if (direction_p4 != UP) {
                    next_direction_p4 = DOWN;
                }
                break;
            case KeyEvent.VK_L:
                if (direction_p4 != LEFT) {
                    next_direction_p4 = RIGHT;
                }
                break;
            case KeyEvent.VK_F11:
                dim = Toolkit.getDefaultToolkit().getScreenSize();
                if ((height != dim.height - 50) || (width != dim.height - 50)) {
                    height = dim.height - 50;
                    width = dim.height - 50;
                } else {
                    height = 600;
                    width = 600;
                }
                frame.setSize(width + 7, height + 27);
                canvas.setSize(width + 7, height + 27);
                canvas.validate();
                frame.validate();
                break;
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;

            case KeyEvent.VK_SPACE:
                if(!game_over)
                    speed = 80;
                    delay();
                    speed = 150;
                break;
            default:
                // Unsupported key
                break;
        }
    }
    public void windowClosing(WindowEvent we) {
        System.exit(0);
    }

    // UNNUSED IMPLEMENTED FUNCTIONS
    public void keyTyped(KeyEvent ke) {}
    public void keyReleased(KeyEvent ke) {}
    public void windowOpened(WindowEvent we) {}
    public void windowClosed(WindowEvent we) {}
    public void windowIconified(WindowEvent we) {}
    public void windowDeiconified(WindowEvent we) {}
    public void windowActivated(WindowEvent we) {}
    public void windowDeactivated(WindowEvent we) {}
}