package com.craftinginterpreters.testui;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.TimerTask;
import javax.swing.plaf.ColorUIResource;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

public class Testui extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;

    public Testui() {
        setTitle("Type Rider");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        Image type_rider = new ImageIcon("C:\\Users\\mhtam\\OneDrive\\Documents\\NetBeansProjects\\testui\\src\\main\\java\\logo.png").getImage();

        //Image type_rider = new ImageIcon(this.getClass().getResource("C:\\Users\\mhtam\\OneDrive\\Documents\\NetBeansProjects\\testui\\src\\main\\java\\logo.png")).getImage();
        setIconImage(type_rider);
        

        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            
        }
        GlobalScreen.getInstance().addNativeKeyListener(new overlay());
       
        
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(25, 25, 25));
        sidebar.setPreferredSize(new Dimension(80, getHeight()));
        
        ImageIcon home = new ImageIcon("C:\\Users\\mhtam\\OneDrive\\Documents\\NetBeansProjects\\testui\\src\\main\\java\\home.png");
        ImageIcon aboutus = new ImageIcon("C:\\Users\\mhtam\\OneDrive\\Documents\\NetBeansProjects\\testui\\src\\main\\java\\aboutus.png");
        ImageIcon typing = new ImageIcon("C:\\Users\\mhtam\\OneDrive\\Documents\\NetBeansProjects\\testui\\src\\main\\java\\typing.png");

        JButton dashboardBtn = createSidebarButton("Dashboard" , home);
        JButton practiceBtn = createSidebarButton("Practice" , typing);
        JButton aboutusBtn = createSidebarButton("About Us" , aboutus);

        sidebar.add(Box.createVerticalStrut(30)); 
        sidebar.add(dashboardBtn);
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(practiceBtn);
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(aboutusBtn);
        sidebar.add(Box.createVerticalGlue());

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(createDashboardPanel(), "Dashboard");
        contentPanel.add(new PracticePanel(), "Practice");
        contentPanel.add(createAboutUsPanel(), "About Us");

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        dashboardBtn.addActionListener(e -> cardLayout.show(contentPanel, "Dashboard"));
        practiceBtn.addActionListener(e -> cardLayout.show(contentPanel, "Practice"));
        aboutusBtn.addActionListener(e -> cardLayout.show(contentPanel, "About Us"));

        setVisible(true);
    }

   private JButton createSidebarButton(String tooltip, ImageIcon icon) {
    Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
    ImageIcon scaledIcon = new ImageIcon(img);

    JButton button = new JButton(scaledIcon);
    button.setToolTipText(tooltip);

    button.setBackground(new Color(30, 30, 30));
    button.setForeground(Color.WHITE);
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setContentAreaFilled(false); 
    button.setAlignmentX(Component.CENTER_ALIGNMENT);
    button.setMaximumSize(new Dimension(60, 60));

    button.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            button.setContentAreaFilled(true);
            button.setBackground(new Color(60, 60, 60));
        }

        @Override
        public void mouseExited(java.awt.event.MouseEvent evt) {
            button.setContentAreaFilled(false);
        }
    });

    return button;
}



    private JPanel createDashboardPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(Color.BLACK);

    
    JPanel upbar = new JPanel();
        upbar.setLayout(new BoxLayout(upbar, BoxLayout.X_AXIS));
        upbar.setBackground(new Color(0, 0, 0));
        upbar.setPreferredSize(new Dimension(getWidth() , 80));
        
        ImageIcon icon = new ImageIcon("C:\\Users\\mhtam\\OneDrive\\Documents\\NetBeansProjects\\testui\\src\\main\\java\\logo.png");

        JButton dashboardBtn = createSidebarButton("Dashboard" , icon);
        
        JLabel name = new JLabel("Type Rider", SwingConstants.CENTER);
        name.setForeground(Color.WHITE);
        name.setFont(new Font("Arial", Font.BOLD, 26));
        
        upbar.add(Box.createHorizontalStrut(430)); 
        upbar.add(dashboardBtn);
        upbar.add(name);
        
    JPanel metersPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 40));
    metersPanel.setOpaque(false);

    JPanel meterWrapper1 = new JPanel();
    meterWrapper1.setLayout(new BoxLayout(meterWrapper1, BoxLayout.Y_AXIS));
    meterWrapper1.setOpaque(false);

    meters meter1 = new meters();
    meter1.setPreferredSize(new Dimension(300, 220));
    meterWrapper1.add(meter1);

    JLabel wpmLabel = new JLabel("                       WPM : "+overlay.WPM, SwingConstants.CENTER);
    wpmLabel.setForeground(Color.WHITE);
    wpmLabel.setFont(new Font("Arial", Font.BOLD, 18));
    meterWrapper1.add(Box.createVerticalStrut(10));
    meterWrapper1.add(wpmLabel);

    JPanel meterWrapper2 = new JPanel();
    meterWrapper2.setLayout(new BoxLayout(meterWrapper2, BoxLayout.Y_AXIS));
    meterWrapper2.setOpaque(false);

    meters meter2 = new meters();
    meter2.setPreferredSize(new Dimension(300, 220));
    meterWrapper2.add(meter2);

    JLabel accLabel = new JLabel("                          Accuracy : 95%", SwingConstants.CENTER);
    accLabel.setForeground(Color.WHITE);
    accLabel.setFont(new Font("Arial", Font.BOLD, 18));
    meterWrapper2.add(Box.createVerticalStrut(10));
    meterWrapper2.add(accLabel);

    metersPanel.add(meterWrapper1);
    metersPanel.add(meterWrapper2);

    JLabel totalKeysLabel = new JLabel("Total number of keys pressed: "+overlay.total_key_pressed, SwingConstants.CENTER);
    totalKeysLabel.setFont(new Font("Arial", Font.BOLD, 22));
    totalKeysLabel.setForeground(Color.WHITE);
    totalKeysLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 40, 0));

    panel.add(metersPanel, BorderLayout.CENTER);
    panel.add(totalKeysLabel, BorderLayout.SOUTH);
    panel.add(upbar,BorderLayout.NORTH);
    
    java.util.Timer timer = new java.util.Timer(false);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                meter1.setValue(overlay.WPM);
                meter2.setValue(95);       
                wpmLabel.setText("                       WPM : "+overlay.WPM);
                totalKeysLabel.setText("Total number of keys pressed: \n\n\n\n\n"+overlay.total_key_pressed);
            }
        }, 5000, 5000);

    

    return panel;
}

    private JPanel createAboutUsPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(Color.BLACK);

    JLabel titleLabel = new JLabel("About Us", SwingConstants.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
    titleLabel.setForeground(Color.WHITE);
    titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
    panel.add(titleLabel, BorderLayout.NORTH);

    JTextArea contentArea = new JTextArea();
    contentArea.setText(
        "                                                                                                           Type Rider v1.0\n\n" +
        " Developed by:\n\n"+
        "                  Mehedi Hassan Tamim\n"+
        "                               Faripur Engineering College\n"+
        "                               Department of Computer Science and Engineering\n"+
        "                               Reg: 913\n" +
        "                               Email: meheditamim7698@gmail.com\n\n\n"+
        "                   Ashik Muhammad\n"+
        "                               Faripur Engineering College\n"+
        "                               Department of Computer Science and Engineering\n"+
        "                               Reg: 952\n" +
        "                               Email: ashik47091@gmail.com\n\n\n"+
        "                   Abubakar Howlader\n"+
        "                               Faripur Engineering College\n"+
        "                               Department of Computer Science and Engineering\n"+
        "                               Reg: 946\n" +
        "                               Email: abubakar01402664@gmail.com\n\n\n"
    );
    contentArea.setFont(new Font("Arial", Font.PLAIN, 16));
    contentArea.setForeground(Color.WHITE);
    contentArea.setBackground(new Color(25, 25, 25));
    contentArea.setEditable(false);
    contentArea.setLineWrap(true);
    contentArea.setWrapStyleWord(true);

    JScrollPane scrollPane = new JScrollPane(contentArea);
    scrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
    panel.add(scrollPane, BorderLayout.CENTER);

    return panel;
}



    private static ServerSocket lockSocket;

    public static void main(String[] args) {
        if (!acquireLock()) {
            JOptionPane.showMessageDialog(null, 
                "Type Rider is already running in tray!",
                "Already Running", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }

        SwingUtilities.invokeLater(() -> {
            new Testui(); 
        });
    }

    private static boolean acquireLock() {
        try {
            lockSocket = new ServerSocket(55667); 
            return true;
        } catch (IOException e) {
            return false; 
        }
    }
}
