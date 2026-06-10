

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CreateCertificateTable {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/practice_management?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "123520";
        
        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {
            
            System.out.println("Starting to create certificate table...");
            
            // Create certificate table
            String createCertificateTable = "CREATE TABLE IF NOT EXISTS certificate (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "certificate_number VARCHAR(50) UNIQUE NOT NULL, " +
                "activity_id BIGINT NOT NULL, " +
                "student_id BIGINT NOT NULL, " +
                "certificate_name VARCHAR(255) NOT NULL, " +
                "issue_date DATE NOT NULL, " +
                "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (activity_id) REFERENCES activity(id), " +
                "FOREIGN KEY (student_id) REFERENCES sys_user(id) " +
                ");";
            statement.executeUpdate(createCertificateTable);
            System.out.println("Successfully created certificate table");
            
            System.out.println("Database table creation completed successfully!");
            
        } catch (Exception e) {
            System.out.println("Database table creation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}