import java.awt.Color;
import java.awt.Graphics;

public class Gate {
    private double x;
    private double y;
    private String type;
    private long createTime;
    private boolean active;
    private static final long DURATION = 5000; // Gate lasts for 5 seconds

    public Gate(double x, double y, String type, long createTime) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.createTime = createTime;
        this.active = true;
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
               fruitRight > x - 25 && fruitLeft < x + 25; // Horizontal alignment
    }
    
    public void deactivate() {
        active = false;
    }

    public void draw(Graphics g) {
        if (active) {
            switch (type) {
                case "Bomb":
                    g.setColor(Color.RED);
                    g.drawRect((int) x - 25, (int) y - 10, 50, 20);
                    g.drawString("B", (int) x - 10, (int) y + 5);
                    break;
                case "Freeze":
                    g.setColor(Color.BLUE);
                    g.drawRect((int) x - 25, (int) y - 10, 50, 20);
                    g.drawString("F", (int) x - 10, (int) y + 5);
                    break;
                case "Rainbow":
                    g.setColor(Color.GREEN);
                    g.drawRect((int) x - 25, (int) y - 10, 50, 20);
                    g.drawString("R", (int) x - 10, (int) y + 5);
                    break;
                case "Double":
                    g.setColor(Color.ORANGE);
                    g.drawRect((int) x - 25, (int) y - 10, 50, 20);
                    g.drawString("D", (int) x - 10, (int) y + 5);
                    break;
                case "Reduce":
                    g.setColor(Color.BLACK);
                    g.drawRect((int) x - 25, (int) y - 10, 50, 20);
                    g.drawString("R-", (int) x - 10, (int) y + 5);
                    break;
            }
        }
    }

    public double getX() { return x; }
    public double getY() { return y; }
}
