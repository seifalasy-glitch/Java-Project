package gui;

import data.DataStore;
import model.AttendanceReport;
import model.AttendanceSession;
import model.Course;
import model.Teacher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Shows attendance reports in a text area.
 * Used by the Admin (all courses) and by the Teacher (only his courses).
 */
public class ReportsPanel extends JPanel implements ActionListener {

    private Teacher teacher;     // null means the admin is using the panel

    private JComboBox<Course> courseBox = new JComboBox<Course>();
    private JComboBox<AttendanceSession> sessionBox = new JComboBox<AttendanceSession>();
    private JButton courseReportButton = new JButton("Course Report");
    private JButton sessionReportButton = new JButton("Session Report");
    private JTextArea output = new JTextArea();

    public ReportsPanel(Teacher teacher) {
        this.teacher = teacher;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Attendance Reports"));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Course:"));
        row1.add(courseBox);
        row1.add(courseReportButton);
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(new JLabel("Session:"));
        row2.add(sessionBox);
        row2.add(sessionReportButton);
        JPanel top = new JPanel(new GridLayout(2, 1));
        top.add(row1);
        top.add(row2);

        output.setEditable(false);
        output.setFont(new Font("Monospaced", Font.PLAIN, 13));   // columns stay aligned

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(output), BorderLayout.CENTER);

        courseBox.addActionListener(this);      // choosing a course fills the session list
        courseReportButton.addActionListener(this);
        sessionReportButton.addActionListener(this);
        refresh();
    }

    public void refresh() {
        ArrayList<Course> courses = (teacher == null) ? DataStore.getCourses() : teacher.getCourses();
        courseBox.removeAllItems();
        for (Course c : courses) {
            courseBox.addItem(c);
        }
        fillSessions();
    }

    private void fillSessions() {
        sessionBox.removeAllItems();
        Course c = (Course) courseBox.getSelectedItem();
        if (c == null) {
            return;
        }
        for (AttendanceSession s : c.getSessions()) {
            if (s.isClosed()) {
                sessionBox.addItem(s);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == courseBox) {
            fillSessions();
        } else if (src == courseReportButton) {
            Course c = (Course) courseBox.getSelectedItem();
            output.setText(c == null ? "No courses." : AttendanceReport.courseReport(c));
        } else if (src == sessionReportButton) {
            AttendanceSession s = (AttendanceSession) sessionBox.getSelectedItem();
            output.setText(s == null ? "No ended sessions for this course yet."
                    : AttendanceReport.sessionReport(s));
        }
    }
}
