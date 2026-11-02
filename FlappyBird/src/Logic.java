import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class Logic implements ActionListener, KeyListener {
    int frameWidth = 360;
    int frameHeight = 640;

    int playerStartPosX = frameWidth / 8;
    int playerStartPosY = frameHeight / 2;
    int playerWidth = 34;
    int playerHeight = 24;

    int pipeStartPosX = frameWidth;
    int pipeStartPosY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    Timer gameLoop;
    Timer pipesCooldown;
    int gravity = 1;
    int pipeVelocityX = -2;

    View view;
    Image[] birdFrames;
    ScoreHistory history = new ScoreHistory();
    SoundManager sound = new SoundManager();
    int tick = 0;
    Player player;
    Image lowerPipeImage;
    Image upperPipeImage;
    ArrayList<Pipe> pipes;

    private int score = 0;
    private boolean gameOver = false;
    private boolean started = false;   // false = burung diam menunggu SPASI
    private boolean paused = false;
    private boolean scoreSaved = false;
    private JLabel scoreLabel;

    public Logic() {
        birdFrames = new Image[4];
        for (int i = 0; i < birdFrames.length; i++) {
            birdFrames[i] = new ImageIcon(getClass().getResource("/assets/bird" + i + ".png")).getImage();
        }
        lowerPipeImage = new ImageIcon(getClass().getResource("/assets/lowerPipe.png")).getImage();
        upperPipeImage = new ImageIcon(getClass().getResource("/assets/upperPipe.png")).getImage();

        player = new Player(playerStartPosX, playerStartPosY, playerWidth, playerHeight, birdFrames[0]);
        pipes = new ArrayList<>();

        pipesCooldown = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                placePipes();
            }
        });
        // pipesCooldown baru dijalankan saat SPASI pertama ditekan

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    public void setView(View view) {
        this.view = view;
    }

    public void setScoreLabel(JLabel scoreLabel) {
        this.scoreLabel = scoreLabel;
    }

    public Player getPlayer() { return player; }
    public ArrayList<Pipe> getPipes() { return pipes; }
    public int getScore() { return score; }
    public boolean isGameOver() { return gameOver; }
    public boolean isStarted() { return started; }
    public ScoreHistory getHistory() { return history; }
    public SoundManager getSound() { return sound; }

    public void pause() {
        if (started && !gameOver && !paused) {
            paused = true;
            pipesCooldown.stop();
        }
    }

    public void resume() {
        if (paused) {
            paused = false;
            if (started && !gameOver) pipesCooldown.start();
        }
    }

    /** Simpan skor jika pemain keluar/pulang di tengah permainan. */
    public void saveIfAbandoned() {
        if (started && !gameOver && !scoreSaved && score > 0) {
            history.add(score);
            scoreSaved = true;
        }
    }

    public void placePipes() {
        int randomPosY = (int) (pipeStartPosY - (pipeHeight / 4) - Math.random() * (pipeHeight / 2));
        int openingSpace = frameHeight / 4;

        Pipe upperPipe = new Pipe(pipeStartPosX, randomPosY, pipeWidth, pipeHeight, upperPipeImage);
        pipes.add(upperPipe);

        Pipe lowerPipe = new Pipe(pipeStartPosX, randomPosY + pipeHeight + openingSpace, pipeWidth, pipeHeight, lowerPipeImage);
        pipes.add(lowerPipe);
    }

    public void move() {
        if (gameOver) return;
        tick++;
        player.setImage(birdFrames[(tick / 8) % birdFrames.length]);   // animasi sayap
        if (paused || !started) return;

        player.setVelocityY(player.getVelocityY() + gravity);
        player.setPosY(player.getPosY() + player.getVelocityY());
        player.setPosY(Math.max(player.getPosY(), 0));

        if (player.getPosY() + player.getHeight() >= frameHeight) {
            triggerGameOver();
        }

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.setPosX(pipe.getPosX() + pipeVelocityX);

            if (!pipe.isPassed() && player.getPosX() > pipe.getPosX() + pipe.getWidth()) {
                pipe.setPassed(true);
                if (pipe.getImage() == upperPipeImage) {
                    score++;
                    sound.playPoint();
                    if (scoreLabel != null) {
                        scoreLabel.setText("Score: " + score);
                    }
                }
            }

            if (checkCollision(player, pipe)) {
                triggerGameOver();
            }
        }
    }

    private boolean checkCollision(Player a, Pipe b) {
        return a.getPosX() < b.getPosX() + b.getWidth() &&
               a.getPosX() + a.getWidth() > b.getPosX() &&
               a.getPosY() < b.getPosY() + b.getHeight() &&
               a.getPosY() + a.getHeight() > b.getPosY();
    }

    private void triggerGameOver() {
        if (gameOver) return;
        gameOver = true;
        sound.playHit();
        if (!scoreSaved) {
            history.add(score);
            scoreSaved = true;
        }
        gameLoop.stop();
        pipesCooldown.stop();
    }

    public void restartGame() {
        player.setPosY(playerStartPosY);
        player.setVelocityY(0);
        pipes.clear();
        score = 0;
        if (scoreLabel != null) {
            scoreLabel.setText("Score: 0");
        }
        gameOver = false;
        started = false;      // burung diam sampai SPASI ditekan
        paused = false;
        scoreSaved = false;
        pipesCooldown.stop();
        gameLoop.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        if (view != null) {
            view.repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!gameOver && !paused) {
                if (!started) {
                    started = true;
                    pipesCooldown.start();
                }
                player.setVelocityY(-10);
                sound.playFlap();
            }
        } else if (e.getKeyCode() == KeyEvent.VK_R) {
            if (gameOver) {
                restartGame();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
