import java.awt.Color;
import java.awt.Graphics;

public class Fruit {
    private double x, y;
    private double vx, vy;
    private int type;
    private int size;
    private boolean frozen = false;
    private boolean hasCollided = false;
    private int freezeStage = 0;

    public Fruit(double x, double y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.vx = 0;
        this.vy = 0;
        this.size = getSizeFromType(type);
    }

    public void update() {
        if (frozen) {
            return; // Skip movement if frozen
        }
        // Apply gravity
        vy += 0.5; // Gravity acceleration
        x += vx;
        y += vy;
    }

    public void draw(Graphics g) {
        if (frozen) {
            g.setColor(new Color(220, 243, 255));
        } else {
            g.setColor(getColor());
        }
        g.fillOval((int) (x - size / 2), (int) (y - size / 2), size, size);
    }

    private Color getColor() {
        switch (type) {
            case 1:
                return Color.RED; // Level 1 fruit
            case 2:
                return Color.ORANGE; // Level 2 fruit
            case 3:
                return Color.YELLOW; // Level 3 fruit
            case 4:
                return Color.GREEN; // Level 4 fruit
            case 5:
                return Color.BLUE; // Level 5 fruit
            case 6:
                return Color.MAGENTA; // Level 6 fruit
            case 7:
                return Color.BLACK; // Bomb fruit
            case 8:
                return Color.PINK; // Rainbow fruit
            case 9:
                return Color.CYAN; // Freeze fruit
            default:
                return Color.GRAY;
        }
    }

    private int getSizeFromType(int type) {
        // Define sizes for different fruit levels, 35 for special
        if (type < 0) {
            return 35;
        }
        return 30 + (type - 1) * 22;
    }
    public int getQueueSize() {
        return 20 + (type - 1) * 10;
    }


    public void drawAt(Graphics g, double drawX, double drawY, int displaySize) {
        // Set color based on fruit type or frozen state
        if (isFrozen()) {
            g.setColor(new Color(220, 243, 255));
        } else {
            g.setColor(getColor());
        }
        // Draw the fruit with the specified display size
        g.fillOval((int) (drawX - displaySize / 2), (int) (drawY - displaySize / 2), displaySize, displaySize);

        // Draw special indicators if needed
        if (isFrozen()) {
            g.setColor(Color.BLUE);
            g.drawString(String.valueOf(freezeStage), (int) drawX - 5, (int) drawY + 5);
        }
    }





    // Getters and setters
    public double getX() { return x; }
    public double getY() { return y; }
    public int getType() { return type; }
    public int getSize() { return size; }
    public double getVx() { return vx; }
    public double getVy() { return vy; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setVx(double vx) { this.vx = vx; }
    public void setVy(double vy) { this.vy = vy; }
    public void setType(int type) {
        this.type = type;
        this.size = getSizeFromType(type);
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void freeze() {
        frozen = true;
        freezeStage = 5;
    }

    public void unfreeze() {
        frozen = false;
        freezeStage = 0;
    }

    public void decrementFreezeStage() {
        freezeStage--;
        if (freezeStage <= 0) {
            unfreeze();
        }
    }

    public boolean canMergeWith(Fruit other) {
        if (this.isFrozen() || other.isFrozen()) {
            return false;
        }
        return this.getType() == other.getType();
    }

    public boolean hasCollided() {
        return hasCollided;
    }

    public void setHasCollided(boolean collided) {
        this.hasCollided = collided;
    }
}
