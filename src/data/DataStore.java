package data;

import model.*;

import java.io.*;
import java.util.ArrayList;

/**
 * DataStore is the "database" of the program.
 * While the program runs, all objects are kept in ArrayLists (Lecture 3).
 * The lists are saved in text files inside the "data" folder and loaded
 * again when the program starts (Lecture 6 - File Handling):
 *
 *   admins.txt    id;name;email;password
 *   teachers.txt  id;name;email;password
 *   students.txt  id;name;email
 *   courses.txt   code;name;teacherId;studentId,studentId,...
 *   sessions.txt  id;courseCode;type;date;detail;studentId:1,studentId:0,...
 *                 (1 = present, 0 = absent)
 *
 * Everything is static, so any class can use DataStore without creating an object.
 */
public class DataStore {

    private static final String FOLDER = "data";

    private static ArrayList<Admin> admins = new ArrayList<Admin>();
    private static ArrayList<Teacher> teachers = new ArrayList<Teacher>();
    private static ArrayList<Student> students = new ArrayList<Student>();
    private static ArrayList<Course> courses = new ArrayList<Course>();

    // ================================================================ getters

    public static ArrayList<Admin> getAdmins() {
        return admins;
    }

    public static ArrayList<Teacher> getTeachers() {
        return teachers;
    }

    public static ArrayList<Student> getStudents() {
        return students;
    }

    public static ArrayList<Course> getCourses() {
        return courses;
    }

    // ================================================================ add / remove

    public static void addAdmin(Admin a) {
        admins.add(a);
        save();
    }

    public static void addStudent(Student s) {
        students.add(s);
        save();
    }

    public static void addTeacher(Teacher t) {
        teachers.add(t);
        save();
    }

    public static void addCourse(Course c) {
        courses.add(c);
        save();
    }

    /** Removes the student from the system and from every course. */
    public static void removeStudent(Student s) {
        students.remove(s);
        for (Course c : courses) {
            c.removeStudent(s);
        }
        save();
    }

    /** Removes the teacher; his courses stay but without a teacher. */
    public static void removeTeacher(Teacher t) {
        teachers.remove(t);
        for (Course c : courses) {
            if (c.getTeacher() == t) {
                c.setTeacher(null);
            }
        }
        save();
    }

    public static void removeCourse(Course c) {
        courses.remove(c);
        save();
    }

    // ================================================================ search

    /** Returns the Admin or Teacher with this email and password, or null. */
    public static Person login(String email, String password) {
        for (Admin a : admins) {
            if (a.login(email, password)) {
                return a;
            }
        }
        for (Teacher t : teachers) {
            if (t.login(email, password)) {
                return t;
            }
        }
        return null;
    }

    public static boolean emailExists(String email) {
        ArrayList<Person> all = new ArrayList<Person>();
        all.addAll(admins);
        all.addAll(teachers);
        all.addAll(students);
        for (Person p : all) {
            if (p.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    public static Student findStudent(int attendanceId) {
        for (Student s : students) {
            if (s.getStudentAttendanceId() == attendanceId) {
                return s;
            }
        }
        return null;
    }

    public static Teacher findTeacher(int employeeId) {
        for (Teacher t : teachers) {
            if (t.getId() == employeeId) {
                return t;
            }
        }
        return null;
    }

    public static Course findCourse(String code) {
        for (Course c : courses) {
            if (c.getCode().equalsIgnoreCase(code)) {
                return c;
            }
        }
        return null;
    }

    public static ArrayList<Course> getCoursesOf(Teacher t) {
        ArrayList<Course> result = new ArrayList<Course>();
        for (Course c : courses) {
            if (c.getTeacher() == t) {
                result.add(c);
            }
        }
        return result;
    }

    // ================================================================ save

    /** Writes all the lists to the text files (overwrites the old files). */
    public static void save() {
        new File(FOLDER).mkdirs();
        try {
            BufferedWriter w = new BufferedWriter(new FileWriter(FOLDER + "/admins.txt"));
            for (Admin a : admins) {
                w.write(a.getId() + ";" + a.getName() + ";" + a.getEmail() + ";" + a.getPassword());
                w.newLine();
            }
            w.close();

            w = new BufferedWriter(new FileWriter(FOLDER + "/teachers.txt"));
            for (Teacher t : teachers) {
                w.write(t.getId() + ";" + t.getName() + ";" + t.getEmail() + ";" + t.getPassword());
                w.newLine();
            }
            w.close();

            w = new BufferedWriter(new FileWriter(FOLDER + "/students.txt"));
            for (Student s : students) {
                w.write(s.getId() + ";" + s.getName() + ";" + s.getEmail());
                w.newLine();
            }
            w.close();

            w = new BufferedWriter(new FileWriter(FOLDER + "/courses.txt"));
            for (Course c : courses) {
                int teacherId = (c.getTeacher() == null) ? 0 : c.getTeacher().getId();
                String ids = "";
                for (Student s : c.getStudents()) {
                    ids += (ids.isEmpty() ? "" : ",") + s.getId();
                }
                w.write(c.getCode() + ";" + c.getName() + ";" + teacherId + ";" + ids);
                w.newLine();
            }
            w.close();

            w = new BufferedWriter(new FileWriter(FOLDER + "/sessions.txt"));
            for (Course c : courses) {
                for (AttendanceSession s : c.getSessions()) {
                    if (!s.isClosed()) {
                        continue;                 // only ended sessions are saved
                    }
                    String recs = "";
                    for (AttendanceRecord r : s.getRecords()) {
                        recs += (recs.isEmpty() ? "" : ",") + r.getStudent().getId() + ":" + (r.isPresent() ? 1 : 0);
                    }
                    w.write(s.getSessionId() + ";" + c.getCode() + ";" + s.getType() + ";"
                            + s.getDate() + ";" + detailOf(s) + ";" + recs);
                    w.newLine();
                }
            }
            w.close();
        } catch (IOException e) {
            System.out.println("Error while saving: " + e.getMessage());
        }
    }

    /** The raw extra value of each session type, as it is written in the file. */
    private static String detailOf(AttendanceSession s) {
        if (s instanceof LectureSession) {
            return ((LectureSession) s).getTopic();
        } else if (s instanceof LabSession) {
            return ((LabSession) s).getExperiment();
        } else {
            return String.valueOf(((SectionSession) s).getGroupNumber());
        }
    }

    // ================================================================ load

    /**
     * Reads all the files. If the data folder does not exist yet
     * (first run), it creates some demo data so the program can be tried at once.
     */
    public static void load() {
        if (!new File(FOLDER + "/admins.txt").exists()) {
            createDemoData();
            return;
        }
        try {
            for (String[] p : readFile("admins.txt")) {
                admins.add(new Admin(Integer.parseInt(p[0]), p[1], p[2], p[3]));
                IdGenerator.register(Integer.parseInt(p[0]));
            }
            for (String[] p : readFile("teachers.txt")) {
                teachers.add(new Teacher(Integer.parseInt(p[0]), p[1], p[2], p[3]));
                IdGenerator.register(Integer.parseInt(p[0]));
            }
            for (String[] p : readFile("students.txt")) {
                students.add(new Student(Integer.parseInt(p[0]), p[1], p[2]));
                IdGenerator.register(Integer.parseInt(p[0]));
            }
            for (String[] p : readFile("courses.txt")) {
                Course c = new Course(p[0], p[1]);
                c.setTeacher(findTeacher(Integer.parseInt(p[2])));
                if (!p[3].isEmpty()) {
                    for (String id : p[3].split(",")) {
                        Student s = findStudent(Integer.parseInt(id));
                        if (s != null) {
                            c.getStudents().add(s);
                        }
                    }
                }
                courses.add(c);
            }
            for (String[] p : readFile("sessions.txt")) {
                Course c = findCourse(p[1]);
                if (c == null) {
                    continue;
                }
                int id = Integer.parseInt(p[0]);
                AttendanceSession s;
                if (p[2].equals("Lecture")) {
                    s = new LectureSession(id, c, p[3], true, p[4]);
                } else if (p[2].equals("Lab")) {
                    s = new LabSession(id, c, p[3], true, p[4]);
                } else {
                    s = new SectionSession(id, c, p[3], true, Integer.parseInt(p[4]));
                }
                if (!p[5].isEmpty()) {
                    for (String rec : p[5].split(",")) {
                        String[] parts = rec.split(":");
                        Student st = findStudent(Integer.parseInt(parts[0]));
                        if (st != null) {
                            s.addRecord(new AttendanceRecord(st, parts[1].equals("1")));
                        }
                    }
                }
                c.addSession(s);
            }
        } catch (IOException e) {
            System.out.println("Error while loading: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("A data file is damaged: " + e.getMessage());
        }
    }

    /** Reads one file and returns every line split on ';'. */
    private static ArrayList<String[]> readFile(String name) throws IOException {
        ArrayList<String[]> lines = new ArrayList<String[]>();
        File f = new File(FOLDER + "/" + name);
        if (!f.exists()) {
            return lines;
        }
        BufferedReader r = new BufferedReader(new FileReader(f));
        String line;
        while ((line = r.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                lines.add(line.split(";", -1));   // -1 keeps empty last parts
            }
        }
        r.close();
        return lines;
    }

    /** Demo data used on the very first run only. */
    private static void createDemoData() {
        Admin admin = new Admin(IdGenerator.newAdminId(), "System Admin", "admin@ams.com", "admin123");
        admins.add(admin);
        try {
            Teacher t1 = admin.createTeacher("Dr. Hany Samir", "hany@ams.com", "1234");
            Teacher t2 = admin.createTeacher("Eng. Ahmed Fathy", "ahmed@ams.com", "1234");
            Course java = admin.createCourse("COMP301", "Advanced Programming (Java)", t1);
            Course db = admin.createCourse("COMP303", "Databases", t2);
            String[] names = {"Mohamed Ali", "Sara Hassan", "Omar Khaled", "Mona Adel", "Youssef Samir"};
            for (int i = 0; i < names.length; i++) {
                Student s = admin.createStudent(names[i], "student" + (i + 1) + "@ams.com");
                admin.enrollStudent(java, s);
                if (i < 3) {
                    admin.enrollStudent(db, s);
                }
            }
        } catch (InvalidDataException e) {
            System.out.println(e.getMessage());
        }
        save();
    }
}
