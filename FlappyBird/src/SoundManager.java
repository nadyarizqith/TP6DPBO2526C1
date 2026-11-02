import javax.sound.sampled.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Efek suara (flap, point, hit) dan backsound.
 * Backsound diputar berurutan & berulang dari main menu sampai game berjalan,
 * dengan volume kecil agar efek suara lebih terdengar.
 */
public class SoundManager {
    private static final float MUSIC_GAIN_DB = -22f;   // pelan
    private static final float SFX_GAIN_DB = 0f;       // jelas

    private static final String[] MUSIC_FILES = {"music1", "music2", "music3"};

    private final byte[] flap = readAll("sfx_flap");
    private final byte[] point = readAll("sfx_point");
    private final byte[] hit = readAll("sfx_hit");

    private Clip musicClip;
    private int musicIndex = 0;
    private boolean musicRunning = false;

    private static byte[] readAll(String name) {
        try (InputStream in = SoundManager.class.getResourceAsStream("/assets/" + name + ".wav")) {
            if (in == null) return null;
            return in.readAllBytes();
        } catch (IOException e) {
            return null;
        }
    }

    private static void setGain(Clip clip, float db) {
        try {
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), db)));
        } catch (Exception ignored) {}
    }

    private void playSfx(byte[] data) {
        if (data == null) return;
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new ByteArrayInputStream(data));
            Clip clip = AudioSystem.getClip();
            clip.addLineListener(ev -> {
                if (ev.getType() == LineEvent.Type.STOP) clip.close();
            });
            clip.open(ais);
            setGain(clip, SFX_GAIN_DB);
            clip.start();
        } catch (Exception ignored) {
            // tidak ada perangkat audio: game tetap jalan tanpa suara
        }
    }

    public void playFlap() { playSfx(flap); }
    public void playPoint() { playSfx(point); }
    public void playHit() { playSfx(hit); }

    public synchronized void startMusic() {
        if (musicRunning) return;
        musicRunning = true;
        playNextTrack();
    }

    private synchronized void playNextTrack() {
        if (!musicRunning) return;
        byte[] data = readAll(MUSIC_FILES[musicIndex]);
        musicIndex = (musicIndex + 1) % MUSIC_FILES.length;
        if (data == null) return;
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new ByteArrayInputStream(data));
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            setGain(clip, MUSIC_GAIN_DB);
            clip.addLineListener(ev -> {
                if (ev.getType() == LineEvent.Type.STOP && musicRunning) {
                    // lagu selesai -> lanjut lagu berikutnya (bukan karena stopMusic)
                    if (clip.getFramePosition() >= clip.getFrameLength() - 1) {
                        clip.close();
                        playNextTrack();
                    }
                }
            });
            musicClip = clip;
            clip.start();
        } catch (Exception ignored) {
            musicRunning = false;
        }
    }

    public synchronized void stopMusic() {
        musicRunning = false;
        if (musicClip != null) {
            musicClip.stop();
            musicClip.close();
            musicClip = null;
        }
    }
}
