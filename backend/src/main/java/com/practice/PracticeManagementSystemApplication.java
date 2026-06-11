package com.practice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@SpringBootApplication
@MapperScan("com.practice.mapper")
public class PracticeManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(PracticeManagementSystemApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            try {
                String url = System.getenv("JDBC_DATABASE_URL");
                if (url == null || url.isEmpty()) {
                    url = "jdbc:mysql://localhost:3306/practice_management?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
                }
                
                String username = System.getenv("MYSQLUSER");
                if (username == null || username.isEmpty()) {
                    username = "root";
                }
                
                String password = System.getenv("MYSQLPASSWORD");
                if (password == null || password.isEmpty()) {
                    password = "123520";
                }

                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url, username, password);
                System.out.println("Database connection successful for initialization!");

                Statement statement = connection.createStatement();

                String addProofFilePathColumn = "ALTER TABLE activity_registration ADD COLUMN proof_file_path VARCHAR(255) DEFAULT NULL COMMENT '证明文件路径'";
                try {
                    statement.executeUpdate(addProofFilePathColumn);
                    System.out.println("Successfully added proof_file_path column!");
                } catch (Exception e) {
                    System.out.println("proof_file_path column might already exist: " + e.getMessage());
                }

                String addProofFileNameColumn = "ALTER TABLE activity_registration ADD COLUMN proof_file_name VARCHAR(255) DEFAULT NULL COMMENT '证明文件名'";
                try {
                    statement.executeUpdate(addProofFileNameColumn);
                    System.out.println("Successfully added proof_file_name column!");
                } catch (Exception e) {
                    System.out.println("proof_file_name column might already exist: " + e.getMessage());
                }

                String addProofSubmitTimeColumn = "ALTER TABLE activity_registration ADD COLUMN proof_submit_time DATETIME DEFAULT NULL COMMENT '证明提交时间'";
                try {
                    statement.executeUpdate(addProofSubmitTimeColumn);
                    System.out.println("Successfully added proof_submit_time column!");
                } catch (Exception e) {
                    System.out.println("proof_submit_time column might already exist: " + e.getMessage());
                }

                String addProofVerifiedColumn = "ALTER TABLE activity_registration ADD COLUMN proof_verified BOOLEAN DEFAULT FALSE COMMENT '证明是否已验证'";
                try {
                    statement.executeUpdate(addProofVerifiedColumn);
                    System.out.println("Successfully added proof_verified column!");
                } catch (Exception e) {
                    System.out.println("proof_verified column might already exist: " + e.getMessage());
                }

                String addCancelDeadlineColumn = "ALTER TABLE activity ADD COLUMN cancel_deadline DATETIME DEFAULT NULL COMMENT '取消截止时间'";
                try {
                    statement.executeUpdate(addCancelDeadlineColumn);
                    System.out.println("Successfully added cancel_deadline column!");
                } catch (Exception e) {
                    System.out.println("cancel_deadline column might already exist: " + e.getMessage());
                }

                String addIsContactActivityColumn = "ALTER TABLE activity ADD COLUMN is_contact_activity TINYINT DEFAULT 0 COMMENT '是否联系活动'";
                try {
                    statement.executeUpdate(addIsContactActivityColumn);
                    System.out.println("Successfully added is_contact_activity column!");
                } catch (Exception e) {
                    System.out.println("is_contact_activity column might already exist: " + e.getMessage());
                }

                String addRealNameColumn = "ALTER TABLE sys_user ADD COLUMN real_name VARCHAR(50) DEFAULT NULL COMMENT '真实姓名'";
                try {
                    statement.executeUpdate(addRealNameColumn);
                    System.out.println("Successfully added real_name column!");
                } catch (Exception e) {
                    System.out.println("real_name column might already exist: " + e.getMessage());
                }

                statement.close();
                connection.close();
                System.out.println("Database connection closed after initialization!");

            } catch (Exception e) {
                System.out.println("Database initialization error: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}