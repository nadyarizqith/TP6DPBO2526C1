import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class App {
    static Logic logic;
    static JFrame frame;

    /** Simpan skor, tampilkan history score, lalu keluar. */
    static void quit() {
        logic.saveIfAbandoned();
        logic.getSound().stopMusic();
        JTextArea area = new JTextArea(logic.getHistory().toText(10));
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JOptionPane.showMessageDialog(frame, area, "History Score", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    /** Dialog pilih background (gambar kecil + nama). */
    static void chooseBackground(View view) {
        JDialog dialog = new JDialog(frame, "Pilih Background", true);
        JPanel grid = new JPanel(new GridLayout(2, 2, 10, 10));
        grid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JButton[] buttons = new JButton[View.BACKGROUND_NAMES.length];
        for (int i = 0; i < buttons.length; i++) {
            final int idx = i;
            Image thumb = view.getBackgroundPreview(i).getScaledInstance(90, 160, Image.SCALE_SMOOTH);
            JButton b = new JButton(View.BACKGROUND_NAMES[i], new ImageIcon(thumb));
            b.setHorizontalTextPosition(SwingConstants.CENTER);
            b.setVerticalTextPosition(SwingConstants.BOTTOM);
            b.setFont(new Font("Arial", Font.BOLD, 13));
            b.setFocusable(false);
            b.addActionListener(e -> {
                view.setSelectedBackground(idx);
                dialog.dispose();
            });
            buttons[i] = b;
            grid.add(b);
        }
        // tandai pilihan saat ini
        buttons[view.getSelectedBackground()].setBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 4));
        dialog.add(grid);
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    /** Tombol 3 garis (hamburger) di pojok kiri atas layar game. */
    static JButton createMenuButton() {
        JButton b = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.DARK_GRAY);
                g2.setStroke(new BasicStroke(2));
                g2.drawRect(3, 3, getWidth() - 7, getHeight() - 7);
                int x1 = 9, x2 = getWidth() - 10;
                for (int i = 1; i <= 3; i++) {
                    int y = 3 + (getHeight() - 6) * i / 4;
                    g2.drawLine(x1, y, x2, y);
                }
            }
        };
        b.setPreferredSize(new Dimension(40, 32));
        b.setFocusable(false);
        b.setToolTipText("Menu");
        return b;
    }

    public static void main(String[] args) {
        frame = new JFrame("FlappyBird");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) { quit(); }
        });
        frame.setResizable(false);

        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        JPanel gameContainer = new JPanel(new BorderLayout());

        JLabel scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        scoreLabel.setOpaque(true);
        scoreLabel.setBackground(Color.LIGHT_GRAY);

        logic = new Logic();
        logic.setScoreLabel(scoreLabel);
        View gameView = new View(logic);
        logic.setView(gameView);

        MainMenu[] menuRef = new MainMenu[1];

        // Popup menu: Home, Menu (lanjut main), Quit
        JPopupMenu popup = new JPopupMenu();
        JMenuItem homeItem = new JMenuItem("Home");
        JMenuItem menuItem = new JMenuItem("Menu");
        JMenuItem quitItem = new JMenuItem("Quit");
        for (JMenuItem it : new JMenuItem[]{homeItem, menuItem, quitItem}) {
            it.setFont(new Font("Arial", Font.BOLD, 14));
            popup.add(it);
        }
        homeItem.addActionListener(e -> {
            logic.saveIfAbandoned();
            logic.restartGame();
            menuRef[0].refreshHistory();
            cardLayout.show(mainPanel, "MENU");
        });
        menuItem.addActionListener(e -> { /* tutup popup & lanjut main */ });
        quitItem.addActionListener(e -> quit());
        popup.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) { logic.pause(); }
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                logic.resume();
                gameView.requestFocusInWindow();
            }
            public void popupMenuCanceled(PopupMenuEvent e) {}
        });

        JButton menuButton = createMenuButton();
        menuButton.addActionListener(e -> popup.show(menuButton, 0, menuButton.getHeight()));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.LIGHT_GRAY);
        topBar.add(menuButton, BorderLayout.WEST);
        topBar.add(scoreLabel, BorderLayout.CENTER);
        topBar.add(Box.createHorizontalStrut(40), BorderLayout.EAST); // agar skor tetap di tengah

        gameContainer.add(topBar, BorderLayout.NORTH);
        gameContainer.add(gameView, BorderLayout.CENTER);

        MainMenu mainMenu = new MainMenu(
            e -> {
                logic.restartGame();   // burung diam sampai SPASI ditekan
                cardLayout.show(mainPanel, "GAME");
                gameView.requestFocusInWindow();
            },
            e -> chooseBackground(gameView),
            e -> quit(),
            logic.getHistory()
        );
        menuRef[0] = mainMenu;

        mainPanel.add(mainMenu, "MENU");
        mainPanel.add(gameContainer, "GAME");

        frame.add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        logic.getSound().startMusic();   // backsound mulai dari main menu
    }
}
