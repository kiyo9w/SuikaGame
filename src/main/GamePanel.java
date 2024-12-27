package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import java.util.Queue;
import javax.swing.JPanel;
import model.Fruit;
import model.Gate;
import model.ScorePopup;


public class GamePanel extends JPanel {
    private static final Font GAME_OVER_FONT = new Font("Comic Sans MS", Font.BOLD, 48);
    private static final Color DANGER_LINE_COLOR = Color.RED;
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
        setOpaque(false);

        scoreBoard = new ScoreBoard();
WavPlayer.playBackgroundMusic("C:\\Users\\ASUS TUF\\SuikaCloneCS3360\\src\\resources\\sound\\background.wav");
        
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
//        g2d.setColor(new Color(247, 242, 200));
//        g2d.fillRect(0, 0, width, height);

        // Draw the fruit queue in a circle
        Queue<Fruit> fruitQueue = game.getFruitQueue();
        drawFruitQueue(g2d, width, fruitQueue);

        // Draw the fruits
        drawFruit(g2d);

        // Draw the bar
        g2d.setColor(Color.GRAY);
        g2d.fillRect(0, Game.getBarYPosition(), width, 7);

        // Draw the player (next fruit)
        drawPlayer(g2d, fruitQueue);

        // Draw the gates
        drawGate(g2d);

        // Draw the danger line
        drawDangerLine(g2d, width);

        // Update and draw the scoreboard
        scoreBoard.setScore(game.getScoreManager().getScore());
        scoreBoard.draw(g2d, width);


        // Draw the score popups
        List<ScorePopup> scorePopups = game.getScoreManager().getScorePopups();
        drawScorePopUp(g2d, scorePopups);

        // Game over screen
        if (game.isGameOver()) {
            drawGameOverSCreen(g2d, width, height);
            WavPlayer.stopBackgroundMusic();
        }
    }

    private void drawScorePopUp(Graphics2D g2d, List<ScorePopup> scorePopups) {
        for (ScorePopup popup : scorePopups) {
            g2d.setFont(popup.getFont());
            g2d.setColor(popup.getColor());
            g2d.drawString(popup.getText(), (float) popup.getX(), (float) popup.getY());
        }
    }

    private void drawGameOverSCreen(Graphics2D g2d, int width, int height) {
        g2d.setColor(new Color(0, 0, 0, 150)); // Semi-transparent overlay
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.YELLOW);
        g2d.setFont(GAME_OVER_FONT);
        String gameOverText = "Game Over";
        int textWidth = g2d.getFontMetrics().stringWidth(gameOverText);
        g2d.drawString(gameOverText, (width - textWidth) / 2, height / 2);
        g2d.setFont(new Font("Comic Sans MS", Font.PLAIN, 36));
    }

    private void drawDangerLine(Graphics2D g2d, int width) {
        int dangerLineY = game.getDangerLineY();
        g2d.setColor(DANGER_LINE_COLOR);
        g2d.drawLine(0, dangerLineY, width, dangerLineY);
        g2d.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        g2d.drawString("Danger Line", 10, dangerLineY - 5); // Label above the line
    }

    private void drawPlayer(Graphics2D g2d, Queue<Fruit> fruitQueue) {
        Fruit nextFruit = fruitQueue.peek();
        if (nextFruit != null) {
            int playerX = game.getPlayerX();
            nextFruit.draw(g2d, playerX + Game.getPlayerWidth() / 2, Game.getBarYPosition() + 20, nextFruit.getSize());
        }
    }

    private void drawGate(Graphics2D g2d) {
        for (Gate gate : game.getGates()) {
            gate.draw(g2d);
        }
    }

    private void drawFruit(Graphics2D g2d) {
        for (Fruit fruit : game.getFruits()) {
            fruit.draw(g2d);
        }
    }

    private void drawFruitQueue(Graphics2D g2d, int width, Queue<Fruit> fruitQueue) {
        int queueSize = fruitQueue.size();
        int boxSize = 50; // Fixed box size for each fruit
        int startX = width - boxSize / 2 - 10;
        int startY = boxSize / 2 + 20;

        int i = 0;
        for (Fruit fruit : fruitQueue) {
            int x = startX - i * (boxSize + 5);
            int y = startY;
            int displaySize = fruit.getQueueSize();
            fruit.draw(g2d, x, y, displaySize);
            i++;
        }
    }
    }

