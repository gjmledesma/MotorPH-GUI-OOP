package org.example.motorphui.dao;

import org.example.motorphui.model.AttendanceRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for employee-facing time-in / time-out operations.
 * Extends BaseDAO to reuse shared file-resolution helpers.
 *
 * OOP PRINCIPLES DEMONSTRATED:
 *   INHERITANCE   — Extends BaseDAO.
 *   ENCAPSULATION — File paths and parsing are private; callers use typed methods.
 */
public class EmployeeAttendanceDAO extends BaseDAO {

    private static final String CSV_RESOURCE_PATH =
            "/org/example/motorphui/data/motorph_attendance_records.csv";

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("H:mm");

    // ── Public API ────────────────────────────────────────────────────────────

    public ObservableList<AttendanceRecord> getRecordsForEmployee(String empId) {
        ObservableList<AttendanceRecord> list = FXCollections.observableArrayList();
        try (BufferedReader reader = openReader(CSV_RESOURCE_PATH)) {
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

    public AttendanceRecord getTodayRecord(String empId) {
        String today = LocalDate.now().format(DATE_FMT);
        try (BufferedReader reader = openReader(CSV_RESOURCE_PATH)) {
            if (reader == null) return null;
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                if (isHeader) { isHeader = false; continue; }
                String[] f = line.split(",", 6);
                if (f.length < 6) continue;
                if (f[0].trim().equals(empId) && f[3].trim().equals(today)) {
                    return new AttendanceRecord(f[0].trim(), f[1].trim(), f[2].trim(),
                                                f[3].trim(), f[4].trim(), f[5].trim());
                }
            }
        } catch (IOException e) {
            System.err.println("[EmployeeAttendanceDAO] Read error: " + e.getMessage());
        }
        return null;
    }

    public AttendanceRecord writeTimeIn(String empId, String lastName, String firstName) {
        File csv = resolveFile(CSV_RESOURCE_PATH);
        if (csv == null) return null;
        String today   = LocalDate.now().format(DATE_FMT);
        String timeNow = LocalTime.now().format(TIME_FMT);
        String newRow  = String.join(",", empId, lastName, firstName, today, timeNow, "");
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(csv, StandardCharsets.UTF_8, true))) {
            writer.newLine();
            writer.write(newRow);
        } catch (IOException e) {
            System.err.println("[EmployeeAttendanceDAO] Write error (time-in): " + e.getMessage());
            return null;
        }
        return new AttendanceRecord(empId, lastName, firstName, today, timeNow, "");
    }

    public AttendanceRecord writeTimeOut(String empId) {
        File csv = resolveFile(CSV_RESOURCE_PATH);
        if (csv == null) return null;
        String today   = LocalDate.now().format(DATE_FMT);
        String timeNow = LocalTime.now().format(TIME_FMT);
        List<String> lines  = new ArrayList<>();
        AttendanceRecord patched = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(csv), StandardCharsets.UTF_8))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) { lines.add(line); isHeader = false; continue; }
                String[] f = line.split(",", 6);
                if (f.length >= 6 && f[0].trim().equals(empId)
                        && f[3].trim().equals(today) && f[5].trim().isEmpty()
                        && patched == null) {
                    f[5]    = timeNow;
                    line    = String.join(",", f);
                    patched = new AttendanceRecord(f[0].trim(), f[1].trim(), f[2].trim(),
                                                   f[3].trim(), f[4].trim(), f[5].trim());
                }
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("[EmployeeAttendanceDAO] Read error (time-out): " + e.getMessage());
            return null;
        }
        if (patched == null) return null;
        rewriteFile(CSV_RESOURCE_PATH, lines);
        return patched;
    }
}
