package org.example.motorphui.dao;

import org.example.motorphui.model.AttendanceRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * DAO for employee-facing attendance operations:
 *   - load records filtered to one employee
 *   - write a new Time-In row
 *   - update an existing row's Log-Out value
 *
 * The CSV is expected at the classpath path below, which resolves to a real
 * file on disk when running from an IDE / Gradle run task.  In a deployed
 * fat-JAR you would instead copy the CSV to a writable directory (e.g. the
 * user's home folder) on first launch and point CSV_RESOURCE_PATH there.
 */
public class EmployeeAttendanceDAO {

    // ── Paths ────────────────────────────────────────────────────────────────

    /** Classpath location — used for reading. */
    private static final String CSV_RESOURCE_PATH =
            "/org/example/motorphui/data/motorph_attendance_records.csv";

    /** CSV date/time formats that match the existing data. */
    private static final DateTimeFormatter DATE_FMT  =
            DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter TIME_FMT  =
            DateTimeFormatter.ofPattern("H:mm");          // e.g. 8:05 or 14:30

    // ── Public API ───────────────────────────────────────────────────────────

    /**
     * Returns all rows whose Employee-# column matches {@code empId},
     * ordered as they appear in the CSV (most recent is usually at the bottom).
     */
    public ObservableList<AttendanceRecord> getRecordsForEmployee(String empId) {
        ObservableList<AttendanceRecord> list = FXCollections.observableArrayList();

        try (BufferedReader reader = openReader()) {
            if (reader == null) return list;

            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                if (isHeader) { isHeader = false; continue; }

                String[] f = line.split(",", 6);
                if (f.length < 6) continue;
                if (f[0].trim().equals(empId.trim())) {
                    list.add(new AttendanceRecord(
                            f[0].trim(), f[1].trim(), f[2].trim(),
                            f[3].trim(), f[4].trim(), f[5].trim()));
                }
            }
        } catch (IOException e) {
            System.err.println("[EmployeeAttendanceDAO] Read error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Checks whether the employee has already timed-in today.
     * Returns the matching row (so the controller can check if log-out is missing),
     * or {@code null} if no row exists for today.
     */
    public AttendanceRecord getTodayRecord(String empId) {
        String today = LocalDate.now().format(DATE_FMT);
        try (BufferedReader reader = openReader()) {
            if (reader == null) return null;

            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                if (isHeader) { isHeader = false; continue; }

                String[] f = line.split(",", 6);
                if (f.length < 6) continue;
                if (f[0].trim().equals(empId) && f[3].trim().equals(today)) {
                    return new AttendanceRecord(
                            f[0].trim(), f[1].trim(), f[2].trim(),
                            f[3].trim(), f[4].trim(), f[5].trim());
                }
            }
        } catch (IOException e) {
            System.err.println("[EmployeeAttendanceDAO] Read error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Appends a new Time-In row for the employee with an empty Log-Out field.
     *
     * @return the created record, or {@code null} on failure.
     */
    public AttendanceRecord writeTimeIn(String empId, String lastName, String firstName) {
        File csv = resolveFile();
        if (csv == null) return null;

        String today   = LocalDate.now().format(DATE_FMT);
        String timeNow = LocalTime.now().format(TIME_FMT);

        // Row format: empId,lastName,firstName,date,logIn,logOut(empty)
        String newRow = String.join(",", empId, lastName, firstName, today, timeNow, "");

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(csv, StandardCharsets.UTF_8, true))) {
            writer.newLine();          // ensure we start on a fresh line
            writer.write(newRow);
        } catch (IOException e) {
            System.err.println("[EmployeeAttendanceDAO] Write error (time-in): " + e.getMessage());
            return null;
        }

        return new AttendanceRecord(empId, lastName, firstName, today, timeNow, "");
    }

    /**
     * Finds the employee's row for today and fills in the Log-Out column.
     * Rewrites the entire CSV so the in-place edit is safe.
     *
     * @return the updated record, or {@code null} on failure.
     */
    public AttendanceRecord writeTimeOut(String empId) {
        File csv = resolveFile();
        if (csv == null) return null;

        String today   = LocalDate.now().format(DATE_FMT);
        String timeNow = LocalTime.now().format(TIME_FMT);

        // Read all lines, patch the matching row, rewrite the file
        java.util.List<String> lines    = new java.util.ArrayList<>();
        AttendanceRecord       patched  = null;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(csv), StandardCharsets.UTF_8))) {

            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    lines.add(line);
                    isHeader = false;
                    continue;
                }

                String[] f = line.split(",", 6);
                // Match: same employee, today's date, logout column is blank
                if (f.length >= 6
                        && f[0].trim().equals(empId)
                        && f[3].trim().equals(today)
                        && f[5].trim().isEmpty()
                        && patched == null) {       // only patch the first match

                    f[5]   = timeNow;
                    line   = String.join(",", f);
                    patched = new AttendanceRecord(
                            f[0].trim(), f[1].trim(), f[2].trim(),
                            f[3].trim(), f[4].trim(), f[5].trim());
                }
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("[EmployeeAttendanceDAO] Read error (time-out): " + e.getMessage());
            return null;
        }

        if (patched == null) {
            System.err.println("[EmployeeAttendanceDAO] No open time-in found for today.");
            return null;
        }

        // Rewrite
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(csv, StandardCharsets.UTF_8, false))) {
            for (int i = 0; i < lines.size(); i++) {
                writer.write(lines.get(i));
                if (i < lines.size() - 1) writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("[EmployeeAttendanceDAO] Write error (time-out): " + e.getMessage());
            return null;
        }

        return patched;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /** Opens a BufferedReader for the CSV resource (read-only). */
    private BufferedReader openReader() {
        InputStream is = getClass().getResourceAsStream(CSV_RESOURCE_PATH);
        if (is == null) {
            // Fallback: try resolving via URL so it works from the file system too
            File f = resolveFile();
            if (f != null && f.exists()) {
                try { is = new FileInputStream(f); }
                catch (FileNotFoundException ignored) {}
            }
        }
        if (is == null) {
            System.err.println("[EmployeeAttendanceDAO] CSV not found: " + CSV_RESOURCE_PATH);
            return null;
        }
        return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
    }

    /**
     * Resolves the CSV to a real {@link File} so we can write to it.
     * Works when running from an IDE or Gradle; in a packaged JAR you should
     * pre-copy the CSV to a writable location (e.g. user home) and adjust this method.
     */
    private File resolveFile() {
        try {
            URL url = getClass().getResource(CSV_RESOURCE_PATH);
            if (url != null && "file".equals(url.getProtocol())) {
                return new File(url.toURI());
            }
        } catch (Exception e) {
            System.err.println("[EmployeeAttendanceDAO] Cannot resolve CSV as file: " + e.getMessage());
        }
        System.err.println("[EmployeeAttendanceDAO] Write unavailable – CSV is inside a JAR. "
                + "Copy the CSV to a writable directory and update CSV_RESOURCE_PATH.");
        return null;
    }
}
