package gui;

import data.DataStore;
import model.Admin;
import model.Person;
import model.Teacher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * First window. The Admin or the Teacher writes his email and password.
 * Students do not log in (they only say their attendance ID to the teacher).
 * Event handling: the frame itself implements ActionListener (Lecture 8).
 */
public class LoginFrame extends JFrame implements ActionListener {

    private JTextField emailField = new JTextField(20);
    private JPasswordField passwordField = new JPasswordField(20);
    private JButton loginButton = new JButton("Login");

    public LoginFrame() {
        setTitle("Attendance Management System - Login");

        JLabel title = new JLabel("Attendance Management System", JLabel.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));

        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Password:"));
        form.add(passwordField);
        form.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel bottom = new JPanel(new GridLayout(2, 1));
        JPanel buttonRow = new JPanel(new FlowLayout());
        buttonRow.add(loginButton);
        bottom.add(buttonRow);
        JLabel hint = new JLabel("Admin and teachers only.", JLabel.CENTER);
        hint.setForeground(Color.GRAY);
        bottom.add(hint);

        add(title, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        loginButton.addActionListener(this);
        passwordField.addActionListener(this);   // pressing Enter also logs in

        setSize(420, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        Person user = DataStore.login(email, password);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Wrong email or password.", "Login failed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (user instanceof Admin) {
            new AdminFrame((Admin) user);
        } else {
            new TeacherFrame((Teacher) user);
        }
        dispose();   // close the login window
    }
}
