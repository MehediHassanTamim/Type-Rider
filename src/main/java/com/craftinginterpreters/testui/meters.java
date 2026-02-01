
package com.craftinginterpreters.testui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import javax.swing.Timer;


public class meters extends JPanel {
    
  
    private int targetValue = 0;    // value we want to reach
    private int displayedValue = 0; // value currently displayed (for animation)
    private int maxRange = 50;

    public void setValue(int value) {
        targetValue = value;

        // Determine maxRange dynamically
        if (targetValue <= 50) {
            maxRange = 50;
        } else if (targetValue <= 100) {
            maxRange = 100;
        } else if (targetValue <= 200) {
            maxRange = 200;
        } else {
            maxRange = targetValue;
        }

        // Timer for smooth animation
        new Timer(10, e -> {
            if (displayedValue < targetValue) {
                displayedValue++;
                repaint();
            } else if (displayedValue > targetValue) {
                displayedValue--;
                repaint();
            } else {
                ((Timer) e.getSource()).stop();
            }
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2 - 20;
        int centerX = width / 2;
        int centerY = height - 20;

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        
        //Draw outer semi-circle background
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillArc(centerX - radius, centerY - radius, 2 * radius, 2 * radius, 0, 180);

        // Fill the outer semi-circle proportionally (animated)
        g2.setColor(Color.BLUE);
        int fillAngle = (int) ((double) displayedValue / maxRange * 180);
        g2.fillArc(centerX - radius, centerY - radius, 2 * radius, 2 * radius, 180 - fillAngle, fillAngle);

        // Draw inner semi-circle background
        g2.setColor(Color.BLACK);
        g2.fillArc(centerX - radius + 10, centerY - radius + 10, 2 * (radius - 10), 2 * (radius - 10), 0, 180);

        // Draw only the lowest and highest numbers
        g2.setColor(Color.WHITE);
        g2.drawString("0", centerX - radius + 15, centerY);
        g2.drawString(String.valueOf(maxRange), centerX + radius - 40, centerY);

    }
    
}