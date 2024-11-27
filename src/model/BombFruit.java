package model;
import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import java.util.Set;

public class BombFruit extends SpecialFruit {
    private int turnsLeft;
    private static final int EXPLOSION_TURNS = 8; // Total turns before explosion

    public BombFruit(double x, double y, int type) {
        super(x, y, type);
        this.turnsLeft = EXPLOSION_TURNS;
    }
/* 
    @Override
    public void update() {
        super.update();
    }
*/
    @Override
    public void draw(Graphics g) {
        super.draw(g);
        // Draw bomb indicator
        g.setColor(Color.WHITE);
        g.drawString(String.valueOf(turnsLeft), (int) getX() - 5, (int) getY() + 5);
    }

    public void onFruitDropped() {
        System.out.println("bomb count");
        turnsLeft--;
    }

    public boolean shouldExplode() {
        return turnsLeft <= 0;
    }

    public void resetTurnsLeft() {
        this.turnsLeft = EXPLOSION_TURNS;
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
    public void postUpdate(List<Fruit> allFruits, Set<Fruit> fruitsToRemove) {
        if (shouldExplode()) {
            explode(allFruits, fruitsToRemove);
            fruitsToRemove.add(this);
        }
    }

}
