import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import main.GameFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String playerName = JOptionPane.showInputDialog("Enter your name:");
            if (playerName == null || playerName.trim().isEmpty()) {
                playerName = "Player"; // Default name if none entered
            }
            GameFrame frame = new GameFrame(playerName);
            frame.setVisible(true);
        });
    }
}
