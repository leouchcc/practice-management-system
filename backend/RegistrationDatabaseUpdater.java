package com.practice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class RegistrationDatabaseUpdater {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/practice_management?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "123520";
        
        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {
            
            System.out.println("Starting database update for activity_registration table...");
            
            // Add missing fields to activity_registration table
            String alterRegistrationTable = "ALTER TABLE activity_registration ADD COLUMN proof_file_path VARCHAR(255), ADD COLUMN proof_file_name VARCHAR(255)";
            statement.executeUpdate(alterRegistrationTable);
            System.out.println("Successfully added proof_file_path and proof_file_name fields");
            
            System.out.println("Database update completed successfully!");
            
        } catch (Exception e) {
            System.out.println("Database update failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}