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
    private int playerX;
    private boolean gameOver;
    private int dropCount;
    private Fruit lastDroppedFruit;
    private long lastDropTime;
    private int width;
    private int height;
    private Random random;
    private Collision collisionManager;
    private ScoreManager scoreManager;
    private static final int DROP_INTERVAL = 8;
    private static final int PLAYER_WIDTH = 50;
    private static final int BAR_Y_POSITION = 100;
    private static final int DROP_DELAY = 2000;
    private LeaderBoard leaderboard;

    public Game(int width, int height) {
        this.width = width;
        this.height = height;
        fruits = new ArrayList<>();
        fruitQueue = new LinkedList<>();
        random = new Random();
        scoreManager = new ScoreManager();
        collisionManager = new Collision(scoreManager); // Instantiate CollisionManager
        initializeFruitQueue();
        playerX = width / 2 - PLAYER_WIDTH / 2;
        gameOver = false;
        dropCount = 0;
        lastDroppedFruit = null;
        lastDropTime = 0;
        leaderboard = new LeaderBoard("scores.txt");
    }

    private void initializeFruitQueue() {
        for (int i = 0; i < DROP_INTERVAL; i++) {
            Fruit fruit = createRandomFruit(0, 0);
            fruitQueue.add(fruit);
        }
    }

    public void update() {
        if (gameOver) {
           
            return;
            
        }

        Set<Fruit> fruitsToRemove = new HashSet<>();
        List<Fruit> fruitsToAdd = new ArrayList<>();

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
        }

        // Handle collisions using CollisionManager
        collisionManager.handleCollisions(fruits, fruitsToRemove, fruitsToAdd);

        fruits.removeAll(fruitsToRemove);
        fruits.addAll(fruitsToAdd);
        scoreManager.updatePopups();
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
                // Generate a new fruit and add it to the end of the queue
                fruitQueue.add(createRandomFruit(0, 0));

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
        Fruit specialFruit;
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
                specialFruit = new Fruit(xPosition, 100, 3);
                break;
        }
        fruits.add(specialFruit);
    }

    private Fruit createRandomFruit(int x, int y) {
        int fruitType = getRandomFruitType();
        return new Fruit(x, y, fruitType);
    }
    public void endGame() {
        int finalScore = scoreManager.getScore();
            
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
