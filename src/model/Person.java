package model;

/**
 * Person is the abstract parent of Admin, Teacher and Student.
 * It holds the data that every person in the system has (name and email).
 * Each subclass has its own ID with a different number of digits,
 * so getId() and getRole() are abstract (Lecture 5 - Abstraction).
 */
public abstract class Person {

    private String name;
    private String email;

    public Person(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /** Every subclass returns its own ID (6, 5 or 4 digits). */
    public abstract int getId();

    /** Returns "Admin", "Teacher" or "Student". */
    public abstract String getRole();

    /** Used by JList / JComboBox to show the person (Lecture 2 - Object class). */
    @Override
    public String toString() {
        return getId() + " - " + name;
    }
}
