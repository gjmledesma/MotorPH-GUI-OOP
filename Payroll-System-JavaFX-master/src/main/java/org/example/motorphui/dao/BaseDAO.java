package org.example.motorphui.dao;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all DAO implementations.
 *
 * Centralises the repeated file-resolution and buffered-reader/writer boilerplate
 * that was previously duplicated across every DAO class.
 *
 * OOP PRINCIPLES DEMONSTRATED:
 *   ABSTRACTION   — Provides a reusable template; concrete DAOs extend this
 *                   class and call the protected helpers rather than repeating
 *                   raw I/O code.
 *   ENCAPSULATION — All helpers are protected; callers outside the DAO layer
 *                   cannot access them directly.
 *   INHERITANCE   — All concrete DAOs (AuthenticationDAO, EmployeeDAOImpl,
 *                   AttendanceDAOImpl, LeaveRequestDAO, etc.) extend BaseDAO.
 */
public abstract class BaseDAO {

    // ── Protected helpers ─────────────────────────────────────────────────────

    /**
     * Resolves a classpath resource path to a writable on-disk {@link File}.
     * Returns null (and prints an error) if the resource cannot be resolved
     * to a real file (e.g. when running inside a JAR).
     *
     * @param resourcePath classpath path, e.g. "/org/example/motorphui/data/foo.csv"
     */
    protected File resolveFile(String resourcePath) {
        try {
            URL url = getClass().getResource(resourcePath);
            if (url != null && "file".equals(url.getProtocol())) {
                return new File(url.toURI());
            }
        } catch (Exception e) {
            System.err.println("[BaseDAO] Cannot resolve " + resourcePath + ": " + e.getMessage());
        }
        System.err.println("[BaseDAO] Resource is not a plain file (may be inside a JAR): " + resourcePath);
        return null;
    }

    /**
     * Opens a {@link BufferedReader} for a classpath resource.
     * Falls back to {@link #resolveFile} when the resource stream is null.
     *
     * @param resourcePath classpath path to the CSV file
     * @return an open BufferedReader, or null if the resource cannot be found
     */
    protected BufferedReader openReader(String resourcePath) {
        InputStream is = getClass().getResourceAsStream(resourcePath);
        if (is == null) {
            File f = resolveFile(resourcePath);
            if (f != null && f.exists()) {
                try { is = new FileInputStream(f); }
                catch (FileNotFoundException ignored) {}
            }
        }
        if (is == null) {
            System.err.println("[BaseDAO] CSV not found: " + resourcePath);
            return null;
        }
        return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
    }

    /**
     * Appends a single data row to the CSV file, creating the file and writing
     * the provided header first if the file does not yet exist or is empty.
     *
     * @param resourcePath classpath path to the target CSV
     * @param csvHeader    header row to write if creating the file
     * @param csvRow       data row to append
     */
    protected void appendRow(String resourcePath, String csvHeader, String csvRow) {
        File file = resolveFile(resourcePath);
        if (file == null) return;
        boolean needsHeader = !file.exists() || file.length() == 0;
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(file, StandardCharsets.UTF_8, true))) {
            if (needsHeader) {
                writer.write(csvHeader);
                writer.newLine();
            }
            writer.write(csvRow);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("[BaseDAO] appendRow error: " + e.getMessage());
        }
    }

    /**
     * Rewrites the entire CSV file from the provided lines list.
     * The first element of {@code lines} must be the header row.
     *
     * @param resourcePath classpath path to the target CSV
     * @param lines        all rows to write (header + data)
     */
    protected void rewriteFile(String resourcePath, List<String> lines) {
        File file = resolveFile(resourcePath);
        if (file == null) return;
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(file, StandardCharsets.UTF_8, false))) {
            for (int i = 0; i < lines.size(); i++) {
                writer.write(lines.get(i));
                if (i < lines.size() - 1) writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("[BaseDAO] rewriteFile error: " + e.getMessage());
        }
    }

    /**
     * Reads all non-blank lines from a CSV resource, skipping the header.
     *
     * @param resourcePath classpath path to the CSV file
     * @return list of data lines (header excluded), never null
     */
    protected List<String> readDataLines(String resourcePath) {
        List<String> result = new ArrayList<>();
        try (BufferedReader reader = openReader(resourcePath)) {
            if (reader == null) return result;
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                if (isHeader) { isHeader = false; continue; }
                result.add(line);
            }
        } catch (IOException e) {
            System.err.println("[BaseDAO] readDataLines error: " + e.getMessage());
        }
        return result;
    }
}
