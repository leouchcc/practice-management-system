package com.practice.util;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@Component
public class DatabaseUpdater {

    public void updateDatabase() {
        String url = "jdbc:mysql://localhost:3306/practice_management?useSSL=false&serverTimezone=UTC";
        String username = "root";
        String password = "123456";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {

            // 读取SQL文件
            String sql = readSqlFile("c:\\Users\\Liu17\\OneDrive\\Desktop\\lhh35\\backend\\update_activity_table.sql");

            // 执行SQL语句
            statement.executeUpdate(sql);
            System.out.println("Database updated successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error updating database: " + e.getMessage());
        }
    }

    private String readSqlFile(String filePath) throws IOException {
        StringBuilder sql = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sql.append(line).append("\n");
            }
        }
        return sql.toString();
    }
}
