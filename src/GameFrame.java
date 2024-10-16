import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class GameFrame extends JFrame {
    public GameFrame(String playerName) {
        setTitle("Suika Game Clone with Physics");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        Game game = new Game(400, 600);
        GamePanel gamePanel = new GamePanel(game);
        gamePanel.setPreferredSize(new Dimension(400, 600)); // Set fixed size for the game panel

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerPanel.add(gamePanel);

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(250, getHeight()));

        // High score board
        JLabel highScoreLabel = new JLabel("High Scores");
        highScoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        highScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea highScoreArea = new JTextArea();
        highScoreArea.setEditable(false);
        highScoreArea.setFont(new Font("Arial", Font.PLAIN, 14));

        // Load high scores from LeaderBoard class
        LeaderBoard leaderboard = new LeaderBoard("scores.txt");
        List<Integer> topScores = leaderboard.getTopScores(10);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < topScores.size(); i++) {
            sb.append((i + 1) + ". " + topScores.get(i) + "\n");
        }
        highScoreArea.setText(sb.toString());

        // Add components to side panel
        sidePanel.add(highScoreLabel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(new JScrollPane(highScoreArea));

        // Settings and credits buttons
        JButton settingsButton = new JButton("Settings");
        settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton creditsButton = new JButton("Credits");
        creditsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        sidePanel.add(settingsButton);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(creditsButton);

        // Create top panel for current score and player name
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel playerNameLabel = new JLabel("Player: " + playerName);
        topPanel.add(playerNameLabel);
        topPanel.add(Box.createRigidArea(new Dimension(20, 0)));

        // Adding stuffs to mainPanel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(sidePanel, BorderLayout.EAST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        Timer timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!game.isGameOver()) {
                    game.update();
                    gamePanel.repaint();

                } else {
                    game.endGame();
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        timer.start();
    }
}
