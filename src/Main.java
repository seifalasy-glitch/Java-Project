import data.DataStore;
import gui.LoginFrame;

/**
 * Starting point of the Attendance Management System.
 * 1) loads the data from the text files,  2) opens the login window.
 */
public class Main {

    public static void main(String[] args) {
        DataStore.load();
        new LoginFrame();
    }
}
