import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class FixCertificateTable {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/practice_management?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "123520";
        
        try {
            // Load driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish connection
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Database connection successful!");
            
            // Create statement
            Statement statement = connection.createStatement();
            
            // Drop existing table if exists
            String dropTableSQL = "DROP TABLE IF EXISTS certificate;";
            statement.executeUpdate(dropTableSQL);
            System.out.println("Dropped existing certificate table if it existed!");
            
            // Create new table with correct structure
            String createTableSQL = "CREATE TABLE certificate (" +
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
            statement.executeUpdate(createTableSQL);
            System.out.println("Created new certificate table with correct structure!");
            
            // Insert test data
            String insertTestDataSQL = "INSERT INTO certificate (certificate_number, activity_id, student_id, certificate_name, issue_date) " +
                    "VALUES ('CERT-TEST000001', 1, 7, '测试证书', CURDATE());";
            statement.executeUpdate(insertTestDataSQL);
            System.out.println("Inserted test certificate data!");
            
            // Close resources
            statement.close();
            connection.close();
            System.out.println("Database connection closed!");
            System.out.println("Certificate table fix completed successfully!");
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}