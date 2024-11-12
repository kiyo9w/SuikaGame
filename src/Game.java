import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

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
    private static final int DROP_INTERVAL = 8;
    private static final int PLAYER_WIDTH = 50;
    private static final int BAR_Y_POSITION = 100;
    private static final int DROP_DELAY = 2000;
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
            int initialLevel = random.nextInt(2) + 1; // Cấp độ 1 hoặc 2 cho trái cây ban đầu
            int x = (int) (random.nextDouble() * width);
            int y = 0;
            List<Fruit> newFruits = createRandomFruit(x, y, initialLevel);
            for (Fruit fruit : newFruits) {
                fruitQueue.add(fruit);
            }
        }
        
    }
    public int getScore() {
        return scoreManager.getScore();
    }
    public int calculateLevelFromScore(int score) {
        
        if(score>20){
            return 5;
        } else if(score>30){
            return 7;
        } else if(score>50){
            return 8;
        }

        return 0;
    }
    
    public void update() {
        if (gameOver) {
           
            return;
            
        }

        Set<Fruit> fruitsToRemove = new HashSet<>();
        List<Fruit> fruitsToAdd = new ArrayList<>();

        int maxHeight = height;
        for (Fruit fruit : fruits) {
            fruit.update();
            fruit.postUpdate(fruits, fruitsToRemove);

            // Collision with walls
            if (fruit.getX() - fruit.getSize() / 2 <= 0) {
                fruit.setX(fruit.getSize() / 2);
                fruit.setVx(-fruit.getVx() * 0.8); // Bounce back with damping
            } else if (fruit.getX() + fruit.getSize() / 2 >= width) {
                fruit.setX(width - fruit.getSize() / 2);
                fruit.setVx(-fruit.getVx() * 0.8);
            }

            // Collision with ground
            if (fruit.getY() + fruit.getSize() / 2 >= height) {
                fruit.setY(height - fruit.getSize() / 2); // Dont know what bug caused ground to be eaten but gonna fix later :D
                fruit.setVy(-fruit.getVy() * 0.8);
                fruit.setVx(fruit.getVx() * 0.95);
                if (Math.abs(fruit.getVy()) < 10) {
                    fruit.setVy(0);
                }
                if (Math.abs(fruit.getVx()) < 0.1) {
                    fruit.setVx(0);
                }
            }

            // Collision with ceiling
            if (fruit.getY() - fruit.getSize() / 2 <= 0) {
                fruit.setY(fruit.getSize() / 2);
                fruit.setVy(-fruit.getVy() * 0.8);
            }

            if (fruit.getY() <= BAR_Y_POSITION) {
                gameOver = true;
                System.out.println("Fruit has hit the bar.");
            }

            //Collision with gate
            for (Gate gate : gates) {
                if (gate.isFruitThroughGate(fruit) && !(fruit instanceof BombFruit || fruit instanceof FreezeFruit || fruit instanceof RainbowFruit)) {
                    double gateX = gate.getX();
                    double gateY = gate.getY();
            
                    switch (gate.getType()) {
                        case "Bomb":
                            fruitsToAdd.add(new BombFruit(gateX, gateY, -1,this));
                            break;
                        case "Freeze":
                            fruitsToAdd.add(new FreezeFruit(gateX, gateY, -3,this));
                            break;
                        case "Rainbow":
                            fruitsToAdd.add(new RainbowFruit(gateX, gateY, -2,this));
                            break;
                        case "Double":
                            fruitsToAdd.add(new Fruit(gateX - 20, gateY - 10, fruit.getType(),this));
                            fruitsToAdd.add(new Fruit(gateX + 20, gateY + 10, fruit.getType(),this));
                            break;
                        case "Reduce":
                            if (fruit.getType() > 1) {
                                fruitsToAdd.add(new Fruit(gateX, gateY, fruit.getType() - 1,this));
                            } else {
                                fruitsToAdd.add(new Fruit(gateX, gateY, fruit.getType(),this));
                            }
                            break;
                    }
                    fruitsToRemove.add(fruit);
                    gate.deactivate(); // Deactivate the gate immediately
                }
            }            
            
        }
            
        // Handle collisions using CollisionManager
        collisionManager.handleCollisions(fruits, fruitsToRemove, fruitsToAdd);

        fruits.removeAll(fruitsToRemove);
        fruits.addAll(fruitsToAdd);

        //Update score popups
        scoreManager.updatePopups();


        // Calculate the maximum height of the fruits
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
        if (lastDroppedFruit == null || (currentTime - lastDropTime >= DROP_DELAY)) {
            Fruit newFruit = fruitQueue.poll();
            if (newFruit != null) {
                newFruit.setX(playerX + PLAYER_WIDTH / 2);
                newFruit.setY(BAR_Y_POSITION + 20);
                fruits.add(newFruit);
                dropCount++;
                int score=this.getScore();
                int level=calculateLevelFromScore(score);
                List<Fruit> newFruits = createRandomFruit(0, 0, level);

            // Tạo trái cây mới với cấp độ (level) đã tính toán và thêm vào hàng đợi
            for (Fruit fruit : newFruits) {
                fruitQueue.add(fruit);
            }
                // Decrement freeze stages of all frozen fruits
                for (Fruit fruit : fruits) {
                    if (fruit.isFrozen()) {
                        fruit.decrementFreezeStage();
                    }
                    if (fruit instanceof BombFruit) {
                        ((BombFruit) fruit).onFruitDropped();
                    }
                }

                lastDroppedFruit = newFruit;
                lastDropTime = currentTime;

                // Check if it's time to drop a special fruit
                if (dropCount % DROP_INTERVAL == 0) {
                    dropSpecialFruit();
                }
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
                specialFruit = new BombFruit(xPosition, BAR_Y_POSITION + 20, -1,this);
                break;
            case 1:
                specialFruit = new RainbowFruit(xPosition, BAR_Y_POSITION + 20, -2,this);
                break;
            case 2:
                specialFruit = new FreezeFruit(xPosition, BAR_Y_POSITION + 20, -3,this);
                break;
            default:
                return;
        }
        fruits.add(specialFruit);
        specialFruitDropped = true;
    }

    
    
    public List<Fruit> createRandomFruit(int x, int y, int level) {
        List<Fruit> fruits = new ArrayList<>(); // Danh sách để chứa các đối tượng Fruit
    
        switch (level) {
            case 5:
                // Tạo một OvalFruit và thêm vào danh sách
                fruits.add(new OvalFruit(x, y, level, this));
    
                // Gọi logic của default để tạo một Fruit bình thường và thêm vào danh sách
                int fruitType = getRandomFruitType();
                fruits.add(new Fruit(x, y, fruitType, this));
                break;
    
            case 7:
                // Tạo một BananaFruit và thêm vào danh sách
                fruits.add(new BananaFruit(x, y, level, this));
                fruitType = getRandomFruitType();
                fruits.add(new Fruit(x, y, fruitType, this));
                break;
                case 8:
                fruits.add(new CircleFruit(x, y, level, this));
                fruitType = getRandomFruitType();
                fruits.add(new Fruit(x, y, fruitType, this));
                break;
    
            default:
                // Tạo một Fruit bình thường và thêm vào danh sách
                fruitType = getRandomFruitType();
                fruits.add(new Fruit(x, y, fruitType, this));
                break;
        }
    
        return fruits;
    }
    
    public void endGame() {
        int finalScore = scoreManager.getScore();
        // Need to implement: stop everything movement
        // Lưu điểm vào bảng xếp hạng
        leaderboard.addScore(finalScore);
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