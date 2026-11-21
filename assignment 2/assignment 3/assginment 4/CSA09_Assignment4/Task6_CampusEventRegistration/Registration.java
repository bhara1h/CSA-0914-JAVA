/**
 * Read-only registration record used between the data, service, and UI layers.
 */
public class Registration {
    private final int id;
    private final String studentName;
    private final String registerNumber;
    private final String department;
    private final String email;
    private final String phone;
    private final String eventName;
    private final String createdAt;

    public Registration(int id, String studentName, String registerNumber,
                        String department, String email, String phone,
                        String eventName, String createdAt) {
        this.id = id;
        this.studentName = studentName;
        this.registerNumber = registerNumber;
        this.department = department;
        this.email = email;
        this.phone = phone;
        this.eventName = eventName;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public String getDepartment() {
        return department;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getEventName() {
        return eventName;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
