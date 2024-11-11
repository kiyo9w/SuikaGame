import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameOverFrame extends JDialog {

    public GameOverFrame() {
        super((Frame) null, true); 
        setUndecorated(true);
        setSize(1000, 800); 
        setLocationRelativeTo(null);
        setBackground(new Color(0, 0, 0, 0));

        JPanel overlayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 150)); 
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        overlayPanel.setOpaque(false);
        overlayPanel.setLayout(null);

        JPanel boxPanel = new JPanel();
        boxPanel.setBackground(new Color(173, 216, 230)); 
        boxPanel.setLayout(new BorderLayout());
        int boxWidth = 400;
        int boxHeight = 300;
        boxPanel.setBounds(
                (getWidth() - boxWidth) / 2,
                (getHeight() - boxHeight) / 2 - 50,
                boxWidth,
                boxHeight
        );

        JLabel gameOverLabel = new JLabel("Game Over", SwingConstants.CENTER);
        gameOverLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
        gameOverLabel.setForeground(new Color(255, 0, 102));
        boxPanel.add(gameOverLabel, BorderLayout.NORTH);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        RoundedButton retryButton = new RoundedButton("Retry", 20);
        retryButton.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        retryButton.setBackground(new Color(255, 165, 0)); 
        retryButton.setForeground(Color.WHITE);  
        retryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        retryButton.setPreferredSize(new Dimension(200, 60));

        RoundedButton quitButton = new RoundedButton("Quit Game", 20);
        quitButton.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        quitButton.setBackground(new Color(255, 165, 0)); 
        quitButton.setForeground(Color.LIGHT_GRAY); 
        quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        quitButton.setPreferredSize(new Dimension(150, 50));

        buttonPanel.add(Box.createVerticalStrut(50)); 
        buttonPanel.add(retryButton);
        buttonPanel.add(Box.createVerticalStrut(10)); 
        buttonPanel.add(quitButton);
        buttonPanel.add(Box.createVerticalGlue());
        boxPanel.add(buttonPanel, BorderLayout.SOUTH);

        retryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); 
                closeGameFrame(); 
                new GameFrame("PlayerName").setVisible(true); 
            }
        });

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                closeGameFrame(); 
                System.exit(0); 
            }
        });

        overlayPanel.add(boxPanel);
        setContentPane(overlayPanel);
    }

    private void closeGameFrame() {
        for (Window window : Window.getWindows()) {
            if (window instanceof GameFrame) {
                window.dispose(); 
            }
        }
    }
    
    public class RoundedButton extends JButton {
        private int radius;

        public RoundedButton(String text, int radius) {
            super(text);
            this.radius = radius;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Color c = getBackground();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setColor(c);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            FontMetrics fm = g2.getFontMetrics();
            Rectangle stringBounds = fm.getStringBounds(getText(), g2).getBounds();
            int textX = (getWidth() - stringBounds.width) / 2;
            int textY = (getHeight() - stringBounds.height) / 2 + fm.getAscent();
            g2.setColor(getForeground());
            g2.setFont(getFont());
            g2.drawString(getText(), textX, textY);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
        }
    }
}
