import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class BombFruit extends Fruit {
    private int timer = 300; // Approximately 5 seconds at 60 FPS

    public BombFruit(double x, double y, int type) {
        super(x, y, type);
    }

    @Override
    public void update() {
        super.update();
        if (getVy() == 0) { // Start countdown after bomb has landed
            timer--;
        }
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        // Draw bomb indicator (e.g., timer)
        g.setColor(Color.BLACK);
        int secondsLeft = (int) Math.ceil(timer / 60.0);
        g.drawString(String.valueOf(secondsLeft), (int) getX() - 5, (int) getY() + 5);
    }

    public boolean shouldExplode() {
        return timer <= 0;
    }

    public void resetTimer() {
        timer = 300;
    }

    public void explode(ArrayList<Fruit> fruits) {
        double explosionRadius = 100; // Radius of explosion effect
        for (int i = fruits.size() - 1; i >= 0; i--) {
            Fruit fruit = fruits.get(i);
            if (fruit != this) {
                double distance = Math.hypot(fruit.getX() - getX(), fruit.getY() - getY());
                if (distance <= explosionRadius) {
                    fruits.remove(i);
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
