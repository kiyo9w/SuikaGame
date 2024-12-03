package main;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ScoreBoard {
    private int score;
    private Image watermelonImage;

    public ScoreBoard() {
        this.score = 0;
        // Load the watermelon image
        try {
            this.watermelonImage = ImageIO.read(getClass().getResource("/resources/suika.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void draw(Graphics2D g2d, int width) {
        // Reduce the watermelon image size
        int imageSize = 24;
        int x = 30;
        int y = 0;
        if (watermelonImage != null) {
            g2d.drawImage(watermelonImage, x, y, imageSize, imageSize, null);
        }

        // Set the font and color similar to the image provided
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.setColor(new Color(0, 255, 0)); // Yellow color similar to the provided image

        // Draw the score number
        String scoreText = String.valueOf(score);
        g2d.drawString(scoreText, x + imageSize + 10, y + imageSize / 2 + 10);
    }
}
