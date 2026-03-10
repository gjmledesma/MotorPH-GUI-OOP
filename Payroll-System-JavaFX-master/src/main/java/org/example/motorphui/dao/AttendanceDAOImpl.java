package org.example.motorphui.dao;

import org.example.motorphui.model.AttendanceRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV-backed implementation of {@link IAttendanceDAO}.
 *
 * Merges and replaces the original NewAttendanceDAO (read-only) and
 * EmployeeAttendanceDAO (read/write) into a single, coherent class.
 * AttendanceDAO is kept as a thin subclass for backward compatibility.
 *
 * OOP PRINCIPLES DEMONSTRATED:
 *   INHERITANCE   — Extends BaseDAO, reusing file I/O helpers.
 *   ABSTRACTION   — Implements IAttendanceDAO; callers depend only on the interface.
 *   ENCAPSULATION — All file paths and parsing logic are private.
 */
public class AttendanceDAOImpl extends BaseDAO implements IAttendanceDAO {

    private static final String CSV_PATH = "/org/example/motorphui/data/motorph_attendance_records.csv";

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("H:mm");

    // ── IAttendanceDAO implementation ─────────────────────────────────────────

    @Override
    public ObservableList<AttendanceRecord> getAllAttendanceRecords() {
        ObservableList<AttendanceRecord> records = FXCollections.observableArrayList();
        try (BufferedReader reader = openReader(CSV_PATH)) {
            if (reader == null) return records;
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                if (isHeader) { isHeader = false; continue; }
                AttendanceRecord r = parseLine(line);
                if (r != null) records.add(r);
            }
        } catch (IOException e) {
            System.err.println("[AttendanceDAOImpl] Read error: " + e.getMessage());
        }
        return records;
    }

    @Override
    public ObservableList<AttendanceRecord> getRecordsForEmployee(String empId) {
        ObservableList<AttendanceRecord> list = FXCollections.observableArrayList();
        for (AttendanceRecord r : getAllAttendanceRecords()) {
            if (r.getEmpNumber().trim().equals(empId.trim())) list.add(r);
        }
        return list;
    }

    @Override
    public AttendanceRecord getTodayRecord(String empId) {
        String today = LocalDate.now().format(DATE_FMT);
        for (AttendanceRecord r : getRecordsForEmployee(empId)) {
            if (r.getDate().trim().equals(today)) return r;
        }
        return null;
    }

    @Override
    public AttendanceRecord writeTimeIn(String empId, String lastName, String firstName) {
        File csv = resolveFile(CSV_PATH);
        if (csv == null) return null;

        String today   = LocalDate.now().format(DATE_FMT);
        String timeNow = LocalTime.now().format(TIME_FMT);
        String newRow  = String.join(",", empId, lastName, firstName, today, timeNow, "");

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(csv, StandardCharsets.UTF_8, true))) {
            writer.newLine();
            writer.write(newRow);
        } catch (IOException e) {
            System.err.println("[AttendanceDAOImpl] Write error (time-in): " + e.getMessage());
            return null;
        }
        return new AttendanceRecord(empId, lastName, firstName, today, timeNow, "");
    }

    @Override
    public AttendanceRecord writeTimeOut(String empId) {
        File csv = resolveFile(CSV_PATH);
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
            System.err.println("[AttendanceDAOImpl] Read error (time-out): " + e.getMessage());
            return null;
        }

        if (patched == null) return null;
        rewriteFile(CSV_PATH, lines);
        return patched;
    }

    @Override
    public double getMonthlyHours(String empNumber, String monthName, String year) {
        double totalHours = 0.0;
        for (AttendanceRecord r : getRecordsForEmployee(empNumber)) {
            if (isDateMatch(r.getDate(), monthName, year)) {
                double login  = parseTimeToDecimal(r.getLogIn());
                double logout = parseTimeToDecimal(r.getLogOut());
                if (logout > login) totalHours += (logout - login);
            }
        }
        return totalHours;
    }

    // ── Utility methods (also used by AttendanceDAO subclass) ─────────────────

    public boolean isDateMatch(String dateStr, String monthName, String targetYear) {
        String[] parts = dateStr.split("/");
        if (parts.length != 3) return false;
        int monthNum = Month.valueOf(monthName.toUpperCase()).getValue();
        String expectedMonth = String.format("%02d", monthNum);
        return parts[0].trim().equals(expectedMonth) && parts[2].trim().equals(targetYear);
    }

    public double parseTimeToDecimal(String time) {
        try {
            String[] parts = time.split(":");
            return Integer.parseInt(parts[0]) + Integer.parseInt(parts[1]) / 60.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    private AttendanceRecord parseLine(String line) {
        String[] fields = line.split(",", 6);
        if (fields.length < 6) return null;
        return new AttendanceRecord(
                fields[0].trim(), fields[1].trim(), fields[2].trim(),
                fields[3].trim(), fields[4].trim(), fields[5].trim());
    }
}
