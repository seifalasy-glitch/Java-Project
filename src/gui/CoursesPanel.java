package gui;

import data.DataStore;
import model.Admin;
import model.Course;
import model.InvalidDataException;
import model.Student;
import model.Teacher;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Admin panel for courses:
 *  - create a course and choose its teacher
 *  - change the teacher of a course
 *  - enroll / remove students
 * When a course is selected in the left list, its students appear in the right list
 * (ListSelectionListener, Lecture 8).
 */
public class CoursesPanel extends JPanel implements ActionListener, ListSelectionListener {

    private Admin admin;

    private JTextField codeField = new JTextField(8);
    private JTextField nameField = new JTextField(16);
    private JComboBox<Teacher> teacherBox = new JComboBox<Teacher>();
    private JButton addButton = new JButton("Add Course");

    private JList<Course> courseList = new JList<Course>();
    private JList<Student> enrolledList = new JList<Student>();
    private JLabel teacherLabel = new JLabel("Teacher: -");

    private JComboBox<Student> studentBox = new JComboBox<Student>();
    private JButton enrollButton = new JButton("Enroll Student");
    private JButton unenrollButton = new JButton("Remove Selected Student");
    private JButton assignButton = new JButton("Assign Teacher (from top list)");
    private JButton deleteButton = new JButton("Delete Course");

    public CoursesPanel(Admin admin) {
        this.admin = admin;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Courses"));

        // ---- top: new course form
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Code:"));
        form.add(codeField);
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Teacher:"));
        form.add(teacherBox);
        form.add(addButton);

        // ---- center: two lists side by side
        JPanel left = new JPanel(new BorderLayout());
        left.add(new JLabel("All courses"), BorderLayout.NORTH);
        left.add(new JScrollPane(courseList), BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout());
        right.add(teacherLabel, BorderLayout.NORTH);
        right.add(new JScrollPane(enrolledList), BorderLayout.CENTER);

        JPanel lists = new JPanel(new GridLayout(1, 2, 8, 8));
        lists.add(left);
        lists.add(right);

        // ---- bottom: actions on the selected course
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Student:"));
        row1.add(studentBox);
        row1.add(enrollButton);
        row1.add(unenrollButton);
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(assignButton);
        row2.add(deleteButton);
        JPanel bottom = new JPanel(new GridLayout(2, 1));
        bottom.add(row1);
        bottom.add(row2);

        add(form, BorderLayout.NORTH);
        add(lists, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        addButton.addActionListener(this);
        enrollButton.addActionListener(this);
        unenrollButton.addActionListener(this);
        assignButton.addActionListener(this);
        deleteButton.addActionListener(this);
        courseList.addListSelectionListener(this);

        refresh();
    }

    /** Reloads the combo boxes and the course list. */
    public void refresh() {
        Course selected = courseList.getSelectedValue();

        teacherBox.removeAllItems();
        for (Teacher t : DataStore.getTeachers()) {
            teacherBox.addItem(t);
        }
        studentBox.removeAllItems();
        for (Student s : DataStore.getStudents()) {
            studentBox.addItem(s);
        }
        courseList.setListData(DataStore.getCourses().toArray(new Course[0]));
        if (selected != null && DataStore.getCourses().contains(selected)) {
            courseList.setSelectedValue(selected, true);
        }
        showSelectedCourse();
    }

    /** Shows the teacher and the students of the selected course. */
    private void showSelectedCourse() {
        Course c = courseList.getSelectedValue();
        if (c == null) {
            teacherLabel.setText("Select a course to see its students");
            enrolledList.setListData(new Student[0]);
            return;
        }
        String teacher = (c.getTeacher() == null) ? "not assigned" : c.getTeacher().getName();
        teacherLabel.setText("Teacher: " + teacher + "   |   Students: " + c.getStudents().size());
        enrolledList.setListData(c.getStudents().toArray(new Student[0]));
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        showSelectedCourse();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        Course c = courseList.getSelectedValue();
        try {
            if (src == addButton) {
                Teacher t = (Teacher) teacherBox.getSelectedItem();
                Course created = admin.createCourse(codeField.getText(), nameField.getText(), t);
                codeField.setText("");
                nameField.setText("");
                refresh();
                courseList.setSelectedValue(created, true);
                return;
            }
            if (c == null) {
                throw new InvalidDataException("Select a course from the list first.");
            }
            if (src == enrollButton) {
                Student s = (Student) studentBox.getSelectedItem();
                if (s == null) {
                    throw new InvalidDataException("There are no students. Add students first.");
                }
                admin.enrollStudent(c, s);
            } else if (src == unenrollButton) {
                Student s = enrolledList.getSelectedValue();
                if (s == null) {
                    throw new InvalidDataException("Select a student from the right list.");
                }
                admin.unenrollStudent(c, s);
            } else if (src == assignButton) {
                Teacher t = (Teacher) teacherBox.getSelectedItem();
                if (t == null) {
                    throw new InvalidDataException("There are no teachers. Add a teacher first.");
                }
                admin.assignTeacher(c, t);
            } else if (src == deleteButton) {
                int answer = JOptionPane.showConfirmDialog(this,
                        "Delete " + c + " and all its attendance?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (answer == JOptionPane.YES_OPTION) {
                    admin.removeCourse(c);
                }
            }
            refresh();
        } catch (InvalidDataException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
