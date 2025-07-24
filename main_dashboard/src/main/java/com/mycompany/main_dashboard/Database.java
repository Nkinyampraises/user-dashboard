/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.main_dashboard;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mariadb://localhost:3306/userdb";
        String user = "root";
        String password = ""; 

        return DriverManager.getConnection(url, user, password);
    }
}