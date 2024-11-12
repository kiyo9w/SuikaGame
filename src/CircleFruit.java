import java.awt.Graphics;

public class CircleFruit extends Fruit {
    public CircleFruit(double x, double y, int type, Game game) {
        super(x, y, type, game);
    }

    @Override
    public void draw(Graphics g) {
        int drawPosX = (int) (getX() - getSize() / 2);
        int drawPosY = (int) (getY() - getSize() / 2);
        g.setColor(getColor());
        g.fillOval(drawPosX, drawPosY, getSize(), getSize()); // Vẽ hình tròn
    }
}
