package com.craftinginterpreters.testui;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class PracticePanel extends JPanel {

    private JComboBox<String> modeBox;
    private JComboBox<String> timeBox;
    private JComboBox<String> fontSizeBox;
    private JButton resetButton;
    private JTextPane typingPane;
    private JLabel timerLabel;

    private List<String> paragraphs = new ArrayList<>();
    private String text;
    private String[] words;
    private StringBuilder typedText = new StringBuilder();
    private int currentWordIndex = 0;
    private int charIndex = 0;
    private long startTime = 0;
    private boolean typingStarted = false;

    private StyledDocument doc;
    private Style defaultStyle, correctStyle, wrongStyle, currentStyle;

    private javax.swing.Timer countdownTimer;
    private int timeRemaining;

    private final Random rand = new Random();

    public PracticePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 30));

        loadParagraphs();
        initTopPanel();
        initTypingPane();
        resetText();
    }

    private void loadParagraphs() {
        try {
            Path path = Paths.get(System.getProperty("user.dir"), "paragraphs.txt");
            if (Files.exists(path)) {
                List<String> lines = Files.readAllLines(path);
                paragraphs = new ArrayList<>(lines);
            } else {
                paragraphs.add("Typing fast is a skill that requires practice, patience, and consistency.");
                paragraphs.add("Focus on accuracy before increasing speed to build proper muscle memory.");
                paragraphs.add("A clean and distraction free workspace helps maintain better focus.");
                paragraphs.add("Consistency matters more than intensity when building new skills.");
                paragraphs.add("Every small improvement compounds over time into mastery.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            paragraphs.add("Error loading paragraphs file. Default text loaded.");
        }
    }

    private void initTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(30, 30, 30));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        leftPanel.setBackground(new Color(30, 30, 30));

        modeBox = new JComboBox<>(new String[]{"Basic", "Quotes"});
        timeBox = new JComboBox<>(new String[]{"1 minute", "5 minutes", "Custom", "Dynamic"});
        fontSizeBox = new JComboBox<>(new String[]{"14", "16", "18", "20", "22", "24", "26", "28", "30", "32", "36", "40"});
        fontSizeBox.setSelectedItem("24");
        resetButton = new JButton("Reset");

        leftPanel.add(new JLabel("Mode:"));
        leftPanel.add(modeBox);
        leftPanel.add(new JLabel("Time:"));
        leftPanel.add(timeBox);
        leftPanel.add(new JLabel("Font Size:"));
        leftPanel.add(fontSizeBox);
        leftPanel.add(resetButton);

        timerLabel = new JLabel("⏱ 00:00");
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setFont(new Font("Consolas", Font.PLAIN, 18));
        timerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(timerLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        resetButton.addActionListener(e -> resetText());
        timeBox.addActionListener(e -> resetText());

    }

    private void initTypingPane() {
    typingPane = new JTextPane() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (typingStarted) {
                try {
                    int offset = getWordStartOffset(words, currentWordIndex) + charIndex;
                    Rectangle rect = modelToView(offset);
                    if (rect != null) {
                        g.setColor(new Color(0, 200, 255)); // cyan caret
                        g.fillRect(rect.x, rect.y + 3, 2, rect.height - 6);
                    }
                } catch (Exception ignored) {}
            }
        }
    };

    typingPane.setFont(new Font("Consolas", Font.PLAIN, 24));
    typingPane.setBackground(new Color(20, 20, 20));
    typingPane.setForeground(Color.WHITE);
    typingPane.setEditable(false);
    typingPane.setHighlighter(null);
    typingPane.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

    JScrollPane scrollPane = new JScrollPane(typingPane);
    add(scrollPane, BorderLayout.CENTER);

    doc = typingPane.getStyledDocument();

    defaultStyle = typingPane.addStyle("default", null);
    StyleConstants.setForeground(defaultStyle, Color.WHITE);

    correctStyle = typingPane.addStyle("correct", null);
    StyleConstants.setForeground(correctStyle, new Color(0, 220, 0));

    wrongStyle = typingPane.addStyle("wrong", null);
    StyleConstants.setForeground(wrongStyle, new Color(255, 70, 70));

    currentStyle = typingPane.addStyle("current", null);
    StyleConstants.setForeground(currentStyle, new Color(100, 180, 255));
    StyleConstants.setUnderline(currentStyle, false);

    fontSizeBox.addActionListener(e -> {
        int newSize = Integer.parseInt((String) fontSizeBox.getSelectedItem());
        typingPane.setFont(new Font("Consolas", Font.PLAIN, newSize));
        typingPane.repaint();
    });

    typingPane.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                handleBackspace();
                e.consume();
                typingPane.repaint();
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            if (!Character.isISOControl(c)) {
                handleFirstKey();
                handleKeyTyped(c);
                typingPane.repaint();
            }
        }
    });

    typingPane.setFocusable(true);
}


    private void handleFirstKey() {
        if (!typingStarted) {
            typingStarted = true;
            startTime = System.currentTimeMillis();

            String selectedTime = (String) timeBox.getSelectedItem();
            if (!"Dynamic".equals(selectedTime)) {
                int minutes = 1;
                if ("5 minutes".equals(selectedTime)) minutes = 5;
                else if ("Custom".equals(selectedTime)) {
                    String input = JOptionPane.showInputDialog(this, "Enter minutes:", "Custom Time", JOptionPane.PLAIN_MESSAGE);
                    try {
                        minutes = Integer.parseInt(input);
                        if (minutes <= 0) minutes = 1;
                    } catch (Exception ex) {
                        minutes = 1;
                    }
                }
                startCountdown(minutes);
            } else {
                timerLabel.setText("⏱ ∞");
            }
        }
    }

    private void startCountdown(int minutes) {
        timeRemaining = minutes * 60;
        updateTimerLabel();
        if (countdownTimer != null && countdownTimer.isRunning()) countdownTimer.stop();
        countdownTimer = new javax.swing.Timer(1000, e -> {
            timeRemaining--;
            updateTimerLabel();
            if (timeRemaining <= 0) {
                ((javax.swing.Timer) e.getSource()).stop();
                JOptionPane.showMessageDialog(this, "Time's up!");
                showResults();
            }
        });
        countdownTimer.start();
    }

    private void updateTimerLabel() {
        int min = timeRemaining / 60;
        int sec = timeRemaining % 60;
        timerLabel.setText(String.format("⏱ %02d:%02d", min, sec));
    }

    private void handleKeyTyped(char c) {
    if (currentWordIndex >= words.length) return;
    String currentWord = words[currentWordIndex];

    if (c == ' ') {
        // On space, finalize the word
        int start = getWordStartOffset(words, currentWordIndex);
        int len = currentWord.length();

        for (int i = 0; i < len; i++) {
            try {
                String expected = String.valueOf(currentWord.charAt(i));
                String typed = (start + i) < typedText.length() ? String.valueOf(typedText.charAt(start + i)) : "";
                doc.setCharacterAttributes(start + i, 1,
                        expected.equals(typed) ? correctStyle : wrongStyle, false);
            } catch (Exception ignored) {}
        }

        typedText.append(' ');
        charIndex = 0;
        currentWordIndex++;

        highlightCurrentWord();
        
        if (currentWordIndex >= words.length) {
            showResults();
        }
    } else {
        // Typing inside current word
        if (charIndex < currentWord.length()) {
            boolean correct = c == currentWord.charAt(charIndex);
            typedText.append(c);
            try {
                doc.setCharacterAttributes(
                        getWordStartOffset(words, currentWordIndex) + charIndex,
                        1,
                        correct ? correctStyle : wrongStyle,
                        false
                );
            } catch (Exception ignored) {}
            charIndex++;
        }
    }
}



    private void handleBackspace() {
        if (charIndex > 0) {
            charIndex--;
            if (typedText.length() > 0) typedText.setLength(typedText.length() - 1);
            int offset = getWordStartOffset(words, currentWordIndex) + charIndex;
            try {
                doc.setCharacterAttributes(offset, 1, currentStyle, false);
            } catch (Exception ignored) {}
        }
    }

    private void highlightCurrentWord() {
    try {
        doc.setCharacterAttributes(0, doc.getLength(), defaultStyle, false);
        int offset = getWordStartOffset(words, currentWordIndex);

        if (currentWordIndex < words.length) {
            int wordLen = words[currentWordIndex].length();
            for (int i = 0; i < wordLen; i++) {
                int globalOffset = offset + i;
                if (globalOffset >= typedText.length()) {
                    doc.setCharacterAttributes(globalOffset, 1, currentStyle, false);
                }
            }
        }
    } catch (Exception ignored) {}
}

   private void showResults() {
    if (startTime == 0) return; 

    long endTime = System.currentTimeMillis();
    double minutes = (endTime - startTime) / 60000.0;
    if (minutes <= 0) minutes = 1.0 / 60.0; 

    String typed = typedText.toString(); 
    int typedLen = typed.length();
    int refLen = text.length();

    int correctChars = 0;
    for (int i = 0; i < typedLen && i < refLen; i++) {
        if (typed.charAt(i) == text.charAt(i)) correctChars++;
    }

    int wpm = (int) ((correctChars / 5.0) / minutes);

    double accuracy = (typedLen > 0)
            ? (double) correctChars / typedLen * 100.0
            : 0.0;

    JOptionPane.showMessageDialog(this,
            "WPM: " + wpm +
            "\nAccuracy: " + String.format("%.2f", accuracy) + "%");
}


    private void resetText() {
        String selectedTime = (String) timeBox.getSelectedItem();
        StringBuilder sb = new StringBuilder();

        if (!"Dynamic".equals(selectedTime)) {
            int minutes = "5 minutes".equals(selectedTime) ? 5 : 1;
            if ("Custom".equals(selectedTime)) {
                String input = JOptionPane.showInputDialog(this, "Enter minutes:", "Custom Time", JOptionPane.PLAIN_MESSAGE);
                try {
                    minutes = Integer.parseInt(input);
                    if (minutes <= 0) minutes = 1;
                } catch (Exception ex) {
                    minutes = 1;
                }
            }
            int targetChars = minutes * 250;
            while (sb.length() < targetChars) {
                sb.append(paragraphs.get(rand.nextInt(paragraphs.size())));
                sb.append(" ");
            }
        } else {
            sb.append(paragraphs.get(rand.nextInt(paragraphs.size())));
        }

        text = sb.toString().trim();
        words = text.split(" ");

        currentWordIndex = 0;
        charIndex = 0;
        typedText.setLength(0);
        typingStarted = false;
        startTime = 0;

        if (countdownTimer != null && countdownTimer.isRunning()) countdownTimer.stop();
        timerLabel.setText("⏱ 00:00");

        try {
            doc.remove(0, doc.getLength());
            doc.insertString(0, text, defaultStyle);
            highlightCurrentWord();
        } catch (Exception e) {
            e.printStackTrace();
        }

        typingPane.requestFocusInWindow();
    }

    private int getWordStartOffset(String[] words, int index) {
        int offset = 0;
        for (int i = 0; i < index && i < words.length; i++) {
            offset += words[i].length() + 1; 
        }
        return offset;
    }
}
