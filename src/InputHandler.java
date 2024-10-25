import java.awt.event.*;

public class InputHandler implements KeyListener, MouseListener, MouseMotionListener {
    private Game game;
    private GamePanel gamePanel;
    private static final int PLAYER_SPEED = 15;

    public InputHandler(Game game, GamePanel gamePanel) {
        this.game = game;
        this.gamePanel = gamePanel;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int newPlayerX = e.getX() - Game.getPlayerWidth() / 2;
        newPlayerX = Math.max(0, Math.min(gamePanel.getWidth() - Game.getPlayerWidth(), newPlayerX));
        if (newPlayerX != game.getPlayerX()) {
            game.setPlayerX(newPlayerX);
            gamePanel.repaint();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        game.dropFruit();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int playerX = game.getPlayerX();
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            playerX = Math.max(0, playerX - PLAYER_SPEED);
            game.setPlayerX(playerX);
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            playerX = Math.min(gamePanel.getWidth() - Game.getPlayerWidth(), playerX + PLAYER_SPEED);
            game.setPlayerX(playerX);
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            game.dropFruit();
        }
        gamePanel.repaint();
    }

    // Unused methods
    @Override
    public void mousePressed(MouseEvent e) {
        if (game.isGameOver()) {
            int mouseX = e.getX();
            int mouseY = e.getY();
            if (mouseX >= restartButtonX && mouseX <= restartButtonX + restartButtonWidth &&
            mouseY >= restartButtonY && mouseY <= restartButtonY + restartButtonHeight) {
            game.reset();  // Restart the game
        }
        if (mouseX >= returnHomeButtonX && mouseX <= returnHomeButtonX + returnHomeButtonWidth &&
        mouseY >= returnHomeButtonY && mouseY <= returnHomeButtonY + returnHomeButtonHeight) {
        game.returnHome();
        }
    }
    }

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
    
}
