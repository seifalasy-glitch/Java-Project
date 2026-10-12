package gui;

import data.DataStore;
import model.Admin;
import model.InvalidDataException;
import model.Student;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

/** Admin panel to add, list and delete students. */
public class StudentsPanel extends JPanel implements ActionListener {

    private Admin admin;
    private JTextField nameField = new JTextField(14);
    private JTextField emailField = new JTextField(14);
    private JButton addButton = new JButton("Add Student");
    private JButton deleteButton = new JButton("Delete Selected");
    private JList<Student> list = new JList<Student>();
    private JLabel countLabel = new JLabel();

    public StudentsPanel(Admin admin) {
        this.admin = admin;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Students  (ID = 4-digit attendance ID)"));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(addButton);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(deleteButton);
        bottom.add(countLabel);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(list), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        addButton.addActionListener(this);
        deleteButton.addActionListener(this);
        refresh();
    }

    /** Reloads the list from the DataStore, sorted by name. */
    public void refresh() {
        ArrayList<Student> students = new ArrayList<Student>(DataStore.getStudents());
        Collections.sort(students);
        list.setListData(students.toArray(new Student[0]));
        countLabel.setText("   Total: " + students.size());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            try {
                Student s = admin.createStudent(nameField.getText(), emailField.getText());
                JOptionPane.showMessageDialog(this, "Student added.\nAttendance ID: " + s.getId());
                nameField.setText("");
                emailField.setText("");
                refresh();
            } catch (InvalidDataException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == deleteButton) {
            Student s = list.getSelectedValue();
            if (s == null) {
                JOptionPane.showMessageDialog(this, "Select a student first.");
                return;
            }
            int answer = JOptionPane.showConfirmDialog(this, "Delete " + s.getName() + "?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                admin.removeStudent(s);
                refresh();
            }
        }
    }
}
