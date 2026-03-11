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
 * Extends BaseDAO; all I/O goes through DataFileManager.
 */
public class EmployeeAttendanceDAO extends BaseDAO {

    private static final String CSV_PATH = "/org/example/motorphui/data/motorph_attendance_records.csv";
    private static final String CSV_HEADER = "Employee #,Last Name,First Name,Date,Log In,Log Out";

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("H:mm");

    public ObservableList<AttendanceRecord> getRecordsForEmployee(String empId) {
        ObservableList<AttendanceRecord> list = FXCollections.observableArrayList();
        try (BufferedReader reader = openReader(CSV_PATH)) {
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
        for (AttendanceRecord r : getRecordsForEmployee(empId)) {
            if (r.getDate().trim().equals(today)) return r;
        }
        return null;
    }

    public AttendanceRecord writeTimeIn(String empId, String lastName, String firstName) {
        String today   = LocalDate.now().format(DATE_FMT);
        String timeNow = LocalTime.now().format(TIME_FMT);
        String newRow  = String.join(",", empId, lastName, firstName, today, timeNow, "");
        appendRow(CSV_PATH, CSV_HEADER, newRow);
        return new AttendanceRecord(empId, lastName, firstName, today, timeNow, "");
    }

    public AttendanceRecord writeTimeOut(String empId) {
        String today   = LocalDate.now().format(DATE_FMT);
        String timeNow = LocalTime.now().format(TIME_FMT);

        ObservableList<AttendanceRecord> all = getRecordsForEmployee(empId);
        // Also need ALL records to rewrite the full file
        ObservableList<AttendanceRecord> allRecords = getAllRecords();

        AttendanceRecord patched = null;
        for (AttendanceRecord r : allRecords) {
            if (r.getEmpNumber().trim().equals(empId)
                    && r.getDate().trim().equals(today)
                    && r.getLogOut().trim().isEmpty()
                    && patched == null) {
                r.setLogOut(timeNow);
                patched = r;
            }
        }
        if (patched == null) return null;

        List<String> lines = new ArrayList<>();
        lines.add(CSV_HEADER);
        for (AttendanceRecord r : allRecords) {
            lines.add(String.join(",",
                    r.getEmpNumber(), r.getLastName(), r.getFirstName(),
                    r.getDate(), r.getLogIn(), r.getLogOut()));
        }
        rewriteFile(CSV_PATH, lines);
        return patched;
    }

    private ObservableList<AttendanceRecord> getAllRecords() {
        ObservableList<AttendanceRecord> list = FXCollections.observableArrayList();
        try (BufferedReader reader = openReader(CSV_PATH)) {
            if (reader == null) return list;
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                if (isHeader) { isHeader = false; continue; }
                String[] f = line.split(",", 6);
                if (f.length >= 6) {
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
}
