import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener, MouseListener {
    private Timer timer;
    private ArrayList<Fruit> fruits;
    private Random random = new Random();
    private int specialFruitTimer = 0; // Counts frames to drop special fruits
    private final int SPECIAL_FRUIT_INTERVAL = 12000 / 16; // Approximately every 12 seconds

    public GamePanel() {
        timer = new Timer(16, this);
        timer.start();
        fruits = new ArrayList<>();
        addMouseListener(this);
        setFocusable(true);
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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Update fruits
        for (int i = fruits.size() - 1; i >= 0; i--) {
            Fruit fruit = fruits.get(i);
            fruit.update();

            // Remove bomb if timer reaches zero and no adjacent merges
            if (fruit instanceof BombFruit) {
                BombFruit bombFruit = (BombFruit) fruit;
                if (bombFruit.shouldExplode()) {
                    bombFruit.explode(fruits);
                    fruits.remove(i);
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

        checkCollisions();

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
                specialFruit = new BombFruit(xPosition, 0, 1);
                break;
            case 1:
                specialFruit = new RainbowFruit(xPosition, 0);
                break;
            case 2:
                specialFruit = new FreezeFruit(xPosition, 0, 1);
                break;
            default:
                specialFruit = new Fruit(xPosition, 0, 1);
                break;
        }
        fruits.add(specialFruit);
    }

    private void checkCollisions() {
        for (int i = 0; i < fruits.size(); i++) {
            Fruit f1 = fruits.get(i);
            for (int j = i + 1; j < fruits.size(); j++) {
                Fruit f2 = fruits.get(j);
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
                        fruits.remove(freezeFruit);
                        if (f1 == freezeFruit) {
                            i--;
                            break;
                        } else {
                            j--;
                            continue;
                        }
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
                        fruits.remove(f1);
                        fruits.remove(f2);
                        fruits.add(newFruit);
                        mergeOccurred = true;
                        i--;
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
                        fruits.remove(f1);
                        fruits.remove(f2);
                        fruits.add(newFruit);
                        mergeOccurred = true;
                        i--;
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

    @Override
    public void mouseClicked(MouseEvent e) {
        // Spawn a random fruit between types 1-4 with specified probabilities
        int fruitType = getRandomFruitType();
        Fruit newFruit = new Fruit(e.getX(), e.getY(), fruitType);
        fruits.add(newFruit);
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
}
