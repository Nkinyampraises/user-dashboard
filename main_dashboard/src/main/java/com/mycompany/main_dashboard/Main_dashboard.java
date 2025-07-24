/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.main_dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.border.EmptyBorder;
import com.mycompany.main_dashboard.AdminDashboard;
import com.mycompany.main_dashboard.UsersProfile;

public class Main_dashboard extends JFrame {
    private JPanel contentPanel;
    private Color skyBlue = new Color(135, 206, 235);
    private Connection connection;
    private final int FIELD_WIDTH = 25; 

    public Main_dashboard() {
        initializeDatabase();
        setupUI();
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void initializeDatabase() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/userdb", "root", "");
            System.out.println("Database connection established successfully");
            
            Statement stmt = connection.createStatement();
            String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "email VARCHAR(100) NOT NULL UNIQUE, " +
                "password VARCHAR(100) NOT NULL, " +
                "number VARCHAR(20), " +
                "date VARCHAR(20), " +
                "status VARCHAR(20), " +
                "gender VARCHAR(10))";
            stmt.execute(createTableSQL);
            
            String createAdminTableSQL = "CREATE TABLE IF NOT EXISTS admins (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL UNIQUE, " +
                "password VARCHAR(100) NOT NULL)";
            stmt.execute(createAdminTableSQL);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupUI() {
        setTitle("Dashboard Navigation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top Panel
        JPanel topPanel = new JPanel();
        topPanel.setBackground(skyBlue);
        topPanel.setPreferredSize(new Dimension(getWidth(), 60));
        topPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("ADMIN ANALYTICS DASHBOARD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(70, 130, 180));
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        // Admin Section
        JLabel adminLabel = new JLabel("ADMINISTRATION");
        adminLabel.setForeground(Color.WHITE);
        adminLabel.setFont(new Font("Arial", Font.BOLD, 16));
        adminLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        sidebar.add(adminLabel);

        JButton adminLoginBtn = createSidebarButton("Admin Login");
        sidebar.add(adminLoginBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        // Users Section
        JLabel usersLabel = new JLabel("USERS");
        usersLabel.setForeground(Color.WHITE);
        usersLabel.setFont(new Font("Arial", Font.BOLD, 16));
        usersLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        sidebar.add(usersLabel);

        JButton userLoginBtn = createSidebarButton("Login");
        JButton registerBtn = createSidebarButton("Register");

        sidebar.add(userLoginBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(registerBtn);

        // Content Panel
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);

        // Action Listeners
        adminLoginBtn.addActionListener(e -> showAdminLogin());
        userLoginBtn.addActionListener(e -> showUserLogin());
        registerBtn.addActionListener(e -> showUserRegistration());

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Show Admin Login immediately instead of welcome message
        showAdminLogin();

        setVisible(true);
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setBackground(new Color(100, 180, 220));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
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

    private void showAdminLogin() {
        contentPanel.removeAll();
        
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20),
            BorderFactory.createLineBorder(skyBlue, 2)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        
        JLabel title = new JLabel("ADMIN LOGIN");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(skyBlue);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        formPanel.add(title, gbc);
        
        JTextField nameField = new JTextField(FIELD_WIDTH);
        JPasswordField passwordField = new JPasswordField(FIELD_WIDTH);
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        
        JCheckBox showPassword = new JCheckBox("Show Password");
        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });
        
        // Name row
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(nameField, gbc);
        
        // Password row
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(passwordField, gbc);
        
        // Show password row
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(showPassword, gbc);
        
        // Login button row
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(loginButton, gbc);
        
        loginButton.addActionListener(e -> {
            try {
                String query = "SELECT * FROM admins WHERE name=? AND password=?";
                PreparedStatement pst = connection.prepareStatement(query);
                pst.setString(1, nameField.getText());
                pst.setString(2, new String(passwordField.getPassword()));
                ResultSet rs = pst.executeQuery();
                
                if (rs.next()) {
                    this.dispose();
                    new AdminDashboard().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Credentials");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        });
        
        container.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(container, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showUserRegistration() {
        contentPanel.removeAll();
        
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20),
            BorderFactory.createLineBorder(skyBlue, 2)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        
        JLabel title = new JLabel("USER REGISTRATION");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(skyBlue);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        formPanel.add(title, gbc);
        
        // Form fields - all with consistent FIELD_WIDTH
        JTextField nameField = new JTextField(FIELD_WIDTH);
        JTextField emailField = new JTextField(FIELD_WIDTH);
        JPasswordField passwordField = new JPasswordField(FIELD_WIDTH);
        JPasswordField confirmPasswordField = new JPasswordField(FIELD_WIDTH);
        JTextField numberField = new JTextField(FIELD_WIDTH);
        JTextField dateField = new JTextField(FIELD_WIDTH);
        
        // Combo boxes with consistent width
        String[] statusOptions = {"Active", "Inactive"};
        JComboBox<String> statusCombo = new JComboBox<>(statusOptions);
        statusCombo.setPreferredSize(new Dimension(FIELD_WIDTH * 10, statusCombo.getPreferredSize().height));
        
        String[] genderOptions = {"Male", "Female"};
        JComboBox<String> genderCombo = new JComboBox<>(genderOptions);
        genderCombo.setPreferredSize(new Dimension(FIELD_WIDTH * 10, genderCombo.getPreferredSize().height));
        
        JCheckBox showPassword = new JCheckBox("Show Password");
        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
                confirmPasswordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
                confirmPasswordField.setEchoChar('•');
            }
        });
        
        // Add form fields with proper alignment
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        
        // Row 1: Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(nameField, gbc);
        
        // Row 2: Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(emailField, gbc);
        
        // Row 3: Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(passwordField, gbc);
        
        // Row 4: Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(confirmPasswordField, gbc);
        
        // Row 5: Show Password
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(showPassword, gbc);
        
        // Row 6: Phone Number
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(numberField, gbc);
        
        // Row 7: Date
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(dateField, gbc);
        
        // Row 8: Status
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(statusCombo, gbc);
        
        // Row 9: Gender
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(genderCombo, gbc);
        
        // Row 10: Submit Button
        JButton submitButton = new JButton("Submit");
        submitButton.setBackground(new Color(70, 130, 180));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(submitButton, gbc);
        
        submitButton.addActionListener(e -> {
            if (nameField.getText().isEmpty() || 
                emailField.getText().isEmpty() || passwordField.getPassword().length == 0 || 
                numberField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all required fields");
                return;
            }
            
            if (!new String(passwordField.getPassword()).equals(new String(confirmPasswordField.getPassword()))) {
                JOptionPane.showMessageDialog(this, "Passwords do not match");
                return;
            }
            
            try {
                String checkEmailQuery = "SELECT * FROM users WHERE email=?";
                PreparedStatement checkStmt = connection.prepareStatement(checkEmailQuery);
                checkStmt.setString(1, emailField.getText());
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Email already registered");
                    return;
                }
                
                String insertQuery = "INSERT INTO users (name, email, password, number, date, status, gender) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pst = connection.prepareStatement(insertQuery);
                pst.setString(1, nameField.getText());
                pst.setString(2, emailField.getText());
                pst.setString(3, new String(passwordField.getPassword()));
                pst.setString(4, numberField.getText());
                pst.setString(5, dateField.getText());
                pst.setString(6, (String) statusCombo.getSelectedItem());
                pst.setString(7, (String) genderCombo.getSelectedItem());
                
                int rowsAffected = pst.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Registration Successful");
                    showUserLogin();
                } else {
                    JOptionPane.showMessageDialog(this, "Registration Failed");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        
        container.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(container, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showUserLogin() {
        contentPanel.removeAll();
        
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20),
            BorderFactory.createLineBorder(skyBlue, 2)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        
        JLabel title = new JLabel("USER LOGIN");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(skyBlue);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        formPanel.add(title, gbc);
        
        JTextField emailField = new JTextField(FIELD_WIDTH);
        JPasswordField passwordField = new JPasswordField(FIELD_WIDTH);
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        
        JCheckBox showPassword = new JCheckBox("Show Password");
        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });
        
        // Add form fields with proper alignment
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        
        // Email row
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(emailField, gbc);
        
        // Password row
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(passwordField, gbc);
        
        // Show password row
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(showPassword, gbc);
        
        // Login button row
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(loginButton, gbc);
        
        loginButton.addActionListener(e -> {
            try {
                String query = "SELECT * FROM users WHERE email=? AND password=?";
                PreparedStatement pst = connection.prepareStatement(query);
                pst.setString(1, emailField.getText());
                pst.setString(2, new String(passwordField.getPassword()));
                ResultSet rs = pst.executeQuery();
                
                if (rs.next()) {
                    this.dispose();
                    int userId = rs.getInt("id");
                    new UsersProfile(rs, connection, userId).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Credentials");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        });
        
        container.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(container, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main_dashboard());
    }
}