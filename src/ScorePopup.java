import java.awt.Color;
import java.awt.Font;

public class ScorePopup {
    private String text;
    private double x;
    private double y;
    private double dy;
    private int alpha;
    private Font font;

    public ScorePopup(double x, double y, int points) {
        this.text = "+" + points;
        this.x = x;
        this.y = y;
        this.dy = -2;
        this.alpha = 255;
        this.font = new Font("Arial", Font.BOLD, 20);
    }

    public void update() {
        y += dy;
        alpha -= 5;
        if (alpha < 0) {
            alpha = 0;
        }
    }

    public boolean isFinished() {
        return alpha == 0;
    }

    public String getText() {
        return text;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getAlpha() {
        return alpha;
    }

    public Font getFont() {
        return font;
    }

    public Color getColor() {
        return new Color(0, 255, 0, alpha);
    }
}
