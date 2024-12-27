package main;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import model.BombFruit;
import model.Collision;
import model.FreezeFruit;
import model.Fruit;
import model.Gate;
import model.LeaderBoard;
import model.RainbowFruit;
import model.ScoreManager;
import model.SpecialFruit;

public class Game {
    private List<Fruit> fruits;
    private Queue<Fruit> fruitQueue;
    private List<Gate> gates;
    private int playerX;
    private boolean gameOver;
    private int dropCount;
    private Fruit lastDroppedFruit;
    private long lastDropTime;
    private long lastGateTime;
    private int width;
    private int height;
    private Random random;
    private Collision collisionManager;
    private ScoreManager scoreManager;
    private WavPlayer wavPlayer;
    private static final int DROP_INTERVAL = 8;
    private static final int PLAYER_WIDTH = 50;
    private static final int BAR_Y_POSITION = 100;
    private static final int DROP_DELAY = 500;
    private static final int GATE_INTERVAL = 10000;
    private static final int DANGER_LINE = 280;
    private boolean specialFruitDropped = false;
    private LeaderBoard leaderboard;

    public Game(int width, int height) {
        this.width = width;
        this.height = height;
        fruits = new ArrayList<>();
        fruitQueue = new LinkedList<>();
        gates = new ArrayList<>();
        random = new Random();
        scoreManager = new ScoreManager();
        collisionManager = new Collision(scoreManager); // Instantiate CollisionManager
        initializeFruitQueue();
        playerX = width / 2 - PLAYER_WIDTH / 2;
        gameOver = false;
        dropCount = 0;
        lastDroppedFruit = null;
        lastDropTime = 0;
        lastGateTime = 0;
        leaderboard = new LeaderBoard("scores.txt");
    }

    private void initializeFruitQueue() {
        for (int i = 0; i < DROP_INTERVAL; i++) {
            Fruit fruit = createRandomFruit(0, 0);
            fruitQueue.add(fruit);
        }
    }

    public void update() {
        if (gameOver) return;

        Set<Fruit> fruitsToRemove = new HashSet<>();
        List<Fruit> fruitsToAdd = new ArrayList<>();

        int maxHeight = height;
        for (Fruit fruit : fruits) {
            fruit.update();
            fruit.postUpdate(fruits, fruitsToRemove);

            // Collision with walls
            collisionManager.handleWallCollisions(fruit, width);

            // Collision with ground & ceiling
            collisionManager.handleGroundAndCeilingCollisions(fruit, height);

            //Collision with gate
            handleGateCollision(fruitsToRemove, fruitsToAdd, fruit);            
            
        }
            
        // Handle collisions using CollisionManager
        collisionManager.handleCollisions(fruits, fruitsToRemove, fruitsToAdd,wavPlayer);

        fruits.removeAll(fruitsToRemove);
        fruits.addAll(fruitsToAdd);

        //Update score popups
        scoreManager.updatePopups();


        // Calculate the maximum height of the fruits
        handleDangerLineLogic(maxHeight);

        checkGameOverCondition();

    }

    private void handleGateCollision(Set<Fruit> fruitsToRemove, List<Fruit> fruitsToAdd, Fruit fruit) {
        for (Gate gate : gates) {
            if (gate.isFruitThroughGate(fruit) && !(fruit instanceof BombFruit || fruit instanceof FreezeFruit || fruit instanceof RainbowFruit)) {
                double gateX = gate.getX();
                double gateY = gate.getY();
        
                switch (gate.getType()) {
                    case "Bomb":
                        fruitsToAdd.add(new BombFruit(gateX, gateY, -1));
                        break;
                    case "Freeze":
                        fruitsToAdd.add(new FreezeFruit(gateX, gateY, -3));
                        break;
                    case "Rainbow":
                        fruitsToAdd.add(new RainbowFruit(gateX, gateY, -2));
                        break;
                    case "Double":
                        fruitsToAdd.add(new Fruit(gateX - 20, gateY - 10, fruit.getType()));
                        fruitsToAdd.add(new Fruit(gateX + 20, gateY + 10, fruit.getType()));
                        break;
                    case "Reduce":
                        if (fruit.getType() > 1) {
                            fruitsToAdd.add(new Fruit(gateX, gateY, fruit.getType() - 1));
                        } else {
                            fruitsToAdd.add(new Fruit(gateX, gateY, fruit.getType()));
                        }
                        break;
                }
                fruitsToRemove.add(fruit);
                gate.deactivate(); // Deactivate the gate immediately
            }
        }
    }
    
    private void handleDangerLineLogic(int maxHeight) {
        if (!specialFruitDropped) {
            maxHeight = height;
            for (Fruit fruit : fruits) {
                if (fruit.getVy() == 0) {
                    boolean onGround = (fruit.getY() + fruit.getSize() / 2 >= height);
                    boolean onTopOfAnother = false;

                    for (Fruit other : fruits) {
                        if (other != fruit && Math.abs(fruit.getY() - other.getY()) <= (fruit.getSize() + other.getSize()) / 2) {
                            onTopOfAnother = true;
                            break;
                        }
                    }   

                    if (onGround || onTopOfAnother) {
                        int fruitHeight = (int) (fruit.getY() - fruit.getSize() / 2);
                        if (fruitHeight < maxHeight) {
                            maxHeight = fruitHeight;
                        }
                    }
                }
            }
        }

        // Check if the highest fruit reaches the danger line
        if (maxHeight > DANGER_LINE) {
            // Handle gate appearance
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastGateTime >= GATE_INTERVAL) {
                addNewGate();
                lastGateTime = currentTime;
            }
        } else {
            System.out.println("Warning: Fruit stack has reached the danger line.");
        }

        // Remove expired gates
        gates.removeIf(gate -> !gate.isActive(System.currentTimeMillis()));
    }

    public void dropFruit() {
        long currentTime = System.currentTimeMillis();
        if (lastDroppedFruit != null && (currentTime - lastDropTime < DROP_DELAY)) return;
        
        Fruit newFruit = fruitQueue.poll();
        if (newFruit == null) return;

        newFruit.setX(playerX + PLAYER_WIDTH / 2);
        newFruit.setY(BAR_Y_POSITION + 20);
        fruits.add(newFruit);
        dropCount++;
        // Generate a new fruit and add it to the end of the queue
        fruitQueue.add(createRandomFruit(0, 0));

        // Decrement freeze stages of all frozen fruits
        updateFreezeStage();

        lastDroppedFruit = newFruit;
        lastDropTime = currentTime;

        // Check if it's time to drop a special fruit
        if (dropCount % DROP_INTERVAL == 0) {
            dropSpecialFruit();
        }
    }

    private void checkGameOverCondition() {
        for (Fruit fruit : fruits) {
            if (fruit.getY() <= BAR_Y_POSITION) {
                gameOver = true;
                return;  // If any fruit reaches above the bar, end the game
            }
        }
    }    

    private void updateFreezeStage() {
        for (Fruit fruit : fruits) {
            if (fruit.isFrozen()) {
                fruit.decrementFreezeStage();
            }
            if (fruit instanceof BombFruit) {
                ((BombFruit) fruit).onFruitDropped();
            }
        }
    }

    private void dropSpecialFruit() {
        // Randomly select a special fruit to drop
        int specialType = random.nextInt(3); // 0: Bomb, 1: Rainbow, 2: Freeze
        int xPosition = random.nextInt(width - 50) + 25; // Avoid spawning at edges
        SpecialFruit specialFruit;
        switch (specialType) {
            case 0:
                specialFruit = new BombFruit(xPosition, BAR_Y_POSITION + 20, -1);
                break;
            case 1:
                specialFruit = new RainbowFruit(xPosition, BAR_Y_POSITION + 20, -2);
                break;
            case 2:
                specialFruit = new FreezeFruit(xPosition, BAR_Y_POSITION + 20, -3);
                break;
            default:
                return;
        }
        fruits.add(specialFruit);
        specialFruitDropped = true;
    }

    private Fruit createRandomFruit(int x, int y) {
        int fruitType = getRandomFruitType();
        return new Fruit(x, y, fruitType);
    }
    public void endGame(String playerName) {
        int finalScore = scoreManager.getScore();
        // Need to implement: stop everything movement
        // Lưu điểm vào bảng xếp hạng
        leaderboard.addScore(playerName,finalScore);
    }

    private int getRandomFruitType() {
        int[] types = {1, 2, 3, 4};
        int[] weights = {55, 30, 10, 5}; // Sum to 100
        int totalWeight = 100;
        int randomNum = random.nextInt(totalWeight);
        int cumulativeWeight = 0;
        for (int i = 0; i < types.length; i++) {
            cumulativeWeight += weights[i];
            if (randomNum < cumulativeWeight) {
                return types[i];
            }
        }
        return types[types.length - 1]; // Fallback
    }

    private void addNewGate() {
        // Randomly select a gate type
        String[] gateTypes = {"Bomb", "Freeze", "Rainbow", "Double", "Reduce"};
        String gateType = gateTypes[random.nextInt(gateTypes.length)];
    
        // Randomly select a position between the ceiling and the danger line
        int gateX = random.nextInt(width - 100) + 50; // Avoid spawning at edges
        int gateY = random.nextInt(DANGER_LINE - BAR_Y_POSITION - 110) + BAR_Y_POSITION + 110;
    
        // Create and add the new gate
        Gate newGate = new Gate(gateX, gateY, gateType, System.currentTimeMillis());
        gates.add(newGate);
    }
    
    public List<Fruit> getFruits() {
        return fruits;
    }

    public Queue<Fruit> getFruitQueue() {
        return fruitQueue;
    }

    public int getPlayerX() {
        return playerX;
    }

    public void setPlayerX(int x) {
        this.playerX = x;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public static int getPlayerWidth() {
        return PLAYER_WIDTH;
    }

    public static int getBarYPosition() {
        return BAR_Y_POSITION;
    }

    public int getDangerLineY(){
        return DANGER_LINE;
    }

    public ScoreManager getScoreManager() {
        return scoreManager;
    }

    public List<Gate> getGates() {
        return gates;
    }
    
    public void reset() {
        fruits.clear();
        fruitQueue.clear();
        initializeFruitQueue();
        gameOver = false;
        dropCount = 0;
        lastDroppedFruit = null;
        lastDropTime = 0;  
    }
} 
