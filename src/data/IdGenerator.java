package data;

import java.util.ArrayList;
import java.util.Random;

/**
 * Generates the random unique IDs:
 *   Student  -> 4 digits (1000 - 9999)
 *   Teacher  -> 5 digits (10000 - 99999)
 *   Admin    -> 6 digits (100000 - 999999)
 * Every ID that is created or loaded from the files is kept in usedIds,
 * so the same number is never given twice.
 */
public class IdGenerator {

    private static Random random = new Random();
    private static ArrayList<Integer> usedIds = new ArrayList<Integer>();

    public static int newStudentId() {
        return generate(1000, 9999);
    }

    public static int newEmployeeId() {
        return generate(10000, 99999);
    }

    public static int newAdminId() {
        return generate(100000, 999999);
    }

    /** Remembers an ID that was loaded from a file (called by DataStore.load). */
    public static void register(int id) {
        if (!usedIds.contains(id)) {
            usedIds.add(id);
        }
    }

    /** Keeps choosing a random number in [min, max] until it finds one not used before. */
    private static int generate(int min, int max) {
        int id;
        do {
            id = min + random.nextInt(max - min + 1);
        } while (usedIds.contains(id));
        usedIds.add(id);
        return id;
    }
}
