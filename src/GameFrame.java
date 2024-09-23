import javax.swing.*;

public class GameFrame extends JFrame {
    public GameFrame() {
        setTitle("Suika Game Clone with Physics");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 600);
        setLocationRelativeTo(null);
        add(new GamePanel());
    }
}
