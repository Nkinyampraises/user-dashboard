package com.mycompany.main_dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class StatisticsPanel extends JPanel {
    private Connection connection;

    public StatisticsPanel(Connection connection) {
        this.connection = connection;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        
        try {
            // Simplified Pie Chart Panel
            JPanel piePanel = new JPanel(new BorderLayout());
            piePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            piePanel.setBackground(Color.WHITE);
            
            // Simplified Status Pie Chart (only Active/Inactive)
            Map<String, Integer> pieData = new LinkedHashMap<>();
            pieData.put("Active Users", getActiveUsers());
            pieData.put("Inactive Users", getInactiveUsers());
            
            PieChartPanel pieChart = new PieChartPanel(pieData, "User Status Distribution");
            piePanel.add(pieChart, BorderLayout.CENTER);
            
            tabbedPane.addTab("Pie Chart", piePanel);
            
            // Bar Chart Panel
            Map<String, Integer> barData = getBarChartData();
            BarChartPanel barChart = new BarChartPanel(barData, "User Statistics");
            tabbedPane.addTab("Bar Chart", barChart);
            
            // Line Chart Panel
            Map<String, Integer> monthlyData = getMonthlyRegistrations();
            LineChartPanel lineChart = new LineChartPanel(monthlyData, "Monthly User Registrations");
            tabbedPane.addTab("Line Chart", lineChart);
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            ex.printStackTrace();
            
            // Fallback content
            JLabel errorLabel = new JLabel("Charts unavailable. Database error.", SwingConstants.CENTER);
            errorLabel.setFont(new Font("Arial", Font.BOLD, 18));
            add(errorLabel, BorderLayout.CENTER);
        }
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private Map<String, Integer> getBarChartData() throws SQLException {
        Map<String, Integer> data = new LinkedHashMap<>();
        data.put("Active", getActiveUsers());
        data.put("Inactive", getInactiveUsers());
        data.put("Male", getGenderCount("Male"));
        data.put("Female", getGenderCount("Female"));
        data.put("Logins", getLoginCount());
        return data;
    }
    
    private Map<String, Integer> getMonthlyRegistrations() throws SQLException {
        // Maintain insertion order (January to December)
        Map<String, Integer> monthlyData = new LinkedHashMap<>();
        String[] months = {"January", "February", "March", "April", "May", "June", 
                          "July", "August", "September", "October", "November", "December"};
        
        // Initialize all months to 0
        for (String month : months) {
            monthlyData.put(month, 0);
        }
        
        // Get actual data from database
        String query = "SELECT MONTH(STR_TO_DATE(date, '%Y-%m-%d')) as month, COUNT(*) as count " +
                       "FROM users GROUP BY MONTH(STR_TO_DATE(date, '%Y-%m-%d'))";
        PreparedStatement pst = connection.prepareStatement(query);
        ResultSet rs = pst.executeQuery();
        
        while (rs.next()) {
            int monthNum = rs.getInt("month");
            if (monthNum >= 1 && monthNum <= 12) {
                monthlyData.put(months[monthNum - 1], rs.getInt("count"));
            }
        }
        
        return monthlyData;
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
    
    private int getLoginCount() throws SQLException {
        // Placeholder for actual login count
        return getActiveUsers() * 3; // Simulated data
    }
    
    // Inner class for Pie Chart
    class PieChartPanel extends JPanel {
        private Map<String, Integer> data;
        private String title;
        private Color[] colors = {new Color(65, 105, 225),    // Active (Royal Blue)
                                 new Color(220, 20, 60)};    // Inactive (Crimson)
        
        public PieChartPanel(Map<String, Integer> data, String title) {
            this.data = data;
            this.title = title;
            setBackground(Color.WHITE);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw title
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            int titleWidth = g2d.getFontMetrics().stringWidth(title);
            g2d.drawString(title, (getWidth() - titleWidth) / 2, 25);
            
            // Calculate total
            int total = data.values().stream().mapToInt(Integer::intValue).sum();
            if (total == 0) return;
            
            // Draw pie chart
            int diameter = Math.min(getWidth() - 60, getHeight() - 120);
            int x = (getWidth() - diameter) / 2;
            int y = 50;
            
            int startAngle = 0;
            int i = 0;
            
            // Draw pie slices
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                int arcAngle = (int) Math.round(360.0 * entry.getValue() / total);
                g2d.setColor(colors[i % colors.length]);
                g2d.fillArc(x, y, diameter, diameter, startAngle, arcAngle);
                
                // Draw outline
                g2d.setColor(Color.BLACK);
                g2d.drawArc(x, y, diameter, diameter, startAngle, arcAngle);
                
                startAngle += arcAngle;
                i++;
            }
            
            // Draw legend
            int legendX = x + diameter + 20;
            int legendY = y + 30;
            i = 0;
            
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                g2d.setColor(colors[i]);
                g2d.fillRect(legendX, legendY, 15, 15);
                
                g2d.setColor(Color.BLACK);
                String text = String.format("%s: %d (%.1f%%)", 
                        entry.getKey(), 
                        entry.getValue(),
                        100.0 * entry.getValue() / total);
                g2d.drawString(text, legendX + 20, legendY + 12);
                
                legendY += 25;
                i++;
            }
        }
    }
    
    // Inner class for Bar Chart
    class BarChartPanel extends JPanel {
        private Map<String, Integer> data;
        private String title;
        private Color barColor = new Color(70, 130, 180); // Steel Blue
        
        public BarChartPanel(Map<String, Integer> data, String title) {
            this.data = data;
            this.title = title;
            setBackground(Color.WHITE);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw title
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            int titleWidth = g2d.getFontMetrics().stringWidth(title);
            g2d.drawString(title, (getWidth() - titleWidth) / 2, 25);
            
            if (data.isEmpty()) return;
            
            // Find max value for scaling
            int maxValue = data.values().stream().max(Integer::compare).orElse(1);
            if (maxValue == 0) maxValue = 1;  // Prevent division by zero
            
            // Set dimensions
            int leftMargin = 70;
            int rightMargin = 30;
            int topMargin = 50;
            int bottomMargin = 80;
            int graphWidth = getWidth() - leftMargin - rightMargin;
            int graphHeight = getHeight() - topMargin - bottomMargin;
            
            // Draw Y-axis and labels
            g2d.drawLine(leftMargin, topMargin, leftMargin, topMargin + graphHeight);
            
            // Y-axis labels
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            for (int i = 0; i <= 5; i++) {
                int value = maxValue * i / 5;
                int y = topMargin + graphHeight - (graphHeight * i / 5);
                String label = String.valueOf(value);
                int labelWidth = g2d.getFontMetrics().stringWidth(label);
                
                // Tick mark
                g2d.drawLine(leftMargin - 5, y, leftMargin, y);
                
                // Label
                g2d.drawString(label, leftMargin - labelWidth - 10, y + 5);
            }
            
            // Draw X-axis
            g2d.drawLine(leftMargin, topMargin + graphHeight, leftMargin + graphWidth, topMargin + graphHeight);
            
            // Draw bars
            int barWidth = graphWidth / data.size();
            int x = leftMargin;
            int i = 0;
            
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                int barHeight = (int) ((double) entry.getValue() / maxValue * graphHeight);
                int y = topMargin + graphHeight - barHeight;
                
                // Draw bar
                g2d.setColor(barColor);
                g2d.fillRect(x + 10, y, barWidth - 20, barHeight);
                
                // Draw value on top
                g2d.setColor(Color.BLACK);
                String value = String.valueOf(entry.getValue());
                int valueWidth = g2d.getFontMetrics().stringWidth(value);
                g2d.drawString(value, x + (barWidth - valueWidth) / 2, y - 5);
                
                // Draw category label (rotated if needed)
                String category = entry.getKey();
                int catWidth = g2d.getFontMetrics().stringWidth(category);
                
                if (catWidth > barWidth - 20) {
                    // Draw rotated text
                    AffineTransform original = g2d.getTransform();
                    g2d.rotate(Math.toRadians(-45), x + barWidth / 2, topMargin + graphHeight + 25);
                    g2d.drawString(category, x + (barWidth - catWidth) / 2, topMargin + graphHeight + 20);
                    g2d.setTransform(original);
                } else {
                    g2d.drawString(category, x + (barWidth - catWidth) / 2, topMargin + graphHeight + 20);
                }
                
                x += barWidth;
                i++;
            }
            
            // Draw axis labels
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            
            // X-axis label
            String xLabel = "Categories";
            int xLabelWidth = g2d.getFontMetrics().stringWidth(xLabel);
            g2d.drawString(xLabel, leftMargin + (graphWidth - xLabelWidth) / 2, getHeight() - 30);
            
            // Y-axis label (rotated)
            String yLabel = "Count";
            AffineTransform original = g2d.getTransform();
            g2d.rotate(-Math.PI/2);
            int yLabelWidth = g2d.getFontMetrics().stringWidth(yLabel);
            g2d.drawString(yLabel, - (getHeight() / 2 + yLabelWidth / 2), 30);
            g2d.setTransform(original);
        }
    }
    
    // Inner class for Line Chart
    class LineChartPanel extends JPanel {
        private Map<String, Integer> data;
        private String title;
        private Color lineColor = new Color(220, 20, 60); // Crimson
        private Color pointColor = new Color(30, 144, 255); // Dodger Blue
        
        public LineChartPanel(Map<String, Integer> data, String title) {
            this.data = data;
            this.title = title;
            setBackground(Color.WHITE);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw title
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            int titleWidth = g2d.getFontMetrics().stringWidth(title);
            g2d.drawString(title, (getWidth() - titleWidth) / 2, 25);
            
            if (data.isEmpty()) return;
            
            // Find max value for scaling
            int maxValue = data.values().stream().max(Integer::compare).orElse(1);
            if (maxValue == 0) maxValue = 1;  // Prevent division by zero
            
            // Set dimensions
            int leftMargin = 70;
            int rightMargin = 30;
            int topMargin = 50;
            int bottomMargin = 80;
            int graphWidth = getWidth() - leftMargin - rightMargin;
            int graphHeight = getHeight() - topMargin - bottomMargin;
            
            // Draw Y-axis and labels
            g2d.drawLine(leftMargin, topMargin, leftMargin, topMargin + graphHeight);
            
            // Y-axis labels
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            for (int i = 0; i <= 5; i++) {
                int value = maxValue * i / 5;
                int y = topMargin + graphHeight - (graphHeight * i / 5);
                String label = String.valueOf(value);
                int labelWidth = g2d.getFontMetrics().stringWidth(label);
                
                // Tick mark
                g2d.drawLine(leftMargin - 5, y, leftMargin, y);
                
                // Label
                g2d.drawString(label, leftMargin - labelWidth - 10, y + 5);
            }
            
            // Draw X-axis
            g2d.drawLine(leftMargin, topMargin + graphHeight, leftMargin + graphWidth, topMargin + graphHeight);
            
            // Draw points and lines
            int pointSpacing = graphWidth / (data.size() - 1);
            int x = leftMargin;
            int prevX = 0, prevY = 0;
            boolean firstPoint = true;
            
            // Draw month labels and gridlines
            for (String month : data.keySet()) {
                // Draw month label
                int monthWidth = g2d.getFontMetrics().stringWidth(month.substring(0, 3));
                g2d.drawString(month.substring(0, 3), x - monthWidth/2, topMargin + graphHeight + 20);
                
                // Draw vertical gridline
                g2d.setColor(new Color(200, 200, 200));
                g2d.drawLine(x, topMargin, x, topMargin + graphHeight);
                
                x += pointSpacing;
            }
            
            // Reset for drawing data
            x = leftMargin;
            g2d.setColor(lineColor);
            g2d.setStroke(new BasicStroke(2.5f));
            
            for (Integer value : data.values()) {
                int y = topMargin + graphHeight - (int) ((double) value / maxValue * graphHeight);
                
                // Draw data point
                g2d.setColor(pointColor);
                g2d.fillOval(x - 5, y - 5, 10, 10);
                g2d.setColor(lineColor);
                
                // Draw connecting line (skip for first point)
                if (!firstPoint) {
                    g2d.drawLine(prevX, prevY, x, y);
                }
                
                // Draw value label
                g2d.setColor(Color.BLACK);
                String valStr = String.valueOf(value);
                int valWidth = g2d.getFontMetrics().stringWidth(valStr);
                g2d.drawString(valStr, x - valWidth/2, y - 10);
                
                prevX = x;
                prevY = y;
                x += pointSpacing;
                firstPoint = false;
            }
            
            // Draw axis labels
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            
            // X-axis label
            String xLabel = "Month";
            int xLabelWidth = g2d.getFontMetrics().stringWidth(xLabel);
            g2d.drawString(xLabel, leftMargin + (graphWidth - xLabelWidth) / 2, getHeight() - 30);
            
            // Y-axis label (rotated)
            String yLabel = "Number of Users";
            AffineTransform original = g2d.getTransform();
            g2d.rotate(-Math.PI/2);
            int yLabelWidth = g2d.getFontMetrics().stringWidth(yLabel);
            g2d.drawString(yLabel, - (getHeight() / 2 + yLabelWidth / 2), 30);
            g2d.setTransform(original);
        }
    }
}