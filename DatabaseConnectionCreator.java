/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.p.ticketsystemspark;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Praktikant
 */
public class DatabaseConnectionCreator {
        static String DBString = "jdbc:mysql://10.0.10.142:3306/d1?serverTimezone=UTC";
        static Connection GetConnection() throws SQLException
        {
            return DriverManager.getConnection(DBString, "tss.d1user", "pass");
        }
        
}
