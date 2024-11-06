import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameOverFrame extends JFrame {
    public GameOverFrame() {
        setTitle("Game Over");
        setSize(800, 1000);
        setLocationRelativeTo(null); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());

 
        JLabel gameOverLabel = new JLabel("Game Over", SwingConstants.CENTER);
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 48));
        gameOverLabel.setForeground(Color.RED);

    
        JButton retryButton = new JButton("Play Again");
        retryButton.setFont(new Font("Arial", Font.BOLD, 24));
        retryButton.setBackground(Color.BLUE);
        retryButton.setForeground(Color.WHITE);
        retryButton.setFocusPainted(false);
        retryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); 
                new GameFrame("PlayerName").setVisible(true);
            }
        });
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 20, 0); 

        add(gameOverLabel, gbc);

        gbc.gridy = 1;
        add(retryButton, gbc);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameOverFrame gameOverFrame = new GameOverFrame();
            gameOverFrame.setVisible(true);
        });
    }
}
    