package model;
import java.util.List;
import java.util.Set;
import main.WavPlayer;

public class Collision {
    private ScoreManager scoreManager;

    public Collision(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
    }

    public void handleCollisions(List<Fruit> fruits, Set<Fruit> fruitsToRemove, List<Fruit> fruitsToAdd,WavPlayer wavPlayer) {
        for (int i = 0; i < fruits.size(); i++) {
            Fruit f1 = fruits.get(i);
            if (fruitsToRemove.contains(f1)) continue;

            for (int j = i + 1; j < fruits.size(); j++) {
                Fruit f2 = fruits.get(j);
                if (fruitsToRemove.contains(f2)) continue;

                // Compute collision parameters
                double dx = f2.getX() - f1.getX();
                double dy = f2.getY() - f1.getY();
                double dist = Math.hypot(dx, dy);
                double minDist = (f1.getSize() + f2.getSize()) / 2.0;

                if (dist < minDist) {
                    // Resolve physical collision
                    resolveCollision(f1, f2, dx, dy, dist, minDist);

                    // Let the fruits handle their collision logic
                    Fruit newFruit = f1.onCollideWith(f2);
                    if (newFruit != null) {
                        fruitsToRemove.add(f1);
                        fruitsToRemove.add(f2);
                        fruitsToAdd.add(newFruit);

                        //Gain points based on the new fruit obtained
                        int level = newFruit.getType();
                        double popupX = newFruit.getX() ;
                        double popupY = newFruit.getY() ;
                        scoreManager.addPoints(level, popupX, popupY);
                        wavPlayer.playMergeSound();

                        break; // Exit inner loop to prevent concurrent modification
                    }
                }
            }
        }
    }

    public void handleWallCollisions(Fruit fruit, int width) {
        if (fruit.getX() - fruit.getSize() / 2 <= 0) {
            fruit.setX(fruit.getSize() / 2);
            fruit.setVx(-fruit.getVx() * 0.8);
        } else if (fruit.getX() + fruit.getSize() / 2 >= width) {
            fruit.setX(width - fruit.getSize() / 2);
            fruit.setVx(-fruit.getVx() * 0.8);
        }
    }

    public void handleGroundAndCeilingCollisions(Fruit fruit, int height) {
        if (fruit.getY() + fruit.getSize() / 2 >= height) {
            fruit.setY(height - fruit.getSize() / 2);
            fruit.setVy(-fruit.getVy() * 0.8);
            fruit.setVx(fruit.getVx() * 0.95);
            if (Math.abs(fruit.getVy()) < 10) {
                fruit.setVy(0);
            }
            if (Math.abs(fruit.getVx()) < 0.1) {
                fruit.setVx(0);
            }
        }

        if (fruit.getY() - fruit.getSize() / 2 <= 0) {
            fruit.setY(fruit.getSize() / 2);
            fruit.setVy(-fruit.getVy() * 0.8);
        }
    }

    private void resolveCollision(Fruit f1, Fruit f2, double dx, double dy, double dist, double minDist) {
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

        double m1Norm = (dpNorm1 * (f1.getSize() - f2.getSize()) + 2 * f2.getSize() * dpNorm2)
                / (f1.getSize() + f2.getSize());
        double m2Norm = (dpNorm2 * (f2.getSize() - f1.getSize()) + 2 * f1.getSize() * dpNorm1)
                / (f1.getSize() + f2.getSize());

        f1.setVx(tx * dpTan1 + nx * m1Norm);
        f1.setVy(ty * dpTan1 + ny * m1Norm);
        f2.setVx(tx * dpTan2 + nx * m2Norm);
        f2.setVy(ty * dpTan2 + ny * m2Norm);

        // Damping to simulate energy loss
        f1.setVx(f1.getVx() * 0.95);
        f1.setVy(f1.getVy() * 0.95);
        f2.setVx(f2.getVx() * 0.95);
        f2.setVy(f2.getVy() * 0.95);
    }
    
}