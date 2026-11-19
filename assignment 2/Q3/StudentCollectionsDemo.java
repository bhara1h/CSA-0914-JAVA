import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class StudentCollectionsDemo {
    public static void main(String[] args) {
        List<LegacyStudent> legacyScanned = new ArrayList<>();
        legacyScanned.add(new LegacyStudent("S101", "Asha"));
        legacyScanned.add(new LegacyStudent("S101", "Asha duplicate"));
        legacyScanned.add(new LegacyStudent("S102", "Ben"));

        Set<LegacyStudent> legacySet = new HashSet<>();
        legacySet.addAll(legacyScanned);
        System.out.println("Legacy equals-only HashSet size: " + legacySet.size()
                + " (incorrect; expected 2)");

        List<Student> scannedStudents = new ArrayList<>();
        scannedStudents.add(new Student("S101", "Asha"));
        scannedStudents.add(new Student("S101", "Asha duplicate"));
        scannedStudents.add(new Student("S102", "Ben"));

        Student query = new Student("S101", "Lookup");
        System.out.println("Normal list scan found S101: " + scannedStudents.contains(query));
        System.out.println("Duplicate scan matches for S101: "
                + countRegistration(scannedStudents, "S101"));

        Set<Student> students = new HashSet<>();
        students.addAll(scannedStudents);
        System.out.println("Unique students: " + students.size());

        Set<Student> sortedStudents =
                new TreeSet<>(Comparator.comparing(Student::getRegNo));
        sortedStudents.addAll(scannedStudents);
        System.out.println("TreeSet students by registration: " + sortedStudents.size());

        Map<String, Student> byRegistration = new HashMap<>();
        for (Student student : scannedStudents) {
            byRegistration.put(student.getRegNo(), student);
        }
        System.out.println("HashMap students by registration: " + byRegistration.size());
        System.out.println("HashMap lookup S102 found: " + byRegistration.containsKey("S102"));

        Set<Student> mutableKeySet = new HashSet<>();
        Student mutableKey = new Student("S200", "Casey");
        mutableKeySet.add(mutableKey);
        mutableKey.setRegNo("S201");
        System.out.println("Mutable HashSet key found after regNo change: "
                + mutableKeySet.contains(mutableKey));
        System.out.println("Mutable HashSet key removable after regNo change: "
                + mutableKeySet.remove(mutableKey));

        Student collisionOne = new Student("Aa", "One");
        Student collisionTwo = new Student("BB", "Two");
        Set<Student> collisionSet = new HashSet<>();
        collisionSet.add(collisionOne);
        collisionSet.add(collisionTwo);
        System.out.println("Aa and BB hash collision: "
                + (collisionOne.hashCode() == collisionTwo.hashCode())
                + "; distinct students retained: " + collisionSet.size());
    }

    private static int countRegistration(List<Student> students, String regNo) {
        int matches = 0;
        for (Student student : students) {
            if (student.getRegNo().equals(regNo)) {
                matches++;
            }
        }
        return matches;
    }

    private static final class LegacyStudent {
        private final String regNo;
        private final String name;

        private LegacyStudent(String regNo, String name) {
            this.regNo = regNo;
            this.name = name;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof LegacyStudent)) {
                return false;
            }
            LegacyStudent student = (LegacyStudent) other;
            return regNo.equals(student.regNo);
        }
    }

}
