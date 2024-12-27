package main;
import java.io.File;
import java.util.Random;
import javax.sound.sampled.*;

public class WavPlayer {
    // Phương thức chung để phát âm thanh từ file
    private static Clip clip;
    public static void playSound(String filePath) {
        try {
            File soundFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }

    // Phương thức phát âm thanh click
    public static void playClickSound() {
         Random random = new Random();
    int soundChoice = random.nextInt(2) + 1;

         String filePath;
    if (soundChoice == 1) {
        filePath = "src\\resources\\sound\\fall1.wav";
    } else {
        filePath = "src\\resources\\sound\\fall2.wav";
    }

    // Phát âm thanh
    playSound(filePath);
    }
    public static void playMergeSound() {
        Random random = new Random();
        int soundChoice = random.nextInt(2) + 1;
    
             String filePath;
        if (soundChoice == 1) {
            filePath = "C:\\Users\\ASUS TUF\\SuikaCloneCS3360\\src\\resources\\sound\\merge1.wav";
        } else {
            filePath = "C:\\Users\\ASUS TUF\\SuikaCloneCS3360\\src\\resources\\sound\\merge2.wav";
        }
    
        // Phát âm thanh
        playSound(filePath);
    }
    public static void playBackgroundMusic(String filePath) {
        try {
            File soundFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);

            // Đặt nhạc nền lặp lại vô hạn
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception e) {
            System.err.println("Error playing background music: " + e.getMessage());
        }
    }
    public static void stopBackgroundMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
}
