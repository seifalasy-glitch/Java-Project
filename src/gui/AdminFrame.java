package gui;

import model.Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main window of the Admin.
 * On the left there is a menu of buttons; the center uses a CardLayout
 * to show one panel at a time: Students, Teachers, Courses or Reports.
 */
public class AdminFrame extends JFrame implements ActionListener {

    private Admin admin;

    private JButton studentsButton = new JButton("Students");
    private JButton teachersButton = new JButton("Teachers");
    private JButton coursesButton = new JButton("Courses");
    private JButton reportsButton = new JButton("Reports");
    private JButton logoutButton = new JButton("Logout");

    private CardLayout cards = new CardLayout();
    private JPanel center = new JPanel(cards);

    private StudentsPanel studentsPanel;
    private TeachersPanel teachersPanel;
    private CoursesPanel coursesPanel;
    private ReportsPanel reportsPanel;

    public AdminFrame(Admin admin) {
        this.admin = admin;
        setTitle("Admin - " + admin.getName());

        JLabel welcome = new JLabel("  Welcome " + admin.getName() + "   (Admin ID: " + admin.getId() + ")");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 15));
        JPanel top = new JPanel(new BorderLayout());
        top.add(welcome, BorderLayout.CENTER);
        top.add(logoutButton, BorderLayout.EAST);
        top.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel menu = new JPanel(new GridLayout(0, 1, 5, 5));   // one column
        menu.add(studentsButton);
        menu.add(teachersButton);
        menu.add(coursesButton);
        menu.add(reportsButton);
        menu.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        studentsPanel = new StudentsPanel(admin);
        teachersPanel = new TeachersPanel(admin);
        coursesPanel = new CoursesPanel(admin);
        reportsPanel = new ReportsPanel(null);     // null = all courses
        center.add(studentsPanel, "students");
        center.add(teachersPanel, "teachers");
        center.add(coursesPanel, "courses");
        center.add(reportsPanel, "reports");

        add(top, BorderLayout.NORTH);
        JPanel west = new JPanel(new BorderLayout());   // keeps the buttons at the top
        west.add(menu, BorderLayout.NORTH);
        add(west, BorderLayout.WEST);
        add(center, BorderLayout.CENTER);

        studentsButton.addActionListener(this);
        teachersButton.addActionListener(this);
        coursesButton.addActionListener(this);
        reportsButton.addActionListener(this);
        logoutButton.addActionListener(this);

        setSize(900, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == studentsButton) {
            studentsPanel.refresh();
            cards.show(center, "students");
        } else if (src == teachersButton) {
            teachersPanel.refresh();
            cards.show(center, "teachers");
        } else if (src == coursesButton) {
            coursesPanel.refresh();
            cards.show(center, "courses");
        } else if (src == reportsButton) {
            reportsPanel.refresh();
            cards.show(center, "reports");
        } else if (src == logoutButton) {
            dispose();
            new LoginFrame();
        }
    }
}
