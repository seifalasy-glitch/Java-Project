package model;

/**
 * One line in the attendance sheet of a session:
 * which student, and was he present or absent.
 */
public class AttendanceRecord {

    private Student student;
    private boolean present;

    public AttendanceRecord(Student student, boolean present) {
        this.student = student;
        this.present = present;
    }

    public Student getStudent() {
        return student;
    }

    public boolean isPresent() {
        return present;
    }

    @Override
    public String toString() {
        return student.getId() + "  " + student.getName() + "  " + (present ? "Present" : "Absent");
    }
}
