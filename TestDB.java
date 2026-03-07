import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TestDB {
    public static void main(String[] args) {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=ReviewPlatform;trustServerCertificate=true;";
        String user = "sa";
        String pass = "12345";

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("Driver loaded.");

            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                System.out.println("Connected to database.");

                String sql = "SELECT * FROM Users";
                try (PreparedStatement stmt = conn.prepareStatement(sql);
                        ResultSet rs = stmt.executeQuery()) {

                    System.out.println("Users in DB:");
                    int count = 0;
                    while (rs.next()) {
                        System.out.println(rs.getString("username") + " | role_id: " + rs.getInt("role_id"));
                        count++;
                    }
                    System.out.println("Total users: " + count);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
