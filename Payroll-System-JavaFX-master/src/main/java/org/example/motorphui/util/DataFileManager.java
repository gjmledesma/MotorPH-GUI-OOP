package org.example.motorphui.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/**
 * Manages a persistent, writable data directory for all CSV files.
 *
 * ROOT CAUSE FIXED:
 *   Using getClass().getResource() to write CSV files resolves to the Gradle
 *   build output directory (build/resources/main/).  Any data written there is
 *   wiped the next time Gradle compiles or syncs the project.  When running on
 *   the JavaFX module path the URL protocol may not be "file" at all, causing
 *   resolveFile() to return null and all writes to silently fail.
 *
 * SOLUTION:
 *   On first access, this class copies every bundled CSV from the classpath
 *   into a stable folder in the user's home directory (~/.motorphui/data/).
 *   All subsequent reads AND writes target that external directory, so data
 *   persists across builds, IDE syncs, and application restarts.
 *
 * USAGE (via BaseDAO helpers — callers never touch this class directly):
 *   File f  = DataFileManager.resolveFile("motorph_employee_data.csv");
 *   BufferedReader r = DataFileManager.openReader("motorph_employee_data.csv");
 */
public final class DataFileManager {

    /** Writable home directory for all application data files. */
    private static final String APP_DATA_DIR =
            System.getProperty("user.home") + File.separator
            + ".motorphui" + File.separator + "data";

    /** Classpath prefix where the bundled (seed) CSV files live. */
    private static final String CLASSPATH_DATA = "/org/example/motorphui/data/";

    /** All CSV files the application uses — seeded from classpath on first run. */
    private static final String[] MANAGED_FILES = {
            "motorph_employee_data.csv",
            "motorph_employee_credentials.csv",
            "motorph_hr_credentials.csv",
            "motorph_finance_credentials.csv",
            "motorph_it_credentials.csv",
            "motorph_attendance_records.csv",
            "motorph_leave_records.csv",
            "motorph_ticket_requests.csv"
    };

    static {
        // Initialise the data directory and seed any missing files as soon as
        // the class is first loaded.
        initialise();
    }

    private DataFileManager() {}

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Returns a writable {@link File} for the given CSV filename.
     * The file is guaranteed to exist (seeded from classpath if needed).
     *
     * @param filename e.g. "motorph_employee_data.csv"
     */
    public static File resolveFile(String filename) {
        ensureSeeded(filename);
        return new File(APP_DATA_DIR, filename);
    }

    /**
     * Opens a {@link BufferedReader} for the given CSV filename,
     * reading from the external data directory.
     *
     * @param filename e.g. "motorph_employee_data.csv"
     * @return an open reader, or null if the file cannot be found
     */
    public static BufferedReader openReader(String filename) {
        File f = resolveFile(filename);
        if (f.exists()) {
            try {
                return new BufferedReader(
                        new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8));
            } catch (IOException e) {
                System.err.println("[DataFileManager] Cannot open reader for " + filename
                        + ": " + e.getMessage());
            }
        }
        // Last-resort fallback — read directly from the classpath (read-only)
        InputStream is = DataFileManager.class.getResourceAsStream(CLASSPATH_DATA + filename);
        if (is != null) {
            System.err.println("[DataFileManager] WARNING: reading " + filename
                    + " from classpath (changes will not persist).");
            return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        }
        System.err.println("[DataFileManager] CSV not found: " + filename);
        return null;
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    /** Creates the data directory and seeds all managed files on first run. */
    private static void initialise() {
        File dir = new File(APP_DATA_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            System.err.println("[DataFileManager] Could not create data directory: " + APP_DATA_DIR);
            return;
        }
        for (String filename : MANAGED_FILES) {
            seedIfAbsent(filename, dir);
        }
    }

    /** Seeds a single file from the classpath if it is not yet present externally. */
    private static void ensureSeeded(String filename) {
        File target = new File(APP_DATA_DIR, filename);
        if (!target.exists()) {
            seedIfAbsent(filename, new File(APP_DATA_DIR));
        }
    }

    /**
     * Copies {@code filename} from the classpath resource bundle into
     * {@code dir} if no file by that name exists there yet.
     */
    private static void seedIfAbsent(String filename, File dir) {
        File target = new File(dir, filename);
        if (target.exists()) return;

        String resourcePath = CLASSPATH_DATA + filename;
        try (InputStream in = DataFileManager.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                System.err.println("[DataFileManager] Classpath resource not found: " + resourcePath);
                return;
            }
            Files.copy(in, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("[DataFileManager] Seeded " + filename + " → " + target.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("[DataFileManager] Failed to seed " + filename + ": " + e.getMessage());
        }
    }
}
