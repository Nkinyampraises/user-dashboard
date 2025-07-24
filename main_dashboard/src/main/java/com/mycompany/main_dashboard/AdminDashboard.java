/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.main_dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class AdminDashboard extends JFrame {
    private Color skyBlue = new Color(135, 206, 235);
    private JPanel contentPanel;
    private Connection connection;

    public AdminDashboard() {
        initializeDatabase();
        initializeUI();
    }

    private void initializeDatabase() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/userdb", "root", "");
            System.out.println("Database connected successfully");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeUI() {
        setTitle("Admin Dashboard");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Panel
        JPanel topPanel = new JPanel();
        topPanel.setBackground(skyBlue);
        topPanel.setPreferredSize(new Dimension(getWidth(), 60));
        topPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("ADMIN DASHBOARD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(70, 130, 180));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        // Navigation Buttons (larger size)
        JButton homeBtn = createSidebarButton("HOME");
        JButton dataAnalysisBtn = createSidebarButton("DATA ANALYSIS");
        JButton statisticsBtn = createSidebarButton("STATISTICS");
        JButton metricsBtn = createSidebarButton("METRICS");  // New METRICS button
        JButton logoutBtn = createSidebarButton("LOGOUT");

        sidebar.add(homeBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));  // Reduced spacing
        sidebar.add(dataAnalysisBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(statisticsBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(metricsBtn);  // Added METRICS button
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(logoutBtn);

        // Content Panel
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);

        // Action Listeners
        homeBtn.addActionListener(e -> showHomePage());
        dataAnalysisBtn.addActionListener(e -> showDataAnalysis());
        statisticsBtn.addActionListener(e -> showStatistics());
        metricsBtn.addActionListener(e -> showMetrics());  // New listener for METRICS
        logoutBtn.addActionListener(e -> logout());

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        showHomePage(); // Show home page by default
        setVisible(true);
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60)); // Increased height
        button.setBackground(new Color(100, 180, 220));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 16)); // Larger font
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(120, 200, 240));
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(100, 180, 220));
            }
        });
        return button;
    }

    private void showHomePage() {
        contentPanel.removeAll();
        
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        try {
            // User Statistics Cards (smaller size)
            JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 10));
            statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120)); // Reduced height
            statsPanel.setBackground(Color.WHITE);

            // Total users card
            int totalUsers = getTotalUsers();
            statsPanel.add(createStatCard("TOTAL USERS", String.valueOf(totalUsers), Color.decode("#4CAF50")));
            
            // Active users card
            int activeUsers = getActiveUsers();
            statsPanel.add(createStatCard("ACTIVE USERS", String.valueOf(activeUsers), Color.decode("#2196F3")));
            
            // Inactive users card
            int inactiveUsers = getInactiveUsers();
            statsPanel.add(createStatCard("INACTIVE USERS", String.valueOf(inactiveUsers), Color.decode("#F44336")));
            
            container.add(statsPanel);
            container.add(Box.createRigidArea(new Dimension(0, 15)));

            // Gender Distribution Cards (smaller size)
            JPanel genderPanel = new JPanel(new GridLayout(1, 2, 10, 10));
            genderPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120)); // Reduced height
            genderPanel.setBorder(BorderFactory.createTitledBorder("GENDER DISTRIBUTION"));
            genderPanel.setBackground(Color.WHITE);
            
            int maleCount = getGenderCount("Male");
            int femaleCount = getGenderCount("Female");
            
            genderPanel.add(createStatCard("MALE USERS", String.valueOf(maleCount), Color.decode("#3F51B5")));
            genderPanel.add(createStatCard("FEMALE USERS", String.valueOf(femaleCount), Color.decode("#E91E63")));
            
            container.add(genderPanel);
            container.add(Box.createRigidArea(new Dimension(0, 15)));

            // Recent Users Table (reduced height)
            JPanel recentUsersPanel = new JPanel(new BorderLayout());
            recentUsersPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200)); // Reduced height
            recentUsersPanel.setBorder(BorderFactory.createTitledBorder("RECENT REGISTERED USERS (Last 5)"));
            recentUsersPanel.setBackground(Color.WHITE);
            
            DefaultTableModel model = new DefaultTableModel(
                new Object[]{"NAME", "EMAIL", "PHONE", "STATUS", "REG DATE", "GENDER"}, 0);
            
            // Show only last 5 users
            String query = "SELECT name, email, number, status, date, gender FROM users ORDER BY id DESC LIMIT 5";
            PreparedStatement pst = connection.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("number"),
                    rs.getString("status"),
                    rs.getString("date"),
                    rs.getString("gender")
                });
            }
            
            JTable table = new JTable(model);
            table.setRowHeight(30); // Compact row height
            table.setFont(new Font("Arial", Font.PLAIN, 12));
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            JScrollPane scrollPane = new JScrollPane(table);
            recentUsersPanel.add(scrollPane, BorderLayout.CENTER);
            
            container.add(recentUsersPanel);
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            ex.printStackTrace();
            
            // Fallback content
            JLabel errorLabel = new JLabel("Statistics unavailable. Database error.", SwingConstants.CENTER);
            errorLabel.setFont(new Font("Arial", Font.BOLD, 18));
            container.add(errorLabel);
        }
        
        contentPanel.add(new JScrollPane(container), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Smaller padding
        card.setPreferredSize(new Dimension(150, 100)); // Reduced size
        card.setMaximumSize(new Dimension(160, 100)); // Max size control

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Smaller font
        titleLabel.setForeground(Color.WHITE);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 22)); // Slightly smaller
        valueLabel.setForeground(Color.WHITE);

        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5))); // Reduced spacing
        card.add(valueLabel);

        return card;
    }
    
    private int getTotalUsers() throws SQLException {
        String query = "SELECT COUNT(*) FROM users";
        PreparedStatement pst = connection.prepareStatement(query);
        ResultSet rs = pst.executeQuery();
        return rs.next() ? rs.getInt(1) : 0;
    }
    
    private int getActiveUsers() throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE status = 'Active'";
        PreparedStatement pst = connection.prepareStatement(query);
        ResultSet rs = pst.executeQuery();
        return rs.next() ? rs.getInt(1) : 0;
    }
    
    private int getInactiveUsers() throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE status = 'Inactive'";
        PreparedStatement pst = connection.prepareStatement(query);
        ResultSet rs = pst.executeQuery();
        return rs.next() ? rs.getInt(1) : 0;
    }
    
    private int getGenderCount(String gender) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE gender = ?";
        PreparedStatement pst = connection.prepareStatement(query);
        pst.setString(1, gender);
        ResultSet rs = pst.executeQuery();
        return rs.next() ? rs.getInt(1) : 0;
    }

    private void showDataAnalysis() {
        contentPanel.removeAll();
        try {
            // Create and add the DataAnalysisPanel
            DataAnalysisPanel analysisPanel = new DataAnalysisPanel(connection);
            contentPanel.add(analysisPanel, BorderLayout.CENTER);
            System.out.println("Data Analysis panel loaded successfully");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data analysis: " + e.getMessage());
            e.printStackTrace();
            showHomePage(); // Fall back to home page on error
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showStatistics() {
        contentPanel.removeAll();
        try {
            // Create and add the StatisticsPanel
            StatisticsPanel statsPanel = new StatisticsPanel(connection);
            contentPanel.add(statsPanel, BorderLayout.CENTER);
            System.out.println("Statistics panel loaded successfully");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading statistics: " + e.getMessage());
            e.printStackTrace();
            showHomePage(); // Fall back to home page on error
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // New method to show MetricsPanel
    private void showMetrics() {
        contentPanel.removeAll();
        try {
            // Create and add the MetricsPanel
            MetricsPanel metricsPanel = new MetricsPanel(connection);
            contentPanel.add(metricsPanel, BorderLayout.CENTER);
            System.out.println("Metrics panel loaded successfully");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading metrics: " + e.getMessage());
            e.printStackTrace();
            showHomePage(); // Fall back to home page on error
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void logout() {
        this.dispose();
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        new Main_dashboard().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AdminDashboard();
        });
    }
}