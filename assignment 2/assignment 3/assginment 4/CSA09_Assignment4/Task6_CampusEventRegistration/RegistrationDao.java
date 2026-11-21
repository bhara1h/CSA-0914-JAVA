import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Layer: all SQL for event registration is kept here.
 */
public class RegistrationDao {
    public void insert(Registration registration) throws SQLException {
        String sql = "INSERT INTO event_registrations "
                + "(student_name, register_number, department, email, phone, event_name) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, registration.getStudentName());
            statement.setString(2, registration.getRegisterNumber());
            statement.setString(3, registration.getDepartment());
            statement.setString(4, registration.getEmail());
            statement.setString(5, registration.getPhone());
            statement.setString(6, registration.getEventName());
            statement.executeUpdate();
        }
    }

    public List<Registration> findPage(String filter, int limit, int offset)
            throws SQLException {
        String sql = "SELECT id, student_name, register_number, department, email, "
                + "phone, event_name, created_at FROM event_registrations "
                + "WHERE student_name LIKE ? COLLATE NOCASE "
                + "OR register_number LIKE ? COLLATE NOCASE "
                + "OR department LIKE ? COLLATE NOCASE "
                + "OR email LIKE ? COLLATE NOCASE "
                + "OR event_name LIKE ? COLLATE NOCASE "
                + "ORDER BY created_at DESC, id DESC LIMIT ? OFFSET ?";
        List<Registration> registrations = new ArrayList<>();
        String pattern = "%" + filter.trim() + "%";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int index = 1; index <= 5; index++) {
                statement.setString(index, pattern);
            }
            statement.setInt(6, limit);
            statement.setInt(7, offset);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    registrations.add(mapRegistration(results));
                }
            }
        }
        return registrations;
    }

    public int count(String filter) throws SQLException {
        String sql = "SELECT COUNT(*) FROM event_registrations "
                + "WHERE student_name LIKE ? COLLATE NOCASE "
                + "OR register_number LIKE ? COLLATE NOCASE "
                + "OR department LIKE ? COLLATE NOCASE "
                + "OR email LIKE ? COLLATE NOCASE "
                + "OR event_name LIKE ? COLLATE NOCASE";
        String pattern = "%" + filter.trim() + "%";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int index = 1; index <= 5; index++) {
                statement.setString(index, pattern);
            }
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? results.getInt(1) : 0;
            }
        }
    }

    private Registration mapRegistration(ResultSet results) throws SQLException {
        return new Registration(
                results.getInt("id"),
                results.getString("student_name"),
                results.getString("register_number"),
                results.getString("department"),
                results.getString("email"),
                results.getString("phone"),
                results.getString("event_name"),
                results.getString("created_at"));
    }
}
