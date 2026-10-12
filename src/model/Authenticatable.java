package model;

/**
 * Interface for the users that are allowed to log in (Admin and Teacher only).
 * A Student does not implement it, so a student can never log in.
 * (Lecture 5 - Interfaces)
 */
public interface Authenticatable {

    /** Returns true if the given email and password belong to this user. */
    boolean login(String email, String password);
}
