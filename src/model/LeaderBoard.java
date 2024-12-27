package model;

import java.io.*;
import java.util.*;

public class LeaderBoard {
    private String filePath;
    private List<PlayerScore> scores; // Lưu cả tên và điểm

    // Lớp phụ để lưu tên và điểm
    public static class PlayerScore {
        private String playerName;
        private int score;

        public PlayerScore(String playerName, int score) {
            this.playerName = playerName;
            this.score = score;
        }

        public String getPlayerName() {
            return playerName;
        }

        public int getScore() {
            return score;
        }

        @Override
        public String toString() {
            return playerName + ":" + score; // Định dạng "Tên:Điểm"
        }

        public static PlayerScore fromString(String line) {
            String[] parts = line.split(":"); // Tách chuỗi theo dấu ":"
            return new PlayerScore(parts[0], Integer.parseInt(parts[1]));
        }
    }

    // Constructor
    public LeaderBoard(String filePath) {
        this.filePath = filePath;
        this.scores = new ArrayList<>();
        loadScores(); // Tải danh sách điểm từ file
    }

    // Tải điểm từ file
    private void loadScores() {
        scores = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                scores.add(PlayerScore.fromString(line)); // Chuyển từng dòng thành PlayerScore
            }
        } catch (IOException e) {
            System.out.println("Lỗi khi đọc file điểm: " + e.getMessage());
        }
    }

    // Lưu điểm vào file
    private void saveScores() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (PlayerScore score : scores) {
                bw.write(score.toString()); // Lưu theo định dạng "Tên:Điểm"
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Lỗi khi ghi file điểm: " + e.getMessage());
        }
    }

    // Thêm điểm mới
    public void addScore(String playerName, int score) {
        scores.add(new PlayerScore(playerName, score)); // Thêm tên và điểm vào danh sách
        scores.sort((a, b) -> b.getScore() - a.getScore()); // Sắp xếp giảm dần theo điểm
        saveScores(); // Ghi lại danh sách vào file
    }

    // Lấy danh sách điểm cao nhất (top N)
    public List<PlayerScore> getTopScores(int n) {
        scores.sort((a, b) -> b.getScore() - a.getScore()); // Sắp xếp trước khi trả về
        return scores.subList(0, Math.min(n, scores.size()));
    }
}
