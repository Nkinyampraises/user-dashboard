/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.main_dashboard;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MetricsPanel extends JPanel {
    private Connection connection;

    public MetricsPanel(Connection connection) {
        this.connection = connection;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        try {
            add(createStatsPanel("Daily Registrations", getDailyRegistrations()));
            add(Box.createRigidArea(new Dimension(0, 20)));
            add(createStatsPanel("Weekly Growth", getWeeklyRegistrations()));
            add(Box.createRigidArea(new Dimension(0, 20)));
            add(createStatsPanel("Registrations by Day of Week", getRegistrationsByDayOfWeek())); // Replaced login frequency
        } catch (SQLException e) {
            e.printStackTrace();
            add(new JLabel("Error loading metrics: " + e.getMessage()));
        }
    }

    private JPanel createStatsPanel(String title, List<Integer> values) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (int val : values) {
            stats.addValue(val);
        }

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setBackground(Color.WHITE);

        panel.add(new JLabel("Mean: " + String.format("%.2f", stats.getMean())));
        panel.add(new JLabel("Standard Deviation: " + String.format("%.2f", stats.getStandardDeviation())));
        panel.add(new JLabel("Variance: " + String.format("%.2f", stats.getVariance())));
        panel.add(new JLabel("Sample Size: " + values.size()));

        return panel;
    }

    private List<Integer> getDailyRegistrations() throws SQLException {
        List<Integer> counts = new ArrayList<>();
        String query = "SELECT DATE(date) as reg_date, COUNT(*) as count FROM users GROUP BY DATE(date)";
        PreparedStatement pst = connection.prepareStatement(query);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            counts.add(rs.getInt("count"));
        }

        return counts;
    }

    private List<Integer> getWeeklyRegistrations() throws SQLException {
        List<Integer> counts = new ArrayList<>();
        String query = "SELECT YEARWEEK(date, 1) as week, COUNT(*) as count FROM users GROUP BY week";
        PreparedStatement pst = connection.prepareStatement(query);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            counts.add(rs.getInt("count"));
        }

        return counts;
    }

    // New method replacing login frequency
    private List<Integer> getRegistrationsByDayOfWeek() throws SQLException {
        List<Integer> counts = new ArrayList<>();
        String query = "SELECT DAYOFWEEK(date) as day, COUNT(*) as count FROM users GROUP BY DAYOFWEEK(date)";
        PreparedStatement pst = connection.prepareStatement(query);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            counts.add(rs.getInt("count"));
        }

        return counts;
    }
} 