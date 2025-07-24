/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.main_dashboard;


import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class UsersProfile extends JFrame {
    private Color skyBlue = new Color(135, 206, 235);
    private JTextField nameField, emailField, phoneField, dateField;
    private JComboBox<String> genderComboBox, statusComboBox;
    private Connection connection;
    private int userId;

    public UsersProfile(ResultSet userData, Connection connection, int userId) {
        this.connection = connection;
        this.userId = userId;
        initializeUI(userData);
    }

    private void initializeUI(ResultSet userData) {
        setTitle("User Profile");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            String name = safeGetString(userData, "name");
            String email = safeGetString(userData, "email");
            String phone = safeGetString(userData, "number", "Not specified");
            String regDate = safeGetString(userData, "date", "Not specified");
            String status = safeGetString(userData, "status", "Active");
            String gender = safeGetString(userData, "gender", "Not specified");

            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(Color.WHITE);

            // Header Panel
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(skyBlue);
            headerPanel.setPreferredSize(new Dimension(getWidth(), 80));

            // User Profile label on left
            JLabel profileTitle = new JLabel("User Profile");
            profileTitle.setFont(new Font("Arial", Font.BOLD, 24)); // Bigger font
            profileTitle.setForeground(Color.WHITE);
            profileTitle.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding
            headerPanel.add(profileTitle, BorderLayout.WEST);

            // Welcome label centered
            JLabel welcomeLabel = new JLabel("Welcome: " + name, SwingConstants.CENTER);
            welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 20)); // Larger font
            welcomeLabel.setForeground(Color.WHITE);
            welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Vertical padding
            headerPanel.add(welcomeLabel, BorderLayout.CENTER);

            // Buttons on top right
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            buttonPanel.setOpaque(false);

            JButton editButton = new JButton("Edit Profile");
            editButton.setPreferredSize(new Dimension(120, 30));
            editButton.setFont(new Font("Arial", Font.BOLD, 12));
            editButton.setBackground(new Color(70, 130, 180));
            editButton.setForeground(Color.WHITE);
            editButton.setFocusPainted(false);
            editButton.addActionListener(e -> showEditProfileDialog(name, email, phone, regDate, status, gender));

            JButton logoutButton = new JButton("Logout");
            logoutButton.setPreferredSize(new Dimension(100, 30));
            logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
            logoutButton.setBackground(new Color(70, 130, 180));
            logoutButton.setForeground(Color.WHITE);
            logoutButton.setFocusPainted(false);
            logoutButton.addActionListener(e -> {
                new Main_dashboard().setVisible(true);
                dispose();
            });

            buttonPanel.add(editButton);
            buttonPanel.add(logoutButton);
            headerPanel.add(buttonPanel, BorderLayout.EAST);

            mainPanel.add(headerPanel, BorderLayout.NORTH);

            // Content Panel
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            contentPanel.setBackground(Color.WHITE);

            String[] columnNames = {"Field", "Value"};
            Object[][] data = {
                {"Name:", name},
                {"Email:", email},
                {"Registration Date:", regDate},
                {"Status:", status},
                {"Phone Number:", phone},
                {"Gender:", gender}
            };

            DefaultTableModel model = new DefaultTableModel(data, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            JTable infoTable = new JTable(model);
            infoTable.setRowHeight(30);
            infoTable.setFont(new Font("Arial", Font.PLAIN, 14));
            infoTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
            infoTable.setShowGrid(false);
            infoTable.setIntercellSpacing(new Dimension(0, 0));

            JScrollPane tableScrollPane = new JScrollPane(infoTable);
            tableScrollPane.setBorder(BorderFactory.createEmptyBorder());

            contentPanel.add(tableScrollPane, BorderLayout.CENTER);
            mainPanel.add(contentPanel, BorderLayout.CENTER);

            add(mainPanel);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading user data: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void showEditProfileDialog(String name, String email, String phone,
                                       String regDate, String status, String gender) {
        JDialog editDialog = new JDialog(this, "Update Your Profile", true);
        editDialog.setSize(400, 500);
        editDialog.setLocationRelativeTo(this);

        JPanel editPanel = new JPanel();
        editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
        editPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Form fields
        editPanel.add(createFieldLabel("Name:"));
        nameField = new JTextField(name);
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        editPanel.add(nameField);
        editPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        editPanel.add(createFieldLabel("Email:"));
        emailField = new JTextField(email);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        editPanel.add(emailField);
        editPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        editPanel.add(createFieldLabel("Phone Number:"));
        phoneField = new JTextField(phone);
        phoneField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        editPanel.add(phoneField);
        editPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        editPanel.add(createFieldLabel("Registration Date:"));
        dateField = new JTextField(regDate);
        dateField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        editPanel.add(dateField);
        editPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        editPanel.add(createFieldLabel("Status:"));
        String[] statuses = {"Active", "Inactive", "Suspended"};
        statusComboBox = new JComboBox<>(statuses);
        statusComboBox.setSelectedItem(status);
        statusComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        editPanel.add(statusComboBox);
        editPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        editPanel.add(createFieldLabel("Gender:"));
        String[] genders = {"Male", "Female", "Other"};
        genderComboBox = new JComboBox<>(genders);
        genderComboBox.setSelectedItem(gender);
        genderComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        editPanel.add(genderComboBox);
        editPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton updateButton = new JButton("Update");
        updateButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        updateButton.setPreferredSize(new Dimension(100, 40));
        updateButton.setFont(new Font("Arial", Font.BOLD, 14));
        updateButton.setBackground(new Color(70, 130, 180));
        updateButton.setForeground(Color.WHITE);
        updateButton.setFocusPainted(false);

        updateButton.addActionListener(e -> {
            try {
                String updatedName = nameField.getText();
                String updatedEmail = emailField.getText();
                String updatedPhone = phoneField.getText();
                String updatedDate = dateField.getText();
                String updatedStatus = (String) statusComboBox.getSelectedItem();
                String updatedGender = (String) genderComboBox.getSelectedItem();

                String sql = "UPDATE users SET name = ?, email = ?, number = ?, " +
                        "date = ?, status = ?, gender = ? WHERE id = ?";

                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, updatedName);
                pstmt.setString(2, updatedEmail);
                pstmt.setString(3, updatedPhone);
                pstmt.setString(4, updatedDate);
                pstmt.setString(5, updatedStatus);
                pstmt.setString(6, updatedGender);
                pstmt.setInt(7, userId);

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(editDialog, "Profile updated successfully!");
                    editDialog.dispose();
                    dispose();
                    ResultSet rs = connection.createStatement().executeQuery(
                            "SELECT * FROM users WHERE id = " + userId);
                    if (rs.next()) {
                        new UsersProfile(rs, connection, userId).setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(editDialog, "Failed to update profile",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

                pstmt.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(editDialog, "Database error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        editPanel.add(updateButton);
        editDialog.add(editPanel);
        editDialog.setVisible(true);
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private String safeGetString(ResultSet rs, String columnName) throws SQLException {
        try {
            return rs.getString(columnName);
        } catch (SQLException e) {
            return "";
        }
    }

    private String safeGetString(ResultSet rs, String columnName, String defaultValue) throws SQLException {
        try {
            String value = rs.getString(columnName);
            return value != null ? value : defaultValue;
        } catch (SQLException e) {
            return defaultValue;
        }
    }
}
