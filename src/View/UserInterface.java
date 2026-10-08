package View;
import IView.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class UserInterface extends JFrame implements IView {

    private InputListener listener;

    private final JLabel scoreLabel = new JLabel("Score: 0");
    private final JLabel statusLabel = new JLabel(" ");
    private final JLabel[][] cells = new JLabel[4][4];

    private final JButton restartButton = new JButton("Restart");
    private final JButton quitButton = new JButton("Quit");
    private final JButton autoButton = new JButton("Auto");

    // Animation state för A nivå
    private int[][] lastBoard = new int[4][4];

    private final JButton undoButton = new JButton("Undo");
    private final JButton saveButton = new JButton("Save");
    private final JButton loadButton = new JButton("Load");

    private final JTextArea highScoreArea = new JTextArea(10, 10);





    public UserInterface() {
        super("2048");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Top panel: score + status
        JPanel top = new JPanel(new GridLayout(2, 1));
        top.add(scoreLabel);
        top.add(statusLabel);
        add(top, BorderLayout.NORTH);

        // Center grid: 4x4
        JPanel grid = new JPanel(new GridLayout(4, 4, 6, 6));
        grid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Font cellFont = new Font(Font.SANS_SERIF, Font.BOLD, 22);

        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                JLabel label = new JLabel("", SwingConstants.CENTER);
                label.setFont(cellFont);
                label.setOpaque(true);
                label.setBackground(new Color(240, 240, 240)); // neutral, inte “A-nivå styling”
                label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                cells[r][c] = label;
                grid.add(label);
            }
        }

        add(grid, BorderLayout.CENTER);

        // Bottom panel: restart + quit
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(restartButton);
        bottom.add(quitButton);
        bottom.add(autoButton);
        bottom.add(undoButton);
        bottom.add(saveButton);
        bottom.add(loadButton);
        add(bottom, BorderLayout.SOUTH);

        restartButton.addActionListener(e -> {
            if (listener != null) listener.onRestart();
        });
        quitButton.addActionListener(e -> {
            if (listener != null) listener.onQuit();
        });
        autoButton.addActionListener(e -> {
            if (listener != null) listener.onAuto();
        });
        undoButton.addActionListener(e -> {
            if (listener != null) listener.onUndo();
        });
        saveButton.addActionListener(e -> {
            if (listener != null) listener.onSave();
        });
        loadButton.addActionListener(e -> {
            if (listener != null) listener.onLoad();
        });

        setupKeyBindings();

        //high scores
        highScoreArea.setEditable(false);
        highScoreArea.setFocusable(false);
        highScoreArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        highScoreArea.setBorder(BorderFactory.createTitledBorder("High Scores"));
        highScoreArea.setText("Play to see highscores\n");

        JScrollPane hsScroll = new JScrollPane(highScoreArea);
        hsScroll.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));

        add(hsScroll, BorderLayout.EAST);


        pack();
        setLocationRelativeTo(null);
        setVisible(true);


    }

    @Override
    public void setListener(InputListener listener) {
        this.listener = listener;
    }

    @Override
    public void render(int[][] board, int score, boolean gameOver) {
        scoreLabel.setText("Score: " + score);
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                int v = board[r][c];
                // Sätt färg + text
                applyTileStyle(cells[r][c], v);
                // Enkel animation när värdet ändras
                if (lastBoard[r][c] != v && v != 0) {
                    animatePop(cells[r][c]);
                }
                // Spara för nästa render
                lastBoard[r][c] = v;
            }
        }
        if (gameOver) {
            statusLabel.setText("Game Over! Press Restart.");
        } else {
            statusLabel.setText(" ");
        }
    }


    @Override
    public void showMessage(String message) {
        statusLabel.setText(message);
    }



    @Override
    public void close() {
        dispose();

    }

    private void setupKeyBindings() {
        JComponent root = getRootPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        // WASD
        bind(im, am, "W", () -> { if (listener != null) listener.onUp(); });
        bind(im, am, "A", () -> { if (listener != null) listener.onLeft(); });
        bind(im, am, "S", () -> { if (listener != null) listener.onDown(); });
        bind(im, am, "D", () -> { if (listener != null) listener.onRight(); });

        // Arrow keys
        bindKeyStroke(im, am, KeyStroke.getKeyStroke("UP"),    () -> { if (listener != null) listener.onUp(); });
        bindKeyStroke(im, am, KeyStroke.getKeyStroke("LEFT"),  () -> { if (listener != null) listener.onLeft(); });
        bindKeyStroke(im, am, KeyStroke.getKeyStroke("DOWN"),  () -> { if (listener != null) listener.onDown(); });
        bindKeyStroke(im, am, KeyStroke.getKeyStroke("RIGHT"), () -> { if (listener != null) listener.onRight(); });

        // Restart + Quit
        bind(im, am, "R", () -> { if (listener != null) listener.onRestart(); });
        bind(im, am, "Q", () -> { if (listener != null) listener.onQuit(); });
    }

    private void bind(InputMap im, ActionMap am, String key, Runnable action) {
        bindKeyStroke(im, am, KeyStroke.getKeyStroke(key), action);
    }

    private void bindKeyStroke(InputMap im, ActionMap am, KeyStroke ks, Runnable action) {
        String name = "action_" + ks.toString();
        im.put(ks, name);
        am.put(name, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }

    //Visuellt för A

    @Override
    public void showHighScores(java.util.List<Integer> scores) {

        StringBuilder sb = new StringBuilder();

        int limit = Math.min(scores.size(), 10); // visa max 10
        for (int i = 0; i < limit; i++) {
            sb.append(i + 1).append(". ").append(scores.get(i)).append("\n");
        }

        highScoreArea.setText(sb.toString());
    }


    // Tile Styling
    private Color colorForValue(int value) {
        return switch (value) {
            case 0    -> new Color(240, 240, 240);
            case 2    -> new Color(238, 228, 218);
            case 4    -> new Color(237, 224, 200);
            case 8    -> new Color(242, 177, 121);
            case 16   -> new Color(245, 149, 99);
            case 32   -> new Color(246, 124, 95);
            case 64   -> new Color(246, 94, 59);
            case 128  -> new Color(237, 207, 114);
            case 256  -> new Color(237, 204, 97);
            case 512  -> new Color(237, 200, 80);
            case 1024 -> new Color(237, 197, 63);
            case 2048 -> new Color(237, 194, 46);
            default   -> new Color(60, 58, 50);
        };
    }

    private Color textColorForValue(int value) {
        return value <= 4 ? new Color(119, 110, 101) : Color.WHITE;
    }

    private void applyTileStyle(JLabel label, int value) {
        label.setOpaque(true);
        label.setBackground(colorForValue(value));
        label.setForeground(textColorForValue(value));
        label.setText(value == 0 ? "" : String.valueOf(value));
    }

    //Animation
    private void animatePop(JLabel label) {
        Font original = label.getFont();
        Font bigger = original.deriveFont(original.getSize2D() + 6f);

        label.setFont(bigger);

        new javax.swing.Timer(120, e -> {
            label.setFont(original);
            ((javax.swing.Timer) e.getSource()).stop();
        }).start();
    }






}
