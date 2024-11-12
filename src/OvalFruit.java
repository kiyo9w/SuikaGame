import java.awt.Graphics;

public class OvalFruit extends Fruit {
    public OvalFruit(double x, double y, int type, Game game) {
        super(x, y, type, game);
    }

    @Override
    public void draw(Graphics g) {
        int drawPosX = (int) (getX() - getSize());
        int drawPosY = (int) (getY() - getSize() / 2);
        g.setColor(getColor());
        g.fillOval(drawPosX, drawPosY, getSize() * 2, getSize()); // Vẽ hình oval với chiều rộng gấp đôi chiều cao
    }
}
