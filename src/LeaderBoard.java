import java.io.*;
import java.util.*;

public class LeaderBoard {
    private String filePath;
    private List<Integer> scores;

    // Constructor nhận đường dẫn đến file trong thư mục src
    public LeaderBoard(String filePath) {
           this.filePath = filePath;
        this.scores = new ArrayList<>();
        
        loadScores();  // Sau đó mới load điểm
    }

   
    // Tải điểm từ file
    private void loadScores() {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                int score = Integer.parseInt(line);
                scores.add(score);  // Thêm điểm vào danh sách
            }
        } catch (IOException e) {
            System.out.println("Lỗi khi đọc file điểm: " + e.getMessage());
        }
    }

    // Lưu điểm vào file
    private void saveScores() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Integer score : scores) {
                bw.write(score.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Lỗi khi ghi file điểm: " + e.getMessage());
        }
    }

    // Thêm điểm mới
    public void addScore(int score) {
        scores.add(score);
        saveScores();
    }

    // Lấy danh sách điểm cao nhất (top N)
    public List<Integer> getTopScores(int n) {
        scores.sort(Collections.reverseOrder());
        return scores.subList(0, Math.min(n, scores.size()));
    }

    // Hiển thị bảng xếp hạng
    public void displayLeaderboard(int n) {
        List<Integer> topScores = getTopScores(n);
        System.out.println("Bảng xếp hạng:");
        for (int i = 0; i < topScores.size(); i++) {
            System.out.println((i + 1) + ". " + topScores.get(i));
        }
    }
}
