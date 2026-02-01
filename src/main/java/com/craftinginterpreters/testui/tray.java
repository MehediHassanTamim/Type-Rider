
package com.craftinginterpreters.testui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class tray {
    private static TrayIcon trayIcon;
    private static Testui mainAppInstance;

    public static void setupTray(overlay overlay_Ins) {
        if (!SystemTray.isSupported()) {
            return;
        }

        SystemTray tray = SystemTray.getSystemTray();

        for (TrayIcon icon : tray.getTrayIcons()) {
        if ("Type Rider".equals(icon.getToolTip())) {
            return;
           }
        }
        PopupMenu popup = new PopupMenu();

        MenuItem toggleOverlayItem = new MenuItem("On/Off Overlay");
        MenuItem exitTrayItem = new MenuItem("Exit");

        popup.add(toggleOverlayItem);
        popup.add(exitTrayItem);

        Image icon = Toolkit.getDefaultToolkit().getImage("C:\\Users\\mhtam\\OneDrive\\Documents\\NetBeansProjects\\testui\\src\\main\\java\\logo.png");
        trayIcon = new TrayIcon(icon, "Type Rider", popup);
        trayIcon.setImageAutoSize(true);

        try {
            SystemTray.getSystemTray().add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
            return;
        }

        trayIcon.addMouseListener(new MouseAdapter() {
            private long lastClickTime = 0;

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastClickTime < 400) {
                        openMainApp();
                    }
                    lastClickTime = currentTime;
                }
            }
        });

        toggleOverlayItem.addActionListener(e -> overlay_Ins.toggle());
        
        exitTrayItem.addActionListener(e -> {System.exit(0);});

    }

     static void openMainApp() {
        if (mainAppInstance == null || !mainAppInstance.isVisible()) {
            SwingUtilities.invokeLater(() -> {
                mainAppInstance = new Testui();
                mainAppInstance.setVisible(true);
            });
        } else {
            mainAppInstance.toFront();
        }
    }
}
