import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class MainMenuPanel extends JPanel {
    private CustomButton playButton;
    private CustomButton creditsButton;
    private Image backgroundImage; // Biến để lưu hình ảnh nền

    public MainMenuPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Sử dụng BoxLayout cho panel

        // Tải hình ảnh nền
        backgroundImage = new ImageIcon(getClass().getResource("/resources/window_background.jpeg")).getImage();

        // Kích thước chung cho các nút
        Dimension buttonSize = new Dimension(200, 60);

        // Tạo nút "Play" với màu cam
        playButton = new CustomButton("Play", new Color(255, 165, 0)); // Màu cam
        playButton.setPreferredSize(buttonSize); // Kích thước nút
        playButton.setMaximumSize(buttonSize); // Kích thước tối đa
        playButton.setMinimumSize(buttonSize); // Kích thước tối thiểu
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Căn giữa nút


        // Tạo nút "Credits" với màu xanh lá
        creditsButton = new CustomButton("Credits", new Color(34, 139, 34)); // Màu xanh lá
        creditsButton.setPreferredSize(buttonSize); // Kích thước nút
        creditsButton.setMaximumSize(buttonSize); // Kích thước tối đa
        creditsButton.setMinimumSize(buttonSize); // Kích thước tối thiểu
        creditsButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Căn giữa nút

        // Thêm khoảng trống để căn giữa các nút
        add(Box.createVerticalGlue()); // Thêm khoảng trống phía trên
        add(playButton); // Thêm nút "Play"
        add(Box.createVerticalStrut(10)); // Thêm khoảng cách giữa hai nút
        add(creditsButton); // Thêm nút "Credits"
        add(Box.createVerticalGlue()); // Thêm khoảng trống phía dưới

        // Sự kiện khi nhấn nút "Credits"
        creditsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String credits = "Team Members:\n"
                        + "- Member 1: Ngo Thanh Trung\n"
                        + "- Member 2: Nguyen Duy Duc\n"
                        + "- Member 3: Nguyen Duy Khoi\n"
                        + "- Member 4: Nguyen Quan Huy";
                
                JOptionPane.showMessageDialog(null, credits, "Credits", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Thiết lập khung cho toàn bộ panel
        setBorder(new LineBorder(Color.BLACK, 5)); // Thêm khung cho toàn bộ màn hình chính
    }

    // Phương thức trả về nút "Play"
    public JButton getPlayButton() {
        return playButton; // Trả về nút Play
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Vẽ hình ảnh nền
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // Vẽ hình ảnh nền
        }
        
        // Vẽ tiêu đề
        g.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
        g.setColor(Color.DARK_GRAY);
        g.drawString("Welcome to Suika Game Clone", 200, 100);
    }

    // Lớp tùy chỉnh cho nút
    class CustomButton extends JButton {
        public CustomButton(String text, Color color) {
            super(text); // Thiết lập văn bản cho nút
            setBackground(color); // Thiết lập màu nền cho nút
            setForeground(Color.WHITE); // Màu chữ
            setBorder(new LineBorder(Color.BLACK, 2)); // Thêm khung cho nút
            setBorderPainted(true); // Hiển thị viền
           
            setOpaque(true); // Đảm bảo màu nền hiển thị
            setFocusPainted(false); // Tắt hiệu ứng khi nút được chọn
            setFont(new Font("Comic Sans MS", Font.BOLD, 20)); // Đặt kích thước chữ
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // Vẽ văn bản lên nút
        }
    }
}
