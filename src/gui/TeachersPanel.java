package gui;

import data.DataStore;
import model.Admin;
import model.InvalidDataException;
import model.Teacher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Admin panel to add, list and delete teachers. */
public class TeachersPanel extends JPanel implements ActionListener {

    private Admin admin;
    private JTextField nameField = new JTextField(12);
    private JTextField emailField = new JTextField(12);
    private JTextField passwordField = new JTextField(8);
    private JButton addButton = new JButton("Add Teacher");
    private JButton deleteButton = new JButton("Delete Selected");
    private JList<Teacher> list = new JList<Teacher>();
    private JLabel countLabel = new JLabel();

    public TeachersPanel(Admin admin) {
        this.admin = admin;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Teachers  (ID = 5-digit employee ID)"));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Password:"));
        form.add(passwordField);
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

    public void refresh() {
        list.setListData(DataStore.getTeachers().toArray(new Teacher[0]));
        countLabel.setText("   Total: " + DataStore.getTeachers().size());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            try {
                Teacher t = admin.createTeacher(nameField.getText(), emailField.getText(),
                        passwordField.getText());
                JOptionPane.showMessageDialog(this, "Teacher added.\nEmployee ID: " + t.getId()
                        + "\nHe logs in with: " + t.getEmail());
                nameField.setText("");
                emailField.setText("");
                passwordField.setText("");
                refresh();
            } catch (InvalidDataException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == deleteButton) {
            Teacher t = list.getSelectedValue();
            if (t == null) {
                JOptionPane.showMessageDialog(this, "Select a teacher first.");
                return;
            }
            int answer = JOptionPane.showConfirmDialog(this,
                    "Delete " + t.getName() + "? His courses will have no teacher.", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                admin.removeTeacher(t);
                refresh();
            }
        }
    }
}
