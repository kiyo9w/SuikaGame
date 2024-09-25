import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import java.util.Set;

public class BombFruit extends Fruit {
    private int startDropCount;


    public BombFruit(double x, double y, int type, int startDropCount) {
        super(x, y, type);
        this.startDropCount = startDropCount;
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        // Draw bomb indicator (e.g., timer)
        g.setColor(Color.WHITE);
//      int dropsLeft = Math.max(0, 10 - (currentDropCount - startDropCount));
        int turnLeft = Math.max(0, 8);
        g.drawString(String.valueOf(turnLeft), (int) getX() - 5, (int) getY() + 5);
    }

    public boolean shouldExplode(int currentDropCount) {
        return (currentDropCount - startDropCount) >= 8;
    }

    public void resetDropCount(int currentDropCount) {
        this.startDropCount = currentDropCount;
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
