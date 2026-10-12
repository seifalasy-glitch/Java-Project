package model;

/**
 * Custom checked exception (Lecture 6 - Custom Exceptions).
 * Thrown whenever the user enters something wrong, for example:
 * an attendance ID that does not exist, a student marked twice,
 * an email that is already used, an empty name ...
 * The GUI catches it and shows its message to the user.
 */
public class InvalidDataException extends Exception {

    public InvalidDataException(String message) {
        super(message);
    }
}
