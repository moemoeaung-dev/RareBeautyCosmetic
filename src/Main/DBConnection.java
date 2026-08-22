package Main;
import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/rarebeauty";
    private static final String USER = "root";      // MySQL username
    private static final String PASSWORD = "root";      // Empty password

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //System.out.println("connected");
            return DriverManager.getConnection(URL, USER, PASSWORD);
            
        } catch (Exception e) {
            System.out.println("❌ Database connection failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

    }
    public static void main(String[] args) {
    	new DBConnection();
    }
}
