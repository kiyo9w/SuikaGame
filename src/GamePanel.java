import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.util.Queue;
import java.util.List;

public class GamePanel extends JPanel {
    private Game game;
    private InputHandler inputHandler;
    private ScoreBoard scoreBoard;

    public GamePanel(Game game) {
        this.game = game;
        inputHandler = new InputHandler(game, this);
        addKeyListener(inputHandler);
        addMouseListener(inputHandler);
        addMouseMotionListener(inputHandler);
        setFocusable(true);

        scoreBoard = new ScoreBoard();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow(); // Request focus after component is displayable
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Reduce noise, make fruits more circley
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int width = getWidth();
        int height = getHeight();

        // Clear the background
        g2d.setColor(new Color(247, 242, 200));
        g2d.fillRect(0, 0, width, height);

        // Draw the fruit queue in a circle
        Queue<Fruit> fruitQueue = game.getFruitQueue();
        int queueSize = fruitQueue.size();
        int boxSize = 50; // Fixed box size for each fruit
        int startX = width - boxSize / 2 - 10;
        int startY = boxSize / 2 + 10;

        int i = 0;
        for (Fruit fruit : fruitQueue) {
            int x = startX - i * (boxSize + 5);
            int y = startY;
            int displaySize = fruit.getQueueSize();
            fruit.draw(g2d, x, y, displaySize);
            i++;
        }

        // Draw the fruits
        for (Fruit fruit : game.getFruits()) {
            fruit.draw(g2d);
        }

        // Draw the bar
        g2d.setColor(Color.GRAY);
        g2d.fillRect(0, Game.getBarYPosition(), width, 7);

        // Draw the player (next fruit)
        Fruit nextFruit = fruitQueue.peek();
        if (nextFruit != null) {
            int playerX = game.getPlayerX();
            nextFruit.draw(g2d, playerX + Game.getPlayerWidth() / 2, Game.getBarYPosition() + 20, nextFruit.getSize());
        }

        
        // Update and draw the scoreboard
        scoreBoard.setScore(game.getScoreManager().getScore());
        scoreBoard.draw(g2d, width);


        // Draw the score popups
        List<ScorePopup> scorePopups = game.getScoreManager().getScorePopups();
        for (ScorePopup popup : scorePopups) {
            g2d.setFont(popup.getFont());
            g2d.setColor(popup.getColor());
            g2d.drawString(popup.getText(), (float) popup.getX(), (float) popup.getY());
        }
        
        // Game over screen
        if (game.isGameOver()) {
            g2d.setColor(new Color(0, 0, 0, 150)); // Semi-transparent overlay
            g2d.fillRect(0, 0, width, height);
            g2d.setColor(Color.YELLOW);
            g2d.setFont(new Font("Arial", Font.BOLD, 48));
            String gameOverText = "Game Over";
            int textWidth = g2d.getFontMetrics().stringWidth(gameOverText);
            g2d.drawString(gameOverText, (width - textWidth) / 2, height / 2);
            if (game.isGameOver()) { 
                g2d.setColor(new Color(0, 0, 0, 150)); // Semi-transparent overlay
                g2d.fillRect(0, 0, width, height);
                g2d.setColor(Color.YELLOW);
                g2d.setFont(new Font("Arial", Font.BOLD, 48));
                String gameOverText = "Game Over";
                int textWidth = g2d.getFontMetrics().stringWidth(gameOverText);
                g2d.drawString(gameOverText, (width - textWidth) / 2, height / 2);
                g2d.setFont(new Font("Arial", Font.PLAIN, 36));
                String restartText = "Restart";
                int restartTextWidth = g2d.getFontMetrics().stringWidth(restartText);
    
                int buttonX = (width - restartTextWidth) / 2;
                int buttonY = height / 2 + 50; // Position the button below the game over text
                int buttonWidth = restartTextWidth + 20;
                int buttonHeight = 40;
                g2d.setColor(Color.WHITE);
                g2d.fillRect(buttonX - 10, buttonY - 30, buttonWidth, buttonHeight);
                g2d.setColor(Color.BLACK);
                g2d.drawString(restartText, buttonX, buttonY);
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        int mouseX = e.getX();
                        int mouseY = e.getY();
                        if(mouseX >= buttonX - 10 && mouseX <= buttonX - 10 + buttonWidth &&
                        mouseY >= buttonY - 30 && mouseY <= buttonY - 30 + buttonHeight) {
                         reset();       
                    }
                
            }
        });
    }
}
