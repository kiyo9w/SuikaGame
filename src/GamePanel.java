import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.LinkedList;

public class GamePanel extends JPanel implements ActionListener, KeyListener, MouseListener, MouseMotionListener {
    private Timer timer;
    private ArrayList<Fruit> fruits;
    private Random random = new Random();
//    private int specialFruitTimer = 0; // Counts frames to drop special fruits
//    private final int SPECIAL_FRUIT_INTERVAL = 3000 / 16; // Approximately every 12 seconds
    private int playerX; // Player's X coordinate (Y coordinate is fixed on the bar)
    private final int BAR_Y_POSITION = 100; // Position of the bar from the top
    private final int PLAYER_WIDTH = 50; // Player's width
    private final int PLAYER_SPEED = 15; // Movement speed
    private boolean gameOver = false;
    private LinkedList<Fruit> fruitQueue;
    private int dropCount = 0;
    private Fruit lastDroppedFruit = null;



    public GamePanel() {
        timer = new Timer(16, this);
        timer.start();
        fruits = new ArrayList<>();
        playerX = 100; // Initial player position on the bar
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        setFocusable(true);
        fruitQueue = new LinkedList<>();
        for (int i = 0; i < 8; i++) {
            int fruitType = getRandomFruitType();
            Fruit fruit = new Fruit(0, 0, fruitType);
            fruitQueue.add(fruit);
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow(); // Request focus after component is displayable
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        playerX = e.getX() - PLAYER_WIDTH / 2;
        // Ensure playerX is within bounds
        playerX = Math.max(0, Math.min(getWidth() - PLAYER_WIDTH, playerX));
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Optional: Handle dragging if needed
        mouseMoved(e);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Clear the background
        g.setColor(new Color(247, 242, 200));
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw the fruit queue in a circle
        int queueSize = fruitQueue.size();
        int boxSize = 50; // Fixed box size for each fruit
        int startX = getWidth() - boxSize / 2 - 10;
        int startY = boxSize / 2 + 10;

        for (int i = 0; i < queueSize; i++) {
            Fruit fruit = fruitQueue.get(i);
            int x = startX - i * (boxSize + 5);
            int y = startY;
            int displaySize = fruit.getQueueSize();
            fruit.draw(g, x, y, displaySize);
        }


        // Draw the fruits
        for (Fruit fruit : fruits) {
            fruit.draw(g);
        }

        // Draw the bar
        g.setColor(Color.GRAY);
        g.fillRect(0, BAR_Y_POSITION, getWidth(), 7);

        // Draw the player (fruit)
        Fruit nextFruit = fruitQueue.getFirst();
        nextFruit.draw(g, playerX + PLAYER_WIDTH / 2, BAR_Y_POSITION + 20, nextFruit.getSize());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            playerX = Math.max(0, playerX - PLAYER_SPEED);
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            playerX = Math.min(getWidth() - PLAYER_WIDTH, playerX + PLAYER_SPEED);
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (lastDroppedFruit == null || lastDroppedFruit.hasCollided()) {
                Fruit newFruit = fruitQueue.removeFirst();
                newFruit.setX(playerX + PLAYER_WIDTH / 2);
                newFruit.setY(BAR_Y_POSITION + 20);
                fruits.add(newFruit);
                dropObject(playerX);
                dropCount++;

                // Generate a new fruit and add it to the end of the queue
                int fruitType = getRandomFruitType();
                Fruit nextFruit = new Fruit(0, 0, fruitType);
                fruitQueue.addLast(nextFruit);

                // Decrement freeze stages of all frozen fruits (5)
                for (Fruit fruit : fruits) {
                    if (fruit.isFrozen()) {
                        fruit.decrementFreezeStage();
                    }
                }
                lastDroppedFruit = newFruit;

                // Check if it's time to drop a special fruit
                if (dropCount % 8 == 0) {
                    dropSpecialFruit();
                }
            }
        }
        if (gameOver) {
            return;
        }
        repaint();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        // Update fruits
        Set<Fruit> fruitsToRemove = new HashSet<>();
        List<Fruit> fruitsToAdd = new ArrayList<>();

        for (Fruit fruit : fruits) {
            if (fruitsToRemove.contains(fruit)) {
                continue; // Skip if already marked for removal
            }

            fruit.update();

            // Explode the thingy
            if (fruit instanceof BombFruit) {
                BombFruit bombFruit = (BombFruit) fruit;
                if (bombFruit.shouldExplode(dropCount)) {
                    bombFruit.explode(fruits, fruitsToRemove);
                    fruitsToRemove.add(bombFruit);
                    continue;
                }
            }

            // Collision with walls
            if (fruit.getX() - fruit.getSize() / 2 <= 0) {
                fruit.setX(fruit.getSize() / 2);
                fruit.setVx(-fruit.getVx() * 0.8); // Bounce back with damping
            } else if (fruit.getX() + fruit.getSize() / 2 >= getWidth()) {
                fruit.setX(getWidth() - fruit.getSize() / 2);
                fruit.setVx(-fruit.getVx() * 0.8);
            }

            // Collision with ground
            if (fruit.getY() + fruit.getSize() / 2 >= getHeight()) {
                fruit.setY(getHeight() - fruit.getSize() / 2);
                fruit.setVy(-fruit.getVy() * 0.8); // Bounce back with damping
                fruit.setVx(fruit.getVx() * 0.95); // Friction
                if (Math.abs(fruit.getVy()) < 1) {
                    fruit.setVy(0);
                    fruit.setHasCollided(true); // Mark as landed
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
            
        }
        if (!gameOver) {
            // Cập nhật vị trí của từng quả
            for (int i = 0; i < fruits.size(); i++) {
                Fruit fruit = fruits.get(i);

                // Giả sử tốc độ rơi của quả là 2 pixel mỗi frame
                fruit.setY(fruit.getY() + 2);  // Tăng tọa độ Y để quả rơi xuống

                // Kiểm tra nếu quả vượt quá thanh bar
                if (fruit.getY() <= BAR_Y_POSITION) {
                    // Xử lý logic khi quả chạm thanh bar
                    gameOver = true;
                    System.out.println("Fruit has hit the bar.");
                }
            }
            repaint();

            // Vẽ lại màn hình
            repaint();
        } else {
            timer.stop(); // Dừng trò chơi khi game over
        }
        
        // Handle collisions without modifying the fruits list directly
        checkCollisions(fruitsToRemove, fruitsToAdd);

        // Update the fruits list after iteration
        fruits.removeAll(fruitsToRemove);
        fruits.addAll(fruitsToAdd);

        // Special fruit drop logic
//        specialFruitTimer++;
//        if (specialFruitTimer >= SPECIAL_FRUIT_INTERVAL) {
//            dropSpecialFruit();
//            specialFruitTimer = 0;
//        }

        repaint();
    }

    private void dropSpecialFruit() {
        // Randomly select a special fruit to drop
        int specialType = random.nextInt(3); // 0: Bomb, 1: Rainbow, 2: Freeze
        int xPosition = random.nextInt(getWidth() - 50) + 25; // Avoid spawning at edges
        Fruit specialFruit;
        switch (specialType) {
            case 0:
                specialFruit = new BombFruit(xPosition, BAR_Y_POSITION + 20, -1, dropCount);
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

    private void checkCollisions(Set<Fruit> fruitsToRemove, List<Fruit> fruitsToAdd) {
        for (int i = 0; i < fruits.size(); i++) {
            Fruit f1 = fruits.get(i);
            if (fruitsToRemove.contains(f1)) {
                continue;
            }
            for (int j = i + 1; j < fruits.size(); j++) {
                Fruit f2 = fruits.get(j);
                if (fruitsToRemove.contains(f2)) {
                    continue;
                }
                double dx = f2.getX() - f1.getX();
                double dy = f2.getY() - f1.getY();
                double dist = Math.hypot(dx, dy);
                double minDist = (f1.getSize() + f2.getSize()) / 2.0;
                if (dist < minDist) {
                    double overlap = minDist - dist + 1;
                    double totalMass = f1.getSize() + f2.getSize();
                    double m1 = f2.getSize() / totalMass;
                    double m2 = f1.getSize() / totalMass;

                    f1.setX(f1.getX() - dx / dist * overlap * m1);
                    f1.setY(f1.getY() - dy / dist * overlap * m1);
                    f2.setX(f2.getX() + dx / dist * overlap * m2);
                    f2.setY(f2.getY() + dy / dist * overlap * m2);

                    // Simple elastic collision
                    double nx = dx / dist;
                    double ny = dy / dist;

                    double tx = -ny;
                    double ty = nx;

                    double dpTan1 = f1.getVx() * tx + f1.getVy() * ty;
                    double dpTan2 = f2.getVx() * tx + f2.getVy() * ty;

                    double dpNorm1 = f1.getVx() * nx + f1.getVy() * ny;
                    double dpNorm2 = f2.getVx() * nx + f2.getVy() * ny;

                    double m1_norm = (dpNorm1 * (f1.getSize() - f2.getSize()) + 2 * f2.getSize() * dpNorm2)
                            / (f1.getSize() + f2.getSize());
                    double m2_norm = (dpNorm2 * (f2.getSize() - f1.getSize()) + 2 * f1.getSize() * dpNorm1)
                            / (f1.getSize() + f2.getSize());

                    f1.setVx(tx * dpTan1 + nx * m1_norm);
                    f1.setVy(ty * dpTan1 + ny * m1_norm);
                    f2.setVx(tx * dpTan2 + nx * m2_norm);
                    f2.setVy(ty * dpTan2 + ny * m2_norm);

                    // Damping to simulate energy loss
                    f1.setVx(f1.getVx() * 0.95);
                    f1.setVy(f1.getVy() * 0.95);
                    f2.setVx(f2.getVx() * 0.95);
                    f2.setVy(f2.getVy() * 0.95);

                    // **Add these lines to mark fruits as having collided**
                    f1.setHasCollided(true);
                    f2.setHasCollided(true);

                    boolean mergeOccurred = false;
                    Fruit newFruit = null;

                    // Handle special fruits and merging logic
                    if (f1 instanceof FreezeFruit || f2 instanceof FreezeFruit) {
                        FreezeFruit freezeFruit = (f1 instanceof FreezeFruit) ? (FreezeFruit) f1 : (FreezeFruit) f2;
                        freezeFruit.freezeNearbyFruits(fruits);
                        fruitsToRemove.add(freezeFruit);
                        continue;
                    } else if (f1 instanceof RainbowFruit || f2 instanceof RainbowFruit) {
                        Fruit regularFruit = (f1 instanceof RainbowFruit) ? f2 : f1;
                        int newType = regularFruit.getType() + 1;
                        newFruit = new Fruit(
                                (f1.getX() + f2.getX()) / 2,
                                (f1.getY() + f2.getY()) / 2,
                                newType
                        );
                        newFruit.setVx((f1.getVx() + f2.getVx()) / 2);
                        newFruit.setVy((f1.getVy() + f2.getVy()) / 2);
                        fruitsToRemove.add(f1);
                        fruitsToRemove.add(f2);
                        fruitsToAdd.add(newFruit);
                        mergeOccurred = true;
                        break;
                    } else if (f1.canMergeWith(f2)) {
                        // Regular merging
                        int newType = f1.getType() + 1;
                        newFruit = new Fruit(
                                (f1.getX() + f2.getX()) / 2,
                                (f1.getY() + f2.getY()) / 2,
                                newType
                        );
                        newFruit.setVx((f1.getVx() + f2.getVx()) / 2);
                        newFruit.setVy((f1.getVy() + f2.getVy()) / 2);
                        fruitsToRemove.add(f1);
                        fruitsToRemove.add(f2);
                        fruitsToAdd.add(newFruit);
                        mergeOccurred = true;
                        break;
                    }

                    // After merging, unfreeze adjacent frozen fruits and reset adjacent bomb timers
                    if (mergeOccurred && newFruit != null) {
                        unfreezeAdjacentFruits(newFruit);
                        resetAdjacentBombTimers(newFruit);
                    }
                }
            }
        }
    }


    private void unfreezeAdjacentFruits(Fruit mergedFruit) {
        for (Fruit fruit : fruits) {
            if (fruit.isFrozen()) {
                double dx = fruit.getX() - mergedFruit.getX();
                double dy = fruit.getY() - mergedFruit.getY();
                double dist = Math.hypot(dx, dy);
                double minDist = (fruit.getSize() + mergedFruit.getSize()) / 2.0;
                if (dist < minDist) {
                    fruit.unfreeze();
                }
            }
        }
    }

    private void resetAdjacentBombTimers(Fruit mergedFruit) {
        for (Fruit fruit : fruits) {
            if (fruit instanceof BombFruit) {
                double dx = fruit.getX() - mergedFruit.getX();
                double dy = fruit.getY() - mergedFruit.getY();
                double dist = Math.hypot(dx, dy);
                double minDist = (fruit.getSize() + mergedFruit.getSize()) / 2.0;
                if (dist < minDist) {
                    ((BombFruit) fruit).resetDropCount(dropCount);
                }
            }
        }
    }

    private void dropObject(int xPosition) {
        System.out.println("Dropping object from X position: " + xPosition);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (lastDroppedFruit == null || lastDroppedFruit.hasCollided()) {
            Fruit newFruit = fruitQueue.removeFirst();
            newFruit.setX(playerX + PLAYER_WIDTH / 2);
            newFruit.setY(BAR_Y_POSITION + 20);
            fruits.add(newFruit);
            dropObject(playerX);
            dropCount++;

            // Generate a new fruit and add it to the end of the queue
            int fruitType = getRandomFruitType();
            Fruit nextFruit = new Fruit(0, 0, fruitType);
            fruitQueue.addLast(nextFruit);

            // Decrement freeze stages of all frozen fruits (5)
            for (Fruit fruit : fruits) {
                if (fruit.isFrozen()) {
                    fruit.decrementFreezeStage();
                }
            }
            lastDroppedFruit = newFruit;

            // Check if it's time to drop a special fruit
            if (dropCount % 8 == 0) {
                dropSpecialFruit();
            }
        }
    }

    private int getRandomFruitType() {
        int randomValue = random.nextInt(100) + 1; // Random number between 1 and 100
        if (randomValue <= 55) {
            return 1; // 55% chance
        } else if (randomValue <= 85) {
            return 2; // Next 30% (85 - 55)
        } else if (randomValue <= 95) {
            return 3; // Next 10% (95 - 85)
        } else {
            return 4; // Remaining 5% (100 - 95)
        }
    }

    // Unused mouse events
    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        // Not needed for this implementation
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not needed for this implementation
    }
}
