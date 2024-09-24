import java.awt.Color;
import java.awt.Graphics;

public class RainbowFruit extends Fruit {

    public RainbowFruit(double x, double y, int type) {
        super(x, y, type); // Base type
    }

    @Override
    public void draw(Graphics g) {
        // Draw a rainbow-colored fruit
        g.setColor(Color.PINK);
        g.fillOval((int) (getX() - getSize() / 2), (int) (getY() - getSize() / 2), getSize(), getSize());
        g.setColor(Color.WHITE);
        g.drawString("R", (int) getX() - 5, (int) getY() + 5);
    }

    @Override
    public boolean canMergeWith(Fruit other) {
        // Can merge with any fruit
        return true;
    }
}
