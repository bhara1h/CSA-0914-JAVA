import java.util.Objects;

public class Student {
    private String regNo;
    private final String name;

    public Student(String regNo, String name) {
        this.regNo = requireText(regNo, "Registration number");
        this.name = requireText(name, "Student name");
    }

    public String getRegNo() {
        return regNo;
    }

    public String getName() {
        return name;
    }

    // Changing regNo while stored in a hash collection demonstrates a mutable-key hazard.
    public void setRegNo(String regNo) {
        this.regNo = requireText(regNo, "Registration number");
    }

    private static String requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " cannot be empty.");
        }
        return value.trim();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Student)) {
            return false;
        }
        Student student = (Student) other;
        return regNo.equals(student.regNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regNo);
    }

    @Override
    public String toString() {
        return name + "(" + regNo + ")";
    }
}
