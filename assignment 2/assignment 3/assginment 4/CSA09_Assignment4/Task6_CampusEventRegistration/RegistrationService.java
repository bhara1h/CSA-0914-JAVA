import java.sql.SQLException;
import java.util.List;

/**
 * Service Layer: validates requests and coordinates registration data access.
 */
public class RegistrationService {
    private static final int PAGE_SIZE = 50;
    private final RegistrationDao registrationDao;

    public RegistrationService() {
        this(new RegistrationDao());
    }

    RegistrationService(RegistrationDao registrationDao) {
        this.registrationDao = registrationDao;
    }

    public void register(String studentName, String registerNumber, String department,
                         String email, String phone, String eventName)
            throws SQLException {
        String name = required(studentName, "Student name");
        String registrationNumber = required(registerNumber, "Register number");
        String studentDepartment = required(department, "Department");
        String studentEmail = required(email, "Email");
        String studentPhone = required(phone, "Phone");
        String selectedEvent = required(eventName, "Event");

        if (!studentEmail.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        if (!studentPhone.matches("[+0-9() -]{7,20}")) {
            throw new IllegalArgumentException("Enter a valid phone number.");
        }
        int digitCount = studentPhone.replaceAll("\\D", "").length();
        if (digitCount < 7 || digitCount > 15) {
            throw new IllegalArgumentException(
                    "Phone number must contain between 7 and 15 digits.");
        }

        registrationDao.insert(new Registration(0, name, registrationNumber,
                studentDepartment, studentEmail, studentPhone, selectedEvent, ""));
    }

    public List<Registration> getPage(String filter, int pageIndex) throws SQLException {
        if (pageIndex < 0) {
            throw new IllegalArgumentException("Page index cannot be negative.");
        }
        return registrationDao.findPage(safeFilter(filter), PAGE_SIZE, pageIndex * PAGE_SIZE);
    }

    public int count(String filter) throws SQLException {
        return registrationDao.count(safeFilter(filter));
    }

    public int getPageSize() {
        return PAGE_SIZE;
    }

    private String required(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value.trim();
    }

    private String safeFilter(String filter) {
        return filter == null ? "" : filter.trim();
    }
}
