import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseUpdater {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/practice_management?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "123520";
        
        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {
            
            System.out.println("Starting database update...");
            
            // Add certificate-related fields to activity table
            String alterActivityTable = "ALTER TABLE activity ADD COLUMN is_certificate BOOLEAN DEFAULT FALSE";
            statement.executeUpdate(alterActivityTable);
            System.out.println("Successfully added is_certificate field");
            
            String alterActivityTable2 = "ALTER TABLE activity ADD COLUMN certificate_name VARCHAR(255)";
            statement.executeUpdate(alterActivityTable2);
            System.out.println("Successfully added certificate_name field");
            
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
            
            System.out.println("Database update completed successfully!");
            
        } catch (Exception e) {
            System.out.println("Database update failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}