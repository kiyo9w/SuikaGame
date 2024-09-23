import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class FreezeFruit extends Fruit {

    public FreezeFruit(double x, double y, int type) {
        super(x, y, type);
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        // Draw a freeze indicator
        g.setColor(Color.BLUE);
        g.drawString("F", (int) getX() - 5, (int) getY() + 5);
    }

    @Override
    public void update() {
        super.update();
        // FreezeFruit disappears after freezing nearby fruits
        // So no additional logic here
    }

    public void freezeNearbyFruits(ArrayList<Fruit> fruits) {
        // Find the 5 closest fruits and freeze them
        ArrayList<FruitDistance> distances = new ArrayList<>();
        for (Fruit fruit : fruits) {
            if (fruit != this && !fruit.isFrozen()) {
                double distance = Math.hypot(fruit.getX() - getX(), fruit.getY() - getY());
                distances.add(new FruitDistance(fruit, distance));
            }
        }
        // Sort the list by distance
        distances.sort((fd1, fd2) -> Double.compare(fd1.distance, fd2.distance));
        // Freeze up to 5 closest fruits
        int freezeCount = Math.min(5, distances.size());
        for (int i = 0; i < freezeCount; i++) {
            distances.get(i).fruit.freeze();
        }
    }

    @Override
    public boolean canMergeWith(Fruit other) {
        // FreezeFruit cannot merge
        return false;
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
