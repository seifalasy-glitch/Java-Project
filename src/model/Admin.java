package model;

import data.DataStore;
import data.IdGenerator;

/**
 * The Admin creates students, teachers and courses,
 * assigns a teacher to each course and enrolls students in courses.
 * adminId is a random unique number of 6 digits.
 * The random IDs of new students and teachers are taken from IdGenerator.
 */
public class Admin extends Person implements Authenticatable {

    private int adminId;
    private String password;

    public Admin(int adminId, String name, String email, String password) {
        super(name, email);
        this.adminId = adminId;
        this.password = password;
    }

    @Override
    public int getId() {
        return adminId;
    }

    @Override
    public String getRole() {
        return "Admin";
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean login(String email, String password) {
        return getEmail().equalsIgnoreCase(email) && this.password.equals(password);
    }

    // ------------------------------------------------------------ students

    public Student createStudent(String name, String email) throws InvalidDataException {
        checkNameAndEmail(name, email);
        Student s = new Student(IdGenerator.newStudentId(), name.trim(), email.trim());
        DataStore.addStudent(s);
        return s;
    }

    public void removeStudent(Student s) {
        DataStore.removeStudent(s);
    }

    // ------------------------------------------------------------ teachers

    public Teacher createTeacher(String name, String email, String password) throws InvalidDataException {
        checkNameAndEmail(name, email);
        if (password == null || password.trim().length() < 4) {
            throw new InvalidDataException("Password must be at least 4 characters.");
        }
        checkText(password);
        Teacher t = new Teacher(IdGenerator.newEmployeeId(), name.trim(), email.trim(), password.trim());
        DataStore.addTeacher(t);
        return t;
    }

    public void removeTeacher(Teacher t) {
        DataStore.removeTeacher(t);
    }

    // ------------------------------------------------------------ courses

    public Course createCourse(String code, String name, Teacher teacher) throws InvalidDataException {
        if (code == null || code.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Course code and name are required.");
        }
        checkText(code);
        checkText(name);
        if (DataStore.findCourse(code.trim()) != null) {
            throw new InvalidDataException("Course code " + code + " already exists.");
        }
        Course c = new Course(code.trim().toUpperCase(), name.trim());
        c.setTeacher(teacher);
        DataStore.addCourse(c);
        return c;
    }

    public void removeCourse(Course c) {
        DataStore.removeCourse(c);
    }

    public void assignTeacher(Course c, Teacher t) {
        c.setTeacher(t);
        DataStore.save();
    }

    public void enrollStudent(Course c, Student s) throws InvalidDataException {
        c.enrollStudent(s);
        DataStore.save();
    }

    public void unenrollStudent(Course c, Student s) {
        c.removeStudent(s);
        DataStore.save();
    }

    // ------------------------------------------------------------ helpers

    /** Checks that the name and email are valid and that the email is not used. */
    private void checkNameAndEmail(String name, String email) throws InvalidDataException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Name is required.");
        }
        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new InvalidDataException("Please enter a valid email.");
        }
        checkText(name);
        checkText(email);
        if (DataStore.emailExists(email.trim())) {
            throw new InvalidDataException("The email " + email + " is already used.");
        }
    }

    /** ';' and ',' are used as separators in the data files, so they are not allowed. */
    private void checkText(String text) throws InvalidDataException {
        if (text.contains(";") || text.contains(",")) {
            throw new InvalidDataException("The characters ; and , are not allowed.");
        }
    }
}
