/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.main_dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class DataAnalysisPanel extends JPanel {
    private Connection connection;
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JComboBox<String> filterComboBox;

    public DataAnalysisPanel(Connection connection) {
        this.connection = connection;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Create main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);
        
        // Create compact control panel for search and filter
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createTitledBorder("User Management"));
        
        // Search field
        searchField = new JTextField(15);
        searchField.setToolTipText("Search users");
        controlPanel.add(new JLabel("Search:"));
        controlPanel.add(searchField);
        
        // Filter combo box
        controlPanel.add(new JLabel("Filter:"));
        String[] filterOptions = {"All", "Active", "Inactive", "Male", "Female"};
        filterComboBox = new JComboBox<>(filterOptions);
        filterComboBox.setPreferredSize(new Dimension(100, 25));
        controlPanel.add(filterComboBox);
        
        // Buttons
        JButton deleteButton = createActionButton("Delete");
        JButton updateButton = createActionButton("Update");
        JButton clearButton = createActionButton("Clear");
        
        controlPanel.add(deleteButton);
        controlPanel.add(updateButton);
        controlPanel.add(clearButton);
        
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        
        // Create table model with ID column
        model = new DefaultTableModel(
            new Object[]{"ID", "NAME", "EMAIL", "PHONE", "STATUS", "REG DATE", "GENDER"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        // Create table with model
        table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Load data from database
        loadData();
        
        // Add listeners
        addListeners(deleteButton, updateButton, clearButton);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(80, 25));
        return button;
    }

    private void loadData() {
        try {
            // Clear existing data
            model.setRowCount(0);
            
            // Query to get all users
            String query = "SELECT id, name, email, number, status, date, gender FROM users";
            PreparedStatement pst = connection.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            // Populate table model
            int rowNum = 1; // Start ID from 1
            while (rs.next()) {
                model.addRow(new Object[]{
                    rowNum++, // Auto-generated ID (visible)
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("number"),
                    rs.getString("status"),
                    rs.getString("date"),
                    rs.getString("gender")
                });
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void addListeners(JButton deleteButton, JButton updateButton, JButton clearButton) {
        // Search field listener
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }
        });
        
        // Filter combobox listener
        filterComboBox.addActionListener(e -> filterTable());
        
        // Clear button - now fully functional
        clearButton.addActionListener(e -> {
            searchField.setText("");
            filterComboBox.setSelectedIndex(0);
            filterTable();
        });
        
        // Delete button
        deleteButton.addActionListener(e -> deleteSelectedUser());
        
        // Update button
        updateButton.addActionListener(e -> updateSelectedUser());
    }
    
    private void filterTable() {
        String searchText = searchField.getText().toLowerCase();
        String filter = (String) filterComboBox.getSelectedItem();
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        RowFilter<DefaultTableModel, Object> rowFilter = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                // Skip ID column (index 0)
                boolean matchesSearch = false;
                for (int i = 1; i < entry.getValueCount(); i++) {
                    String value = entry.getStringValue(i).toLowerCase();
                    if (value.contains(searchText)) {
                        matchesSearch = true;
                        break;
                    }
                }
                
                if (!matchesSearch) return false;
                
                if ("All".equals(filter)) return true;
                
                // Check status and gender columns
                String status = entry.getStringValue(4); // Status column
                String gender = entry.getStringValue(6); // Gender column
                
                switch (filter) {
                    case "Active": return "Active".equals(status);
                    case "Inactive": return "Inactive".equals(status);
                    case "Male": return "Male".equals(gender);
                    case "Female": return "Female".equals(gender);
                    default: return true;
                }
            }
        };
        
        sorter.setRowFilter(rowFilter);
    }
    
    private void deleteSelectedUser() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete");
            return;
        }
        
        int modelRow = table.convertRowIndexToModel(viewRow);
        String email = (String) model.getValueAt(modelRow, 2); // Email is in column 2
        
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Delete user with email: " + email + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String query = "DELETE FROM users WHERE email = ?";
                PreparedStatement pst = connection.prepareStatement(query);
                pst.setString(1, email);
                int rowsAffected = pst.executeUpdate();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "User deleted successfully");
                    loadData(); // Refresh table
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete user");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
    
    private void updateSelectedUser() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to update");
            return;
        }
        
        int modelRow = table.convertRowIndexToModel(viewRow);
        String email = (String) model.getValueAt(modelRow, 2); // Email is in column 2
        
        try {
            String query = "SELECT * FROM users WHERE email = ?";
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                showUpdateDialog(rs);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void showUpdateDialog(ResultSet userData) {
        try {
            String name = userData.getString("name");
            String email = userData.getString("email");
            String phone = userData.getString("number");
            String date = userData.getString("date");
            String status = userData.getString("status");
            String gender = userData.getString("gender");
            
            JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Update User", true);
            dialog.setSize(400, 400);
            dialog.setLocationRelativeTo(this);
            
            JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            // Name
            panel.add(new JLabel("Name:"));
            JTextField nameField = new JTextField(name);
            panel.add(nameField);
            
            // Email (read-only)
            panel.add(new JLabel("Email:"));
            JTextField emailField = new JTextField(email);
            emailField.setEditable(false);
            panel.add(emailField);
            
            // Phone
            panel.add(new JLabel("Phone:"));
            JTextField phoneField = new JTextField(phone);
            panel.add(phoneField);
            
            // Date
            panel.add(new JLabel("Reg Date:"));
            JTextField dateField = new JTextField(date);
            panel.add(dateField);
            
            // Status
            panel.add(new JLabel("Status:"));
            JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Active", "Inactive"});
            statusCombo.setSelectedItem(status);
            panel.add(statusCombo);
            
            // Gender
            panel.add(new JLabel("Gender:"));
            JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
            genderCombo.setSelectedItem(gender);
            panel.add(genderCombo);
            
            // Update button
            JButton updateButton = new JButton("Update");
            updateButton.addActionListener(e -> {
                try {
                    String query = "UPDATE users SET name = ?, number = ?, date = ?, status = ?, gender = ? WHERE email = ?";
                    PreparedStatement pst = connection.prepareStatement(query);
                    pst.setString(1, nameField.getText());
                    pst.setString(2, phoneField.getText());
                    pst.setString(3, dateField.getText());
                    pst.setString(4, (String) statusCombo.getSelectedItem());
                    pst.setString(5, (String) genderCombo.getSelectedItem());
                    pst.setString(6, email);
                    
                    int rowsAffected = pst.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(dialog, "User updated successfully");
                        dialog.dispose();
                        loadData(); // Refresh table
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Failed to update user");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Database error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });
            
            panel.add(new JLabel()); // Spacer
            panel.add(updateButton);
            
            dialog.add(panel);
            dialog.setVisible(true);
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading user data: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}