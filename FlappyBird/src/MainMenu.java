import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainMenu extends JPanel {
    private final JLabel historyLabel;
    private final ScoreHistory history;

    public MainMenu(ActionListener onStart, ActionListener onBackground, ActionListener onExit, ScoreHistory history) {
        this.history = history;
        setPreferredSize(new Dimension(360, 640));
        setBackground(Color.cyan);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("FLAPPY BIRD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.DARK_GRAY);
        gbc.gridy = 0;
        add(titleLabel, gbc);

        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.addActionListener(onStart);
        gbc.gridy = 1;
        add(startButton, gbc);

        JButton bgButton = new JButton("Background");
        bgButton.setFont(new Font("Arial", Font.BOLD, 18));
        bgButton.addActionListener(onBackground);
        gbc.gridy = 2;
        add(bgButton, gbc);

        JButton exitButton = new JButton("Exit Game");
        exitButton.setFont(new Font("Arial", Font.BOLD, 18));
        exitButton.addActionListener(onExit);
        gbc.gridy = 3;
        add(exitButton, gbc);

        JLabel historyTitle = new JLabel("HISTORY SCORE", SwingConstants.CENTER);
        historyTitle.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridy = 4;
        add(historyTitle, gbc);

        historyLabel = new JLabel("", SwingConstants.CENTER);
        historyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 5;
        add(historyLabel, gbc);

        refreshHistory();
    }

    public void refreshHistory() {
        historyLabel.setText(history.toHtml(5));
    }
}
