package org.example.motorphui.dao;

import org.example.motorphui.util.DataFileManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDAO {

    // ── Private utility ────────────────────────────────────────────────────────

    /** Extracts the bare filename from a full classpath resource path.
     *  "/org/example/motorphui/data/foo.csv" → "foo.csv" */
    private static String filenameFrom(String resourcePath) {
        return resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
    }

    // ── Protected helpers ─────────────────────────────────────────────────────

    /**
     * Resolves a classpath resource path to a writable on-disk {@link File}
     * inside the persistent external data directory (~/.motorphui/data/).
     * Never returns null — DataFileManager guarantees the directory exists.
     */
    protected File resolveFile(String resourcePath) {
        return DataFileManager.resolveFile(filenameFrom(resourcePath));
    }

    /**
     * Opens a {@link BufferedReader} for a CSV file, reading from the
     * persistent external data directory via DataFileManager.
     */
    protected BufferedReader openReader(String resourcePath) {
        return DataFileManager.openReader(filenameFrom(resourcePath));
    }

    /**
     * Appends a single data row to the CSV file.
     *
     * <p>If the file does not yet exist or is empty the header row is written
     * first.  If the file already has content a newline is written before the
     * new row to guard against CSV files that do not end with one — this was
     * the root cause of new employees being concatenated onto the last row.
     * Extra blank lines produced by this guard are silently skipped by
     * {@link #readDataLines}.
     *
     * @param resourcePath classpath path to the target CSV (used to derive filename)
     * @param csvHeader    header row written only when creating a new file
     * @param csvRow       data row to append
     */
    protected void appendRow(String resourcePath, String csvHeader, String csvRow) {
        File file = resolveFile(resourcePath);
        boolean isEmpty = !file.exists() || file.length() == 0;

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(file, StandardCharsets.UTF_8, true))) {

            if (isEmpty) {
                // New file: write header first, then the data row.
                writer.write(csvHeader);
                writer.newLine();
            } else {
                // Existing file: always write a leading newline before the new
                // row.  If the file already ends with '\n' this produces one
                // harmless blank line; if it does not end with '\n' (common
                // with Excel/Notepad exports) this prevents row concatenation.
                // readDataLines() skips blank lines so either case is safe.
                writer.newLine();
            }

            writer.write(csvRow);
            writer.newLine();

        } catch (IOException e) {
            System.err.println("[BaseDAO] appendRow error for " + file.getName()
                    + ": " + e.getMessage());
        }
    }

    /**
     * Rewrites the entire CSV file from the provided lines list.
     * The first element of {@code lines} must be the header row.
     */
    protected void rewriteFile(String resourcePath, List<String> lines) {
        File file = resolveFile(resourcePath);
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(file, StandardCharsets.UTF_8, false))) {
            for (int i = 0; i < lines.size(); i++) {
                writer.write(lines.get(i));
                if (i < lines.size() - 1) writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("[BaseDAO] rewriteFile error for " + file.getName()
                    + ": " + e.getMessage());
        }
    }

    /**
     * Reads all non-blank lines from a CSV, skipping the header row.
     * Returns an empty list (never null) on any failure.
     */
    protected List<String> readDataLines(String resourcePath) {
        List<String> result = new ArrayList<>();
        try (BufferedReader reader = openReader(resourcePath)) {
            if (reader == null) return result;
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;          // skip blank/guard lines
                if (isHeader) { isHeader = false; continue; }
                result.add(line);
            }
        } catch (IOException e) {
            System.err.println("[BaseDAO] readDataLines error: " + e.getMessage());
        }
        return result;
    }
}
