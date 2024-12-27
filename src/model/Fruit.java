package model;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;

public class Fruit {
    private double x, y;
    private double vx, vy;
    private static final double GRAVITY = 0.3;
    private static final double FRICTION = 0.98;
    private int type;
    private int size;
    protected BufferedImage image;
    private static final Color FREEZE_COLOR = new Color(220, 243, 255, 100);
    private boolean frozen = false;
    private int freezeStage = 0;

    public Fruit(double x, double y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.vx = 0;
        this.vy = 0;
        this.size = getSizeFromType(type);
        loadImage();
    }

    public void update() {
        if (frozen) {
            return; // Skip movement if frozen
        }
        // Apply gravity
        vy += GRAVITY;
        // Apply friction to horizontal movement
        vx *= FRICTION;
        // Update position
        x += vx;
        y += vy;
    }

    public void postUpdate(List<Fruit> allFruits, Set<Fruit> fruitsToRemove) {
        // Default implementation does nothing, reserve for special fruits
    }

    public Fruit onCollideWith(Fruit other) {
        // Default behavior: attempt to merge if possible
        if (this.canMergeWith(other)) {
            int newType = this.type + 1;
            Fruit newFruit = new Fruit(
                    (this.x + other.x) / 2,
                    (this.y + other.y) / 2,
                    newType
            );
            newFruit.setVx((this.vx + other.vx) / 2);
            newFruit.setVy((this.vy + other.vy) / 2);
            return newFruit;
        }
        // No special action; return null
        return null;
    }

    private void loadImage() {
        String imagePath = switch (type) {
            case 1 -> "/resources/strawberry.png";
            case 2 -> "/resources/cherry.png";
            case 3 -> "/resources/orange.png";
            case 4 -> "/resources/peach.png";
            case 5 -> "/resources/apple.png";
            case 6 -> "/resources/yellowmelon.png";
            case 7 -> "/resources/grapes.png";
            case 8 -> "/resources/pineapple.png";
            case 9 -> "/resources/suika.png";
            case -1 -> "/resources/bomb.png";
            case -2 -> "/resources/rainbow.png";
            case -3 -> "/resources/snowflake.png";
            default -> "";
        };

        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception or set a default image
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            // Handle the case where the resource is not found
        }
    }

    // Back-up for when image assets doesnt load, considering removinng later
    private Color getColor() {
        switch (type) {
            case 1:
                return new Color(243, 34, 35); // Level 1 fruit
            case 2:
                return new Color(172, 108, 255); // Level 2 fruit
            case 3:
                return new Color(247, 186, 0); // Level 3 fruit
            case 4:
                return new Color(250, 8, 14); // Level 4 fruit
            case 5:
                return new Color(253, 239, 157); // Level 5 fruit
            case 6:
                return new Color(255, 181, 172); // Level 6 fruit
            case 7:
                return new Color(248, 238, 17); // Bomb fruit
            case 8:
                return new Color(159, 221, 15); // Rainbow fruit
            case 9:
                return new Color(66, 179, 7); // Freeze fruit
            default:
                return Color.GRAY;
        }
    }

    private int getSizeFromType(int type) {
        // Define sizes for different fruit levels, 35 for special
        if (type < 0) {
            return 35;
        }
        return 30 * (type - 1) + 30;
    }
    public int getQueueSize() {
        return 20 + (type - 1) * 10;
    }

    public void draw(Graphics g) {
        draw(g, this.x, this.y, this.size);  // Calls the method with parameters
    }

    public void draw(Graphics g, double drawX, double drawY, int displaySize) {
        //Reduce noise, make fruits more circley
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        if (isFrozen()) {
//            g.setColor(FREEZE_COLOR);
//        } else {
//            g.setColor(getColor());
//        }
//        int drawPosX = (int) (drawX - displaySize / 2);
//        int drawPosY = (int) (drawY - displaySize / 2);
//        g.fillOval(drawPosX, drawPosY, displaySize, displaySize);
        int drawPosX = (int) (drawX - displaySize / 2);
        int drawPosY = (int) (drawY - displaySize / 2);
        if (image != null) {
            g.drawImage(image, drawPosX, drawPosY, displaySize, displaySize, null);
        } else {
            // Back-up for when image assets doesnt load, considering removinng later
            g.setColor(getColor());
            g.fillOval(drawPosX, drawPosY, displaySize, displaySize);
        }
        // If the fruit is frozen, draw an overlay
        if (frozen) {
            g.setColor(FREEZE_COLOR); // Semi-transparent overlay
            g.fillOval(drawPosX, drawPosY, displaySize, displaySize);
        }
        // Draw the face
//        g.setColor(Color.BLACK);
//        int eyeWidth, eyeHeight;
//        if (isBlinking) {
//            eyeWidth = displaySize / 10;
//            eyeHeight = displaySize / 20;
//        } else {
//            eyeWidth = displaySize / 10;
//            eyeHeight = displaySize / 10;
//        }
//        int eyeXOffset = displaySize / 5;
//        int eyeY = drawPosY + displaySize / 3;
//        int leftEyeX = drawPosX + displaySize / 2 - eyeXOffset - eyeWidth / 2;
//        int rightEyeX = drawPosX + displaySize / 2 + eyeXOffset - eyeWidth / 2;
//        g.fillOval(leftEyeX, eyeY, eyeWidth, eyeHeight);
//        g.fillOval(rightEyeX, eyeY, eyeWidth, eyeHeight);
//
//        int mouthWidth = displaySize / 4;
//        int mouthHeight = displaySize / 8;
//        int mouthX = drawPosX + displaySize / 2 - mouthWidth / 2;
//        int mouthY = drawPosY + displaySize / 2 + displaySize / 6;
//        g.drawArc(mouthX, mouthY, mouthWidth, mouthHeight, 0, -180);
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
}
