import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

public class BananaFruit extends Fruit {
    public BananaFruit(double x, double y, int type, Game game) {
        super(x, y, type, game);
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int width = getSize() * 2;
        int height = getSize() / 2;

        // Lưu trạng thái gốc
        AffineTransform originalTransform = g2d.getTransform();
        g2d.translate(getX(), getY());
        g2d.rotate(Math.toRadians(-30)); // Xoay nhẹ để giống hình dạng chuối

        // Vẽ hình Ellipse để đại diện cho chuối
        Ellipse2D bananaShape = new Ellipse2D.Double(-width / 2, -height / 2, width, height);
        g2d.setColor(getColor());
        g2d.fill(bananaShape);

        // Khôi phục trạng thái gốc
        g2d.setTransform(originalTransform);
    }
}
