package model;
import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import java.util.Set;

public class RainbowFruit extends SpecialFruit {

    public RainbowFruit(double x, double y, int type) {
        super(x, y, type); // Base type
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        g.setColor(Color.WHITE);
        g.drawString("R", (int) getX() - 5, (int) getY() + 5);
    }

    @Override
    public Fruit onCollideWith(Fruit other) {
        if (!(other instanceof BombFruit || other instanceof FreezeFruit || other instanceof RainbowFruit)) {
            int newType = other.getType() + 1;
            Fruit newFruit = new Fruit(
                    (this.getX() + other.getX()) / 2,
                    (this.getY() + other.getY()) / 2,
                    newType
            );
            newFruit.setVx((this.getVx() + other.getVx()) / 2);
            newFruit.setVy((this.getVy() + other.getVy()) / 2);
            return newFruit;
        }
        // Cannot merge; return null
        return null;
    }

    @Override
    public boolean canMergeWith(Fruit other) {
        // Can merge with any fruit except special fruits
        return !(other instanceof SpecialFruit);
    }

    @Override
    public void postUpdate(List<Fruit> allFruits, Set<Fruit> fruitsToRemove) {
        // RainbowFruit has no post-update action
    }
}
