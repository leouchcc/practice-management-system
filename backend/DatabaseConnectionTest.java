import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseConnectionTest {
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
            
            // Create table
            Statement statement = connection.createStatement();
            String createTableSQL = "CREATE TABLE IF NOT EXISTS certificate (" +
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
            System.out.println("Successfully created certificate table!");
            
            // Close connection
            statement.close();
            connection.close();
            System.out.println("Database connection closed!");
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}