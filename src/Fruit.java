import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Fruit {
    private double x, y;
    private double vx, vy;
    private static final double GRAVITY = 0.5;
    private int type;
    private int size;
    protected BufferedImage image;
    private static final Color FREEZE_COLOR = new Color(220, 243, 255, 100);
    private boolean frozen = false;
    private boolean hasCollided = false;
    private int freezeStage = 0;
    // Blinking variables
    private int blinkingTimer;
    private int nextBlinkTime;
    private boolean isBlinking = false;
    private int blinkDuration = 0;
    private static final int BLINK_DURATION_FRAMES = 5;


    public Fruit(double x, double y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.vx = 0;
        this.vy = 0;
        this.size = getSizeFromType(type);
        this.blinkingTimer = 0;
        //random between 100 and 300 frames
        setNextBlinkTime();
        loadImage();
    }

    public void update() {
        if (frozen) {
            return; // Skip movement if frozen
        }
        // Apply gravity
        vy += GRAVITY; // Gravity acceleration
        x += vx;
        y += vy;

        blinkingTimer++;
        if (blinkingTimer >= nextBlinkTime) {
            // Start blinking
            isBlinking = true;
            blinkDuration = BLINK_DURATION_FRAMES;
            blinkingTimer = 0;
        }
        if (isBlinking) {
            blinkDuration--;
            if (blinkDuration <= 0) {
                isBlinking = false;
            }
        }
    }

    private void loadImage() {
        String imagePath = "";
        switch (type) {
            case 1:
                imagePath = "/resources/strawberry.png";
                break;
            case 2:
                imagePath = "/resources/cherry.png";
                break;
            case 3:
                imagePath = "/resources/orange.png";
                break;
            case 4:
                imagePath = "/resources/peach.png";
                break;
            case 5:
                imagePath = "/resources/apple.png";
                break;
            case 6:
                imagePath = "/resources/yellowmelon.png";
                break;
            case 7:
                imagePath = "/resources/grapes.png";
                break;
            case 8:
                imagePath = "/resources/pineapple.png";
                break;
            case 9:
                imagePath = "/resources/suika.png";
                break;
            case -1:
                imagePath = "/resources/bomb.png";
                break;
            case -2:
                imagePath = "/resources/rainbow.png";
                break;
            case -3:
                imagePath = "/resources/snowflake.png";
                break;
        }

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

    public boolean hasCollided() {
        return hasCollided;
    }

    public void setHasCollided(boolean collided) {
        this.hasCollided = collided;
    }

    private void setNextBlinkTime() {
        // 100 and 300 frames until the next blink
        nextBlinkTime = blinkingTimer + (int) (Math.random() * 200 + 100);
    }

}
