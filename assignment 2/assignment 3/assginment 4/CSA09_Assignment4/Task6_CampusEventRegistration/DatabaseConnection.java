import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Layer: centralizes the SQLite JDBC connection URL.
 * Run the application from the assignment root directory.
 */
public final class DatabaseConnection {
    private static final String URL =
            "jdbc:sqlite:CSA09_Assignment4/database/campus_events.db";

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
