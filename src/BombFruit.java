import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import java.util.Set;

public class BombFruit extends Fruit {
    private int timer = 300; // Approximately 5 seconds at 60 FPS

    public BombFruit(double x, double y, int type) {
        super(x, y, type);
    }

    @Override
    public void update() {
        super.update();
        if (true) {
            timer--;
        }
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        // Draw bomb indicator (e.g., timer)
        g.setColor(Color.WHITE);
        int secondsLeft = (int) Math.ceil(timer / 60.0);
        g.drawString(String.valueOf(secondsLeft), (int) getX() - 5, (int) getY() + 5);
    }

    public boolean shouldExplode() {
        return timer <= 0;
    }

    public void resetTimer() {
        timer = 300;
    }

    public void explode(List<Fruit> fruits, Set<Fruit> fruitsToRemove) {
        double explosionRadius = 100; // Radius of explosion effect
        for (Fruit fruit : fruits) {
            if (fruit != this && !fruitsToRemove.contains(fruit)) {
                double distance = Math.hypot(fruit.getX() - getX(), fruit.getY() - getY());
                if (distance <= explosionRadius) {
                    fruitsToRemove.add(fruit);
                }
            }
        }
    }

    @Override
    public boolean canMergeWith(Fruit other) {
        // Bomb cannot merge
        return false;
    }
}
