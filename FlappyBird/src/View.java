import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class View extends JPanel {
    int width = 360;
    int height = 640;
    private Logic logic;
    private Image[][] backgrounds;
    private int selectedBackground = 0;
    public static final String[] BACKGROUND_NAMES = {"Hutan", "Gunung", "Jungle", "Air Terjun"};

    public View(Logic logic) {
        this.logic = logic;
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.cyan);
        backgrounds = new Image[][]{
            load("bg_forest"), load("bg_mountain"), load("bg_tileset"), load("bg_waterfall", 8)
        };
        setFocusable(true);
        addKeyListener(logic);
    }

    private Image[] load(String name) {
        return new Image[]{new ImageIcon(getClass().getResource("/assets/" + name + ".png")).getImage()};
    }

    // background beranimasi: frame bernomor name0..name(count-1)
    private Image[] load(String name, int count) {
        Image[] frames = new Image[count];
        for (int i = 0; i < count; i++) {
            frames[i] = new ImageIcon(getClass().getResource("/assets/" + name + i + ".png")).getImage();
        }
        return frames;
    }

    public int getSelectedBackground() { return selectedBackground; }

    public void setSelectedBackground(int index) {
        if (index >= 0 && index < backgrounds.length) selectedBackground = index;
        repaint();
    }

    public Image getBackgroundPreview(int index) { return backgrounds[index][0]; }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        Image[] frames = backgrounds[selectedBackground];
        Image bg = frames[(int) ((System.currentTimeMillis() / 100) % frames.length)];
        g.drawImage(bg, 0, 0, width, height, null);

        Player player = logic.getPlayer();
        if (player != null) {
            g.drawImage(player.getImage(), player.getPosX(), player.getPosY(),
                    player.getWidth(), player.getHeight(), null);
        }

        ArrayList<Pipe> pipes = logic.getPipes();
        if (pipes != null) {
            for (Pipe pipe : pipes) {
                g.drawImage(pipe.getImage(), pipe.getPosX(), pipe.getPosY(),
                        pipe.getWidth(), pipe.getHeight(), null);
            }
        }

        if (!logic.isStarted() && !logic.isGameOver()) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            FontMetrics fm = g.getFontMetrics();
            String s = "Tekan SPASI untuk mulai";
            g.drawString(s, (getWidth() - fm.stringWidth(s)) / 2, height / 2 - 60);
        }

        if (logic.isGameOver()) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            FontMetrics fm1 = g.getFontMetrics();
            String gameOverStr = "GAME OVER";
            int x1 = (getWidth() - fm1.stringWidth(gameOverStr)) / 2;
            g.drawString(gameOverStr, x1, height / 2 - 20);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            FontMetrics fm2 = g.getFontMetrics();
            String restartStr = "Tekan 'R' untuk Restart";
            int x2 = (getWidth() - fm2.stringWidth(restartStr)) / 2;
            g.drawString(restartStr, x2, height / 2 + 20);
        }
    }
}
