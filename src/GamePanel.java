import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class GamePanel extends JPanel implements ActionListener, KeyListener, MouseListener {
    private Timer timer;
    private ArrayList<Fruit> fruits;
    private Random random = new Random();
    private int specialFruitTimer = 0; // Counts frames to drop special fruits
    private final int SPECIAL_FRUIT_INTERVAL = 3000 / 16; // Approximately every 12 seconds
    private int playerX; // Player's X coordinate (Y coordinate is fixed on the bar)
    private final int BAR_Y_POSITION = 100; // Position of the bar from the top
    private final int PLAYER_WIDTH = 50; // Player's width
    private final int PLAYER_HEIGHT = 20;
    private final int PLAYER_SPEED = 10; // Movement speed
    private boolean gameOver = false;
    private int accumulatedHeight = 0; // Chiều cao tích lũy của đống quả


    public GamePanel() {
        timer = new Timer(16, this);
        timer.start();
        fruits = new ArrayList<>();
        playerX = 100; // Initial player position on the bar
        addKeyListener(this);
        addMouseListener(this);
        setFocusable(true);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow(); // Request focus after component is displayable
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Clear the background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw the fruits
        for (Fruit fruit : fruits) {
            fruit.draw(g);
        }

        // Draw the bar
        g.setColor(Color.GRAY);
        g.fillRect(0, BAR_Y_POSITION, getWidth(), 10);

        // Draw the player on the bar
        g.setColor(Color.BLUE);
        g.fillRect(playerX, BAR_Y_POSITION - PLAYER_HEIGHT, PLAYER_WIDTH, PLAYER_HEIGHT); // Rectangle representing the player
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            playerX = Math.max(0, playerX - PLAYER_SPEED); // Move left, limit at coordinate 0
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            playerX = Math.min(getWidth() - PLAYER_WIDTH, playerX + PLAYER_SPEED); // Move right, limit at window width
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            int fruitType = getRandomFruitType();
            Fruit newFruit = new Fruit(playerX + PLAYER_WIDTH / 2, BAR_Y_POSITION - PLAYER_HEIGHT+40, fruitType);
            fruits.add(newFruit);
            dropObject(playerX);
        }
        if (gameOver) {
            return; // Không xử lý chuột nếu trò chơi đã kết thúc
        }
        repaint(); // Repaint the game panel
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

            // Remove bomb if timer reaches zero
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
                if (Math.abs(fruit.getVy()) < 1) {
                    fruit.setVy(0);
                    fruit.setLanded(true); // Mark as landed
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
            if (!gameOver) {
                // Cập nhật vị trí của từng quả
                for (int i = 0; i < fruits.size(); i++) {
                    Fruit fruitss = fruits.get(i);
        
                    // Giả sử tốc độ rơi của quả là 2 pixel mỗi frame
                    fruitss.setY(fruitss.getY() );  // Tăng tọa độ Y để quả rơi xuống
        
                    // Kiểm tra nếu quả vượt quá thanh bar
                    if (fruitss.getY() < BAR_Y_POSITION) {
                        // Xử lý logic khi quả chạm thanh bar
                        gameOver = true;
                        System.out.println("Fruit has hit the bar.");
                        
                    }
                }
                
        
                // Vẽ lại màn hình
                repaint();
            } else {
                timer.stop(); // Dừng trò chơi khi game over
            }
        }
       
        
        // Handle collisions without modifying the fruits list directly
        checkCollisions(fruitsToRemove, fruitsToAdd);

        // Update the fruits list after iteration
        fruits.removeAll(fruitsToRemove);
        fruits.addAll(fruitsToAdd);

        // Special fruit drop logic
        specialFruitTimer++;
        if (specialFruitTimer >= SPECIAL_FRUIT_INTERVAL) {
            dropSpecialFruit();
            specialFruitTimer = 0;
        }

        repaint();
    }

    private void dropSpecialFruit() {
        // Randomly select a special fruit to drop
        int specialType = random.nextInt(3); // 0: Bomb, 1: Rainbow, 2: Freeze
        int xPosition = random.nextInt(getWidth() - 50) + 25; // Avoid spawning at edges
        Fruit specialFruit;
        switch (specialType) {
            case 0:
                specialFruit = new BombFruit(xPosition, 140, -1);
                break;
            case 1:
                specialFruit = new RainbowFruit(xPosition, 140, -2);
                break;
            case 2:
                specialFruit = new FreezeFruit(xPosition, 140, -3);
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
                    ((BombFruit) fruit).resetTimer();
                }
            }
        }
    }

    private void dropObject(int xPosition) {
        // Logic to drop an object from xPosition
        System.out.println("Dropping object from X position: " + xPosition);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // You can implement mouse click logic here if needed
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
