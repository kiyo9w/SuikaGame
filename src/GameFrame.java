import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class GameFrame extends JFrame {
    private MainMenuPanel mainMenuPanel;
    private JPanel mainPanel;  // Holds the main game layout
    private JLabel scoreLabel;
    private Game game;
    private GamePanel gamePanel;
    private Timer timer;
    private String playerName;

    public GameFrame(String playerName) {
        this.playerName = playerName;
        setSize(1000, 800);
        setLocationRelativeTo(null); // Center on screen
        setVisible(true);

        // Initialize main menu panel and add it to frame
        mainMenuPanel = new MainMenuPanel();
        add(mainMenuPanel);

        // Listen for Play button click in MainMenuPanel
        mainMenuPanel.getPlayButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame(playerName);  // Start the game when "Play" is clicked
            }
        });
    }

    public void startGame(String playerName) {
        remove(mainMenuPanel);
        setTitle("Suika Game Clone with Physics");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);

        // Load images
        Image windowBackground = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/window_background.jpeg"));
        Image gameBackground = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/game_background.png"));
        Image leaderboardBg = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/leaderboard.png"));

        // Create the game panel
        game = new Game(400, 600); // Game field size is 400x600
        gamePanel = new GamePanel(game);
        gamePanel.setPreferredSize(new Dimension(400, 600)); // Set fixed size for the game panel
        gamePanel.setOpaque(false); // Make the game panel transparent if you want the game background to show

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
        centerPanel.setOpaque(false); // Make transparent to show the window background
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
        highScoreLabel.setFont(new Font("ComicSansMS", Font.BOLD, 16));
        highScoreLabel.setForeground(Color.BLACK);
        highScoreLabel.setHorizontalAlignment(JLabel.CENTER);
        scoresPanel.add(highScoreLabel, BorderLayout.NORTH);

        // Create the high score area
        JTextArea highScoreArea = new JTextArea();
        highScoreArea.setEditable(false);
        highScoreArea.setFont(new Font("ComicSansMS", Font.PLAIN, 14));
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
        topPanel.setOpaque(false); // Make transparent to show the window background

        JLabel playerNameLabel = new JLabel("Player: " + playerName);
        playerNameLabel.setFont(new Font("ComicSansMS", Font.BOLD, 16));
        playerNameLabel.setForeground(Color.BLACK);
        playerNameLabel.setBounds(10, 10, 200, 30);
        topPanel.add(playerNameLabel);

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("ComicSansMS", Font.BOLD, 16));
        scoreLabel.setForeground(Color.BLACK);
        scoreLabel.setBounds(220, 10, 200, 30);
        topPanel.add(scoreLabel);

        // Create main panel with the window background image
        BackgroundPanel mainPanel = new BackgroundPanel(windowBackground);
        mainPanel.setLayout(new BorderLayout());

        // Add components to mainPanel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(sidePanelWrapper, BorderLayout.EAST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
        repaint();

        // Initialize the timer
        timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!game.isGameOver()) {
                    game.update();
                    gamePanel.repaint();
                    scoreLabel.setText("Score: " + game.getScoreManager().getScore());
                    GameOverFrame gameOverFrame = new GameOverFrame();
                    gameOverFrame.setVisible(true);
                } else {
                    game.endGame();
                    // Show the GameOverFrame without disposing the GameFrame
                    GameOverFrame gameOverFrame = new GameOverFrame();
                    gameOverFrame.setVisible(true);
                    timer.stop(); // Stop the timer
                }
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame("PlayerName");
            frame.setVisible(true);
        });
    }
}
