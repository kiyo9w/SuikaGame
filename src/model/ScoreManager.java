package model;
import java.util.ArrayList;
import java.util.List;

public class ScoreManager {
    private int score;
    private List<ScorePopup> scorePopups;

    public ScoreManager() {
        this.score = 0;
        this.scorePopups = new ArrayList<>();
    }

    public void addPoints(int level, double x, double y) {
        int points = (int) Math.pow(2, level);
        score += points;
        scorePopups.add(new ScorePopup(x, y, points));
    }

    public void updatePopups() {
        for (ScorePopup popup : scorePopups) {
            popup.update();
        }
        scorePopups.removeIf(ScorePopup::isFinished);
    }

    public List<ScorePopup> getScorePopups() {
        return scorePopups;
    }

    public int getScore() {
        return score;
    }
    public void resetScore () {

        score = 0;
    }
}
