package gui;

import model.AttendanceReport;
import model.AttendanceSession;
import model.Course;
import model.InvalidDataException;
import model.Student;
import model.Teacher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Main window of the Teacher. Flow:
 *  1) choose one of his courses and the type (Lecture / Lab / Section) -> Start Session
 *  2) each student says his attendance ID; the teacher types it -> Mark Present (or Enter)
 *  3) End Session -> everyone not marked is saved as absent.
 * A second card shows the reports of his courses.
 */
public class TeacherFrame extends JFrame implements ActionListener, ItemListener {

    private Teacher teacher;
    private AttendanceSession currentSession;     // null when no session is running

    // menu
    private JButton attendanceButton = new JButton("Take Attendance");
    private JButton reportsButton = new JButton("Reports");
    private JButton logoutButton = new JButton("Logout");
    private CardLayout cards = new CardLayout();
    private JPanel center = new JPanel(cards);
    private ReportsPanel reportsPanel;

    // step 1
    private JComboBox<Course> courseBox = new JComboBox<Course>();
    private JRadioButton lectureRadio = new JRadioButton("Lecture", true);
    private JRadioButton labRadio = new JRadioButton("Lab");
    private JRadioButton sectionRadio = new JRadioButton("Section");
    private JLabel detailLabel = new JLabel("Topic:");
    private JTextField detailField = new JTextField(14);
    private JButton startButton = new JButton("Start Session");

    // step 2
    private JLabel sessionLabel = new JLabel("No session running.");
    private JTextField idField = new JTextField(8);
    private JButton markButton = new JButton("Mark Present");
    private JLabel countLabel = new JLabel(" ");
    private JTextArea log = new JTextArea();
    private JButton endButton = new JButton("End Session");

    public TeacherFrame(Teacher teacher) {
        this.teacher = teacher;
        setTitle("Teacher - " + teacher.getName());

        // ---------- top bar
        JLabel welcome = new JLabel("  Welcome " + teacher.getName() + "   (Employee ID: " + teacher.getId() + ")");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 15));
        JPanel top = new JPanel(new BorderLayout());
        top.add(welcome, BorderLayout.CENTER);
        top.add(logoutButton, BorderLayout.EAST);
        top.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel menu = new JPanel(new GridLayout(0, 1, 5, 5));   // one column
        menu.add(attendanceButton);
        menu.add(reportsButton);
        menu.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // ---------- step 1 panel
        ButtonGroup group = new ButtonGroup();     // only one type can be selected
        group.add(lectureRadio);
        group.add(labRadio);
        group.add(sectionRadio);

        JPanel step1 = new JPanel(new GridLayout(3, 1));
        step1.setBorder(BorderFactory.createTitledBorder("Step 1: choose course and session type"));
        JPanel r1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        r1.add(new JLabel("Course:"));
        r1.add(courseBox);
        JPanel r2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        r2.add(new JLabel("Type:"));
        r2.add(lectureRadio);
        r2.add(labRadio);
        r2.add(sectionRadio);
        JPanel r3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        r3.add(detailLabel);
        r3.add(detailField);
        r3.add(startButton);
        step1.add(r1);
        step1.add(r2);
        step1.add(r3);

        // ---------- step 2 panel
        JPanel step2 = new JPanel(new BorderLayout(5, 5));
        step2.setBorder(BorderFactory.createTitledBorder("Step 2: type each student's attendance ID"));
        JPanel idRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        idRow.add(new JLabel("Attendance ID:"));
        idRow.add(idField);
        idRow.add(markButton);
        idRow.add(countLabel);
        JPanel step2Top = new JPanel(new GridLayout(2, 1));
        step2Top.add(sessionLabel);
        step2Top.add(idRow);
        log.setEditable(false);
        log.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JPanel endRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        endRow.add(endButton);
        step2.add(step2Top, BorderLayout.NORTH);
        step2.add(new JScrollPane(log), BorderLayout.CENTER);
        step2.add(endRow, BorderLayout.SOUTH);

        JPanel attendanceCard = new JPanel(new BorderLayout(5, 5));
        attendanceCard.add(step1, BorderLayout.NORTH);
        attendanceCard.add(step2, BorderLayout.CENTER);

        reportsPanel = new ReportsPanel(teacher);
        center.add(attendanceCard, "attendance");
        center.add(reportsPanel, "reports");

        add(top, BorderLayout.NORTH);
        JPanel west = new JPanel(new BorderLayout());   // keeps the buttons at the top
        west.add(menu, BorderLayout.NORTH);
        add(west, BorderLayout.WEST);
        add(center, BorderLayout.CENTER);

        // ---------- listeners
        attendanceButton.addActionListener(this);
        reportsButton.addActionListener(this);
        logoutButton.addActionListener(this);
        startButton.addActionListener(this);
        markButton.addActionListener(this);
        idField.addActionListener(this);           // Enter in the ID field = Mark Present
        endButton.addActionListener(this);
        lectureRadio.addItemListener(this);
        labRadio.addItemListener(this);
        sectionRadio.addItemListener(this);

        for (Course c : teacher.getCourses()) {
            courseBox.addItem(c);
        }
        setSessionRunning(false);

        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /** Enables step 1 or step 2 depending on whether a session is running. */
    private void setSessionRunning(boolean running) {
        courseBox.setEnabled(!running);
        lectureRadio.setEnabled(!running);
        labRadio.setEnabled(!running);
        sectionRadio.setEnabled(!running);
        detailField.setEnabled(!running);
        startButton.setEnabled(!running);
        idField.setEnabled(running);
        markButton.setEnabled(running);
        endButton.setEnabled(running);
        logoutButton.setEnabled(!running);
    }

    private String selectedType() {
        if (labRadio.isSelected()) {
            return "Lab";
        } else if (sectionRadio.isSelected()) {
            return "Section";
        }
        return "Lecture";
    }

    /** The label of the detail field changes with the selected type (Lecture 8 - ItemListener). */
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (lectureRadio.isSelected()) {
            detailLabel.setText("Topic:");
        } else if (labRadio.isSelected()) {
            detailLabel.setText("Experiment:");
        } else {
            detailLabel.setText("Group number:");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        try {
            if (src == attendanceButton) {
                cards.show(center, "attendance");
            } else if (src == reportsButton) {
                reportsPanel.refresh();
                cards.show(center, "reports");
            } else if (src == logoutButton) {
                dispose();
                new LoginFrame();
            } else if (src == startButton) {
                startSession();
            } else if (src == markButton || src == idField) {
                markStudent();
            } else if (src == endButton) {
                endSession();
            }
        } catch (InvalidDataException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            idField.selectAll();
            idField.requestFocus();
        }
    }

    private void startSession() throws InvalidDataException {
        Course c = (Course) courseBox.getSelectedItem();
        if (c == null) {
            throw new InvalidDataException("You have no courses. Ask the admin to assign you a course.");
        }
        if (detailField.getText().trim().isEmpty()) {
            throw new InvalidDataException("Please write the " + detailLabel.getText().replace(":", "").toLowerCase() + ".");
        }
        if (detailField.getText().contains(";") || detailField.getText().contains(",")) {
            throw new InvalidDataException("The characters ; and , are not allowed.");
        }
        currentSession = teacher.startSession(c, selectedType(), detailField.getText().trim());
        sessionLabel.setText("Running: " + c.getCode() + "  " + currentSession);
        log.setText("");
        updateCount();
        setSessionRunning(true);
        idField.requestFocus();
    }

    private void markStudent() throws InvalidDataException {
        int id;
        try {
            id = Integer.parseInt(idField.getText().trim());
        } catch (NumberFormatException ex) {
            throw new InvalidDataException("The attendance ID must be a 4-digit number.");
        }
        Student s = teacher.markAttendance(currentSession, id);
        log.append(s.getId() + "  " + s.getName() + "  - Present\n");
        idField.setText("");
        idField.requestFocus();
        updateCount();
    }

    private void endSession() {
        int answer = JOptionPane.showConfirmDialog(this,
                "End the session? Students not marked will be saved as absent.", "End Session",
                JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        teacher.endSession(currentSession);
        log.setText(AttendanceReport.sessionReport(currentSession));
        sessionLabel.setText("Session ended and saved.");
        countLabel.setText(" ");
        detailField.setText("");
        currentSession = null;
        setSessionRunning(false);
    }

    private void updateCount() {
        int enrolled = currentSession.getCourse().getStudents().size();
        countLabel.setText("   Present: " + currentSession.countPresent() + " / " + enrolled);
    }
}
