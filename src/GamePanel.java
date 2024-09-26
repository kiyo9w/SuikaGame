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
    private static final int BAR_Y_POSITION = 100; // Position of the bar from the top
    private static final int PLAYER_WIDTH = 50; // Player's width
    private static final int PLAYER_SPEED = 15; // Movement speed
    private boolean gameOver = false;
    private LinkedList<Fruit> fruitQueue;
    private int dropCount = 0;
    private Fruit lastDroppedFruit = null;
    private static final int DROP_INTERVAL = 8;



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
        for (int i = 0; i < DROP_INTERVAL; i++) {
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
        int newPlayerX = e.getX() - PLAYER_WIDTH / 2;
        newPlayerX = Math.max(0, Math.min(getWidth() - PLAYER_WIDTH, newPlayerX));

        if (newPlayerX != playerX) {
            playerX = newPlayerX;
            repaint(); // Only repaint if playerX has changed
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //Reduce noise, make fruits more circley
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Clear the background
        g2d.setColor(new Color(247, 242, 200));
        g2d.fillRect(0, 0, getWidth(), getHeight());

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
            fruit.draw(g2d, x, y, displaySize);
        }


        // Draw the fruits
        synchronized (fruits) {
            for (Fruit fruit : fruits) {
                fruit.draw(g);
            }
        }

        // Draw the bar
        g2d.setColor(Color.GRAY);
        g2d.fillRect(0, BAR_Y_POSITION, getWidth(), 7);

        // Draw the player (fruit)
        Fruit nextFruit = fruitQueue.getFirst();
        nextFruit.draw(g2d, playerX + PLAYER_WIDTH / 2, BAR_Y_POSITION + 20, nextFruit.getSize());

        // Game over screen
        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 150)); // Semi-transparent overlay
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            String gameOverText = "Game Over";
            int textWidth = g.getFontMetrics().stringWidth(gameOverText);
            g.drawString(gameOverText, (getWidth() - textWidth) / 2, getHeight() / 2);

            // Restart buttn here
        }
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
                synchronized (fruits) {
                    for (Fruit fruit : fruits) {
                        if (fruit.isFrozen()) {
                            fruit.decrementFreezeStage();
                        }
                        if (fruit instanceof BombFruit) {
                            ((BombFruit) fruit).onFruitDropped();
                        }
                    }
                }
                lastDroppedFruit = newFruit;

                // Check if it's time to drop a special fruit
                if (dropCount % 8 == 0) {
                    dropSpecialFruit();
                }
            }
        }
        repaint();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        // Update fruits
        Set<Fruit> fruitsToRemove = new HashSet<>();
        List<Fruit> fruitsToAdd = new ArrayList<>();

        if (!gameOver) {
            for (Fruit fruit : fruits) {
                if (fruit.getY() <= BAR_Y_POSITION) {
                    gameOver = true;
                    System.out.println("Fruit has hit the bar.");
                }
                if (fruitsToRemove.contains(fruit)) {
                    continue; // Skip if already marked for removal
                }
                fruit.update();

                // Explode the thingy
                if (fruit instanceof BombFruit) {
                    BombFruit bombFruit = (BombFruit) fruit;
                    if (bombFruit.shouldExplode()) {
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
                    if (Math.abs(fruit.getVy()) < 5) {
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
                specialFruit = new BombFruit(xPosition, BAR_Y_POSITION + 20, -1);
                break;
            case 1:
                specialFruit = new RainbowFruit(xPosition, BAR_Y_POSITION + 20, -2);
                break;
            case 2:
                specialFruit = new FreezeFruit(xPosition, BAR_Y_POSITION + 20, -3);
                break;
            default:
                specialFruit = new Fruit(xPosition, 140, 3);
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
                    ((BombFruit) fruit).resetTurnsLeft();
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

    // Unused mouse events (need to keep in for game panel declaration)
    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void mouseDragged(MouseEvent e) {}

    public void keyReleased(KeyEvent e) {}

    public void keyTyped(KeyEvent e) {}
}
