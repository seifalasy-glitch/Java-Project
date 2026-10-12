package model;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Builds the attendance reports as text.
 * All methods are static because the class keeps no data of its own
 * (Lecture 2 - static methods).
 */
public class AttendanceReport {

    /** Number of ended sessions of one type ("Lecture", "Lab", "Section") in a course. */
    public static int countSessions(Course c, String type) {
        int count = 0;
        for (AttendanceSession s : c.getSessions()) {
            if (s.isClosed() && s.getType().equals(type)) {
                count++;
            }
        }
        return count;
    }

    /** Number of sessions of one type that the student attended. */
    public static int countAttended(Course c, Student st, String type) {
        int count = 0;
        for (AttendanceSession s : c.getSessions()) {
            if (s.isClosed() && s.getType().equals(type) && s.isPresent(st)) {
                count++;
            }
        }
        return count;
    }

    /** Percentage of all ended sessions (all types) that the student attended. */
    public static double attendanceRate(Course c, Student st) {
        int total = 0;
        int attended = 0;
        String[] types = {"Lecture", "Lab", "Section"};
        for (String t : types) {
            total += countSessions(c, t);
            attended += countAttended(c, st, t);
        }
        if (total == 0) {
            return 0;
        }
        return attended * 100.0 / total;
    }

    /** Report for a whole course: one line per student. */
    public static String courseReport(Course c) {
        StringBuffer sb = new StringBuffer();
        int lec = countSessions(c, "Lecture");
        int lab = countSessions(c, "Lab");
        int sec = countSessions(c, "Section");

        sb.append("Attendance report - ").append(c).append("\n");
        sb.append("Teacher: ").append(c.getTeacher() == null ? "not assigned" : c.getTeacher().getName()).append("\n");
        sb.append("Ended sessions: ").append(lec).append(" lectures, ")
          .append(lab).append(" labs, ").append(sec).append(" sections\n\n");
        sb.append(String.format("%-6s %-22s %-9s %-9s %-9s %s%n", "ID", "Name", "Lecture", "Lab", "Section", "Total"));
        sb.append("-----------------------------------------------------------------\n");

        ArrayList<Student> list = new ArrayList<Student>(c.getStudents());
        Collections.sort(list);                         // uses Student.compareTo
        for (Student st : list) {
            sb.append(String.format("%-6d %-22s %-9s %-9s %-9s %.0f%%%n",
                    st.getId(), cut(st.getName(), 22),
                    countAttended(c, st, "Lecture") + "/" + lec,
                    countAttended(c, st, "Lab") + "/" + lab,
                    countAttended(c, st, "Section") + "/" + sec,
                    attendanceRate(c, st)));
        }
        if (list.isEmpty()) {
            sb.append("No students enrolled.\n");
        }
        return sb.toString();
    }

    /** Report for one session: who was present and who was absent. */
    public static String sessionReport(AttendanceSession s) {
        StringBuffer sb = new StringBuffer();
        sb.append(s.getCourse()).append("\n");
        sb.append(s).append("\n");
        sb.append("Present: ").append(s.countPresent())
          .append("   Absent: ").append(s.countAbsent()).append("\n\n");
        for (AttendanceRecord r : s.getRecords()) {
            sb.append(r).append("\n");
        }
        return sb.toString();
    }

    private static String cut(String text, int max) {
        return text.length() <= max ? text : text.substring(0, max - 1) + ".";
    }
}
