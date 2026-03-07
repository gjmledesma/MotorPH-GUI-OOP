package org.example.motorphui.dao;

import org.example.motorphui.model.AttendanceRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Data-access object that reads attendance records from the bundled CSV file.
 *
 * Expected CSV format (header row present):
 *   Employee #, Last Name, First Name, Date, Log In, Log Out
 *
 * Place the CSV at:
 *   src/main/resources/data/motorph_attendance_records.csv
 */
public class NewAttendanceDAO {

    // Path relative to the classpath root
    private static final String CSV_PATH = "/org/example/motorphui/data/motorph_attendance_records.csv";

    /**
     * Loads every row from the CSV and returns them as an ObservableList
     * ready to be handed directly to a TableView.
     *
     * @return ObservableList of {@link AttendanceRecord}; never null, may be empty.
     */
    public ObservableList<AttendanceRecord> getAllAttendanceRecords() {
        ObservableList<AttendanceRecord> records = FXCollections.observableArrayList();

        try (InputStream is = getClass().getResourceAsStream(CSV_PATH)) {

            if (is == null) {
                System.err.println("[AttendanceDAO] CSV not found on classpath: " + CSV_PATH);
                return records;
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8));

            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {

                // Skip blank lines
                if (line.isBlank()) continue;

                // Skip the header row
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                // Split on comma; limit=6 keeps the last field intact
                String[] fields = line.split(",", 6);

                if (fields.length < 6) {
                    System.err.println("[AttendanceDAO] Skipping malformed row: " + line);
                    continue;
                }

                records.add(new AttendanceRecord(
                        fields[0].trim(),   // Employee #
                        fields[1].trim(),   // Last Name
                        fields[2].trim(),   // First Name
                        fields[3].trim(),   // Date
                        fields[4].trim(),   // Log In
                        fields[5].trim()    // Log Out
                ));
            }

        } catch (Exception e) {
            System.err.println("[AttendanceDAO] Error reading CSV: " + e.getMessage());
            e.printStackTrace();
        }

        return records;
    }
}
