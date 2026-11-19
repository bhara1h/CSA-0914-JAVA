import java.util.HashSet;
import java.util.Set;

public class Attendance {
    private final Set<Student> presentStudents = new HashSet<>();

    public boolean markPresent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student is required.");
        }
        return presentStudents.add(student);
    }

    public int uniqueStudentCount() {
        return presentStudents.size();
    }

    public static void main(String[] args) {
        Attendance attendance = new Attendance();
        attendance.markPresent(new Student("CSA001", "Asha"));
        attendance.markPresent(new Student("CSA001", "Asha duplicate"));
        attendance.markPresent(new Student("CSA002", "Ben"));
        System.out.println("Unique students: " + attendance.uniqueStudentCount());
    }
}
