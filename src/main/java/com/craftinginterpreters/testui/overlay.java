package com.craftinginterpreters.testui;


import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.TimerTask;



public class overlay extends JFrame implements NativeKeyListener{
    public static int total_key_pressed = 0;
    int count=0;

    static public int WPM;
    private final String OVERLAY_TEXT = "WPM : ";
    private Shape currentOverlayShape;

    public overlay() {
        setTitle("Overlay App");
        setUndecorated(true);
        setAlwaysOnTop(true);

        setType(Type.UTILITY);
        setFocusableWindowState(false);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setBackground(new Color(0, 0, 0, 0));

        JPanel contentPane = createOverlayPanel();
        this.setContentPane(contentPane);

        setSize(120, 30);
        positionTopRight();
        //setupSystemTray();
        tray.setupTray(this);
        real_time_wpm_cal();
        
        setVisible(true);
        
    }

    private JPanel createOverlayPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setFont(new Font("Monospaced", Font.BOLD, 12));
                FontMetrics fm = g2d.getFontMetrics();
                int padding = 5;
                int textWidth = fm.stringWidth(OVERLAY_TEXT+WPM);
                int textHeight = fm.getHeight();
                int boxX = 0;
                int boxY = 0;
                int boxWidth = textWidth + 2 * padding;
                int boxHeight = textHeight + padding;
                currentOverlayShape = new RoundRectangle2D.Double(boxX, boxY, boxWidth, boxHeight, 5, 5);
                g2d.setColor(new Color(30, 30, 30, 180));
                g2d.fill(currentOverlayShape);
                g2d.setColor(Color.WHITE);
                String text = "WPM: " + WPM;
                g2d.drawString(text, padding, padding + fm.getAscent() - fm.getLeading());

                g2d.dispose();
                applyClickThroughShape();
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    private void applyClickThroughShape() {
        if (currentOverlayShape != null && isVisible()) {
            setShape(currentOverlayShape);
        }
    }

    private void positionTopRight() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) screenSize.getWidth() - getWidth() - 8;
        int y = 20;
        setLocation(x, y);
    }

    private void real_time_wpm_cal() {
              
        java.util.Timer timer = new java.util.Timer(false);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                float count2 = count ;
                count2 = (count2/25)*60;
                WPM = (int) count2;
                repaint();
                count = 0;
            }
        }, 5000, 5000);

    }
/*private void setupSystemTray() {
    if (!SystemTray.isSupported()) return;

    final SystemTray tray = SystemTray.getSystemTray();
    Image image = ((ImageIcon) UIManager.getIcon("OptionPane.informationIcon")).getImage();
    final TrayIcon trayIcon = new TrayIcon(image, "Typing Companion Overlay");
    trayIcon.setImageAutoSize(true);

    PopupMenu popup = new PopupMenu();

    MenuItem toggleItem = new MenuItem("Show/Hide Overlay");
    toggleItem.addActionListener(e -> toggle());
    popup.add(toggleItem);
    
    MenuItem exitItem = new MenuItem("Exit App");
    exitItem.addActionListener(e -> {
        tray.remove(trayIcon);
        System.exit(0);
    });
    popup.add(exitItem);

    trayIcon.setPopupMenu(popup);

    try {
        tray.add(trayIcon);
    } catch (AWTException e) {
        e.printStackTrace();
    }
}*/



    public void turnOn() {
        if (!isVisible()) {
            setVisible(true);
            repaint();
        }
    }

    public void turnOff() {
        if (isVisible()) {
            setVisible(false);
        }
    }

    public void toggle() {
        if (isVisible()) {
            turnOff();
        } else {
            turnOn();
        }
    }

    public void setOverlayText(String text) {
        SwingUtilities.invokeLater(() -> {
            try {
                getContentPane().repaint();
            } catch (Exception ignored) {}
        });
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        String key = NativeKeyEvent.getKeyText(e.getKeyCode());
    if (key.equals("Backspace") && count>0) {
        count = count - 1 ; 
    }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nke) {
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
      total_key_pressed = total_key_pressed +1 ; 
      char ch = e.getKeyChar();
      if (ch >= 32 && ch != 127) { 
        count= count+1;
      }
    }
}
