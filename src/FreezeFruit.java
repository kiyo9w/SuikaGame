import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class FreezeFruit extends SpecialFruit {
    private int countdown;
    private static final int COUNTDOWN_DURATION = 100;

    public FreezeFruit(double x, double y, int type) {
        super(x, y, type);
        this.countdown = COUNTDOWN_DURATION;
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        // Draw a freeze indicator
        g.setColor(Color.WHITE);
        g.drawString("F", (int) getX() - 5, (int) getY() + 5);
    }

    @Override
    public void update() {
        super.update();
        // FreezeFruit disappears after freezing nearby fruits
        // So no additional logic here
    }

    public void freezeNearbyFruits(List<Fruit> fruits) {
        // Find the 5 closest fruits and freeze them
        List<FruitDistance> distances = new ArrayList<>();
        for (Fruit fruit : fruits) {
            if (fruit != this && !fruit.isFrozen()) {
                double distance = Math.hypot(fruit.getX() - getX(), fruit.getY() - getY());
                distances.add(new FruitDistance(fruit, distance));
            }
        }
        // Sort the list by distance
        distances.sort(Comparator.comparingDouble(fd -> fd.distance));
        // Freeze up to 5 closest fruits
        int freezeCount = Math.min(5, distances.size());
        for (int i = 0; i < freezeCount; i++) {
            distances.get(i).fruit.freeze();
        }
    }

    @Override
    public void postUpdate(List<Fruit> allFruits, Set<Fruit> fruitsToRemove) {
        if (countdown > 0) {
            countdown--;
        } else {
            freezeNearbyFruits(allFruits);
            fruitsToRemove.add(this); // Remove after freezing
        }
    }

    // Helper class to store fruits and their distances
    private class FruitDistance {
        Fruit fruit;
        double distance;

        FruitDistance(Fruit fruit, double distance) {
            this.fruit = fruit;
            this.distance = distance;
        }
    }
}
