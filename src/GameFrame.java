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

        // Load images
        Image gameBackground = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/game_background.png"));
        Image leaderboardBg = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/leaderboard.png"));
        // Removed userBg as we're not using it anymore

        // Create the game panel
        Game game = new Game(400, 600); // Game field size is 400x600
        GamePanel gamePanel = new GamePanel(game);
        gamePanel.setPreferredSize(new Dimension(400, 600)); // Set fixed size for the game panel

        // Wrap the game panel in a BackgroundPanel
        BackgroundPanel gameBackgroundPanel = new BackgroundPanel(gameBackground);
        gameBackgroundPanel.setPreferredSize(new Dimension(400, 600));
        gameBackgroundPanel.setLayout(null);
        gameBackgroundPanel.add(gamePanel);
        gamePanel.setBounds(0, 0, 400, 600);

        // Create gamePanelWrapper to center gameBackgroundPanel horizontally
        JPanel gamePanelWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        gamePanelWrapper.setOpaque(false);
        gamePanelWrapper.add(gameBackgroundPanel);

        // Create center panel with BoxLayout to position game panel at bottom
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(Box.createVerticalGlue()); // Pushes the game panel to the bottom
        centerPanel.add(gamePanelWrapper);

        // Create side panel (right) for leaderboard image
        BackgroundPanel sidePanel = new BackgroundPanel(leaderboardBg);
        sidePanel.setPreferredSize(new Dimension(300, 170)); // Fixed size matching the image

        // Create scoresPanel to hold the high score label and text
        JPanel scoresPanel = new JPanel();
        scoresPanel.setLayout(new BorderLayout());
        scoresPanel.setOpaque(false);

        // Create high score label
        JLabel highScoreLabel = new JLabel("High Scores");
        highScoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        highScoreLabel.setForeground(Color.BLACK);
        highScoreLabel.setHorizontalAlignment(JLabel.CENTER);
        scoresPanel.add(highScoreLabel, BorderLayout.NORTH);

        // Create the high score area
        JTextArea highScoreArea = new JTextArea();
        highScoreArea.setEditable(false);
        highScoreArea.setFont(new Font("Arial", Font.PLAIN, 14));
        highScoreArea.setOpaque(false);
        highScoreArea.setForeground(Color.BLACK);

        // Put the high score area in a scroll pane
        JScrollPane highScoreScrollPane = new JScrollPane(highScoreArea);
        highScoreScrollPane.setOpaque(false);
        highScoreScrollPane.getViewport().setOpaque(false);
        highScoreScrollPane.setBorder(null);
        scoresPanel.add(highScoreScrollPane, BorderLayout.CENTER);

        // Wrap sidePanel and scoresPanel in a fixed-size wrapper
        JPanel sidePanelWrapper = new JPanel();
        sidePanelWrapper.setPreferredSize(new Dimension(300, 500));
        sidePanelWrapper.setLayout(new BoxLayout(sidePanelWrapper, BoxLayout.Y_AXIS));
        sidePanelWrapper.setOpaque(false);

        // Add sidePanel (leaderboard image) to sidePanelWrapper
        sidePanelWrapper.add(sidePanel);

        // Add scoresPanel (high score text) to sidePanelWrapper
        sidePanelWrapper.add(scoresPanel);

        // Load high scores from LeaderBoard class
        LeaderBoard leaderboard = new LeaderBoard("scores.txt");
        List<Integer> topScores = leaderboard.getTopScores(10);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < topScores.size(); i++) {
            sb.append((i + 1) + ". " + topScores.get(i) + "\n");
        }
        highScoreArea.setText(sb.toString());

        // Top panel for current score and player name
        JPanel topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(getWidth(), 50));
        topPanel.setLayout(null);
        topPanel.setBackground(new Color(230, 230, 230));

        JLabel playerNameLabel = new JLabel("Player: " + playerName);
        playerNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        playerNameLabel.setForeground(Color.BLACK); // Adjusted to black for visibility
        playerNameLabel.setBounds(10, 10, 200, 30);
        topPanel.add(playerNameLabel);

        JLabel scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        scoreLabel.setForeground(Color.BLACK); // Adjusted to black for visibility
        scoreLabel.setBounds(220, 10, 200, 30);
        topPanel.add(scoreLabel);

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Add components to mainPanel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(sidePanelWrapper, BorderLayout.EAST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        Timer timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!game.isGameOver()) {
                    game.update();
                    gamePanel.repaint();

                    scoreLabel.setText("Score: " + game.getScoreManager().getScore());
                } else {
                    game.endGame();
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        timer.start();
    }
}
