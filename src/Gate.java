import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Gate {
    private double x;
    private double y;
    private String type;
    private long createTime;
    private boolean active;
    private static final long DURATION = 5000; // Gate lasts for 5 seconds
    private Image gateImage;

    public Gate(double x, double y, String type, long createTime) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.createTime = createTime;
        this.active = true;
        loadImage();
    }

    private void loadImage() {
        try {
            switch (type) {
                case "Bomb":
                    gateImage = ImageIO.read(getClass().getResource("/resources/bomb_gate.png"));
                    break;
                case "Freeze":
                    gateImage = ImageIO.read(getClass().getResource("/resources/freeze_gate.png"));
                    break;
                case "Rainbow":
                    gateImage = ImageIO.read(getClass().getResource("/resources/rainbow_gate.png"));
                    break;
                case "Double":
                    gateImage = ImageIO.read(getClass().getResource("/resources/double_gate.png"));
                    break;
                case "Reduce":
                    gateImage = ImageIO.read(getClass().getResource("/resources/reduce_gate.png"));
                    break;
                default:
                    gateImage = ImageIO.read(getClass().getResource("/resources/default_gate.png"));
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isActive(long currentTime) {
        return active && currentTime - createTime < DURATION;
    }

    public String getType() {
        return type;
    }

    public boolean isFruitThroughGate(Fruit fruit) {
        double fruitBottom = fruit.getY() + fruit.getSize() / 2;
        double fruitTop = fruit.getY() - fruit.getSize() / 2;
        double fruitLeft = fruit.getX() - fruit.getSize() / 2;
        double fruitRight = fruit.getX() + fruit.getSize() / 2;

        return active &&
               fruitTop < y && fruitBottom > y && // Vertical alignment
               fruitRight > x - 50 && fruitLeft < x + 50; // Horizontal alignment
    }

    public void deactivate() {
        active = false;
    }

    public void draw(Graphics g) {
        if (active) {
            if (gateImage != null) {
                g.drawImage(gateImage, (int) x - 50, (int) y - 50, 100, 100, null); // Adjusted size to 100x100 pixels
            } else {
                g.setColor(Color.MAGENTA);
                g.drawRect((int) x - 50, (int) y - 25, 100, 50);
                g.drawString(type.substring(0, 1), (int) x - 10, (int) y + 5);
            }
        }
    }

    public double getX() { return x; }
    public double getY() { return y; }
}
