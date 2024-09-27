import java.awt.Color;
import java.awt.Graphics;

public class RainbowFruit extends Fruit {

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
    public boolean canMergeWith(Fruit other) {
        // Can merge with any fruit except special fruits
        return !(other instanceof BombFruit || other instanceof FreezeFruit || other instanceof RainbowFruit);
    }
}
