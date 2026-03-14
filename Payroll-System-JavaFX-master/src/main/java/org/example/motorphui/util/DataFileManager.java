package org.example.motorphui.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/**
 * Manages all CSV file I/O exclusively within the project's local resources
 * directory at:
 *
 *   <project-root>/src/main/resources/org/example/motorphui/data/
 *
 * All CRUD operations (reads, writes, appends) are performed directly on
 * these source files.  No external directory (e.g. ~/.motorphui/) is created
 * or used.
 *
 */
public final class DataFileManager {

    /**
     * Absolute path to the local resources data folder.
     * Resolved at startup from the JVM working directory, which is expected
     * to be the project root when the app is launched from an IDE or from
     * a run-script in the repo root.
     */
    private static final String DATA_DIR =
            System.getProperty("user.dir")
            + File.separator + "src"
            + File.separator + "main"
            + File.separator + "resources"
            + File.separator + "org"
            + File.separator + "example"
            + File.separator + "motorphui"
            + File.separator + "data";

    /**
     * Transactional files that must exist with at least a header row.
     * These are created automatically if they are missing from the repo.
     */
    private static final String[][] TRANSACTIONAL_HEADERS = {
            { "motorph_ticket_requests.csv",
              "Ticket ID,Employee ID,Last Name,First Name,Category,Subject,Description,Date Filed,Status,IT Remarks" },
            { "motorph_leave_records.csv",
              "Leave ID,Last Name,First Name,Start Date,End Date,Days,Leave Type,Reason,Approved?" },
            { "motorph_attendance_records.csv",
              "Employee #,Last Name,First Name,Date,Log In,Log Out" }
    };

    static { initialise(); }

    private DataFileManager() {}

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Returns a writable {@link File} pointing to {@code filename} inside
     * the local resources data directory.
     *
     * <p>Guarantees the parent directory exists before returning, so
     * {@link FileWriter} never encounters a missing parent.</p>
     *
     * @param filename the bare CSV filename, e.g. {@code "motorph_leave_records.csv"}
     * @return a {@link File} ready for reading or writing
     */
    public static File resolveFile(String filename) {
        try {
            Files.createDirectories(Path.of(DATA_DIR));
        } catch (IOException e) {
            System.err.println("[DataFileManager] Cannot create data dir: " + e.getMessage());
        }
        return new File(DATA_DIR, filename);
    }

    /**
     * Opens a {@link BufferedReader} for {@code filename} from the local
     * resources data directory.
     *
     * @param filename the bare CSV filename
     * @return a {@link BufferedReader}, or {@code null} if the file is not found
     */
    public static BufferedReader openReader(String filename) {
        File f = resolveFile(filename);
        if (!f.exists() || f.length() == 0) {
            System.err.println("[DataFileManager] File not found or empty: " + f.getAbsolutePath());
            return null;
        }
        try {
            return new BufferedReader(
                    new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("[DataFileManager] Cannot open " + filename
                    + ": " + e.getMessage());
            return null;
        }
    }

    // ── Startup ────────────────────────────────────────────────────────────────

    /**
     * Called once at class-load time.
     * Ensures the data directory exists and that all transactional CSV files
     * have at least a header row.  Master CSVs (employee data, credentials)
     * are assumed to already exist in the repo and are never auto-created.
     */
    private static void initialise() {
        try {
            Files.createDirectories(Path.of(DATA_DIR));
        } catch (IOException e) {
            System.err.println("[DataFileManager] Cannot create data dir: " + e.getMessage());
            return;
        }

        for (String[] entry : TRANSACTIONAL_HEADERS) {
            seedTransactionalIfAbsent(entry[0], entry[1]);
        }

        System.out.println("[DataFileManager] Data directory: " + DATA_DIR);
    }

    /**
     * Creates a header-only transactional CSV file inside the local resources
     * directory if it does not already exist.
     */
    private static void seedTransactionalIfAbsent(String filename, String csvHeader) {
        File target = new File(DATA_DIR, filename);
        if (target.exists()) return;
        try (BufferedWriter w = new BufferedWriter(
                new FileWriter(target, StandardCharsets.UTF_8, false))) {
            w.write(csvHeader);
            w.newLine();
            System.out.println("[DataFileManager] Created transactional: " + filename);
        } catch (IOException e) {
            System.err.println("[DataFileManager] Failed to create " + filename
                    + ": " + e.getMessage());
        }
    }
}