import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameFrame extends JFrame {
    public GameFrame() {
        setTitle("Suika Game Clone with Physics");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 600);
        setLocationRelativeTo(null);

        Game game = new Game(getWidth(), getHeight());
        GamePanel gamePanel = new GamePanel(game);
        add(gamePanel);

        // Start the game loop using a Swing Timer
        Timer timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!game.isGameOver()) {
                    game.update();
                    gamePanel.repaint();
                } else {
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        timer.start();
    }
}
