package com.practice.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseUpdaterForProof {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/practice_management_system?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "123456";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();

            System.out.println("Database connection successful!");

            // 添加proof_file_path字段
            String addProofFilePathColumn = "ALTER TABLE activity_registration ADD COLUMN proof_file_path VARCHAR(255) DEFAULT NULL COMMENT '证明文件路径'";
            try {
                statement.executeUpdate(addProofFilePathColumn);
                System.out.println("Successfully added proof_file_path column!");
            } catch (Exception e) {
                System.out.println("proof_file_path column might already exist: " + e.getMessage());
            }

            // 添加proof_file_name字段
            String addProofFileNameColumn = "ALTER TABLE activity_registration ADD COLUMN proof_file_name VARCHAR(255) DEFAULT NULL COMMENT '证明文件名'";
            try {
                statement.executeUpdate(addProofFileNameColumn);
                System.out.println("Successfully added proof_file_name column!");
            } catch (Exception e) {
                System.out.println("proof_file_name column might already exist: " + e.getMessage());
            }

            // 添加proof_submit_time字段
            String addProofSubmitTimeColumn = "ALTER TABLE activity_registration ADD COLUMN proof_submit_time DATETIME DEFAULT NULL COMMENT '证明提交时间'";
            try {
                statement.executeUpdate(addProofSubmitTimeColumn);
                System.out.println("Successfully added proof_submit_time column!");
            } catch (Exception e) {
                System.out.println("proof_submit_time column might already exist: " + e.getMessage());
            }

            statement.close();
            connection.close();
            System.out.println("Database connection closed!");
            System.out.println("Proof fields update completed successfully!");

        } catch (Exception e) {
            System.out.println("Error updating database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
