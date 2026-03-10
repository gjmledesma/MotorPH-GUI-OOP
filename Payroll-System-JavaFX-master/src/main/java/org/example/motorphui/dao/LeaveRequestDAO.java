package org.example.motorphui.dao;

import org.example.motorphui.model.LeaveRequest;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV-backed implementation of {@link ILeaveDAO}.
 *
 * OOP PRINCIPLES DEMONSTRATED:
 *   INHERITANCE   — Extends BaseDAO, inheriting file-resolution helpers.
 *   ABSTRACTION   — Implements ILeaveDAO; callers depend on the interface.
 *   ENCAPSULATION — File paths and parsing logic are private.
 */
public class LeaveRequestDAO extends BaseDAO implements ILeaveDAO {

    private static final String CSV_RESOURCE_PATH =
            "/org/example/motorphui/data/motorph_leave_records.csv";

    public static final String HEADER =
            "Leave ID,Last Name,First Name,Start Date,End Date,Days,Leave Type,Reason,Approved?";

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MM/dd/yyyy");

    // ── ILeaveDAO implementation ──────────────────────────────────────────────

    @Override
    public ObservableList<LeaveRequest> getAllLeaveRequests() {
        ObservableList<LeaveRequest> list = FXCollections.observableArrayList();
        try (BufferedReader reader = openReader(CSV_RESOURCE_PATH)) {
            if (reader == null) return list;
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                if (isHeader) { isHeader = false; continue; }
                LeaveRequest r = parseLine(line);
                if (r != null) list.add(r);
            }
        } catch (IOException e) {
            System.err.println("[LeaveRequestDAO] Read error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public ObservableList<LeaveRequest> getRequestsForEmployee(String empId) {
        ObservableList<LeaveRequest> list = FXCollections.observableArrayList();
        for (LeaveRequest r : getAllLeaveRequests()) {
            if (r.getLeaveId().startsWith("LV-" + empId + "-")) list.add(r);
        }
        return list;
    }

    @Override
    public LeaveRequest submitLeaveRequest(String empId, String lastName, String firstName,
                                           String startDate, String endDate,
                                           String leaveType, String reason) {
        File csv = resolveOrCreateFile();
        if (csv == null) return null;

        String leaveId = generateLeaveId(empId);
        String days    = calculateDays(startDate, endDate);

        String newRow = String.join(",",
                leaveId, lastName, firstName, startDate, endDate,
                days, leaveType, escapeField(reason), "Pending");

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(csv, StandardCharsets.UTF_8, true))) {
            writer.newLine();
            writer.write(newRow);
        } catch (IOException e) {
            System.err.println("[LeaveRequestDAO] Write error: " + e.getMessage());
            return null;
        }
        return new LeaveRequest(leaveId, lastName, firstName, startDate, endDate,
                                days, leaveType, reason, "Pending");
    }

    @Override
    public boolean updateStatus(String leaveId, String newStatus) {
        File csv = resolveOrCreateFile();
        if (csv == null) return false;

        List<String> lines   = new ArrayList<>();
        boolean      updated = false;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(csv), StandardCharsets.UTF_8))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) { lines.add(line); isHeader = false; continue; }
                String[] f = line.split(",", 9);
                if (f.length >= 9 && f[0].trim().equals(leaveId) && !updated) {
                    f[8]    = newStatus;
                    line    = String.join(",", f);
                    updated = true;
                }
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("[LeaveRequestDAO] Read error (update): " + e.getMessage());
            return false;
        }

        if (!updated) return false;
        rewriteFile(CSV_RESOURCE_PATH, lines);
        return true;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private LeaveRequest parseLine(String line) {
        String[] f = line.split(",", 9);
        if (f.length < 9) return null;
        return new LeaveRequest(f[0].trim(), f[1].trim(), f[2].trim(),
                f[3].trim(), f[4].trim(), f[5].trim(),
                f[6].trim(), f[7].trim(), f[8].trim());
    }

    private String generateLeaveId(String empId) {
        int next = getRequestsForEmployee(empId).size() + 1;
        return String.format("LV-%s-%03d", empId, next);
    }

    private String calculateDays(String startDate, String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate, DATE_FMT);
            LocalDate end   = LocalDate.parse(endDate,   DATE_FMT);
            long days = ChronoUnit.DAYS.between(start, end) + 1;
            return String.valueOf(Math.max(days, 1));
        } catch (Exception e) {
            return "?";
        }
    }

    private String escapeField(String field) {
        if (field == null) return "";
        return field.contains(",") ? "\"" + field + "\"" : field;
    }

    /**
     * Resolves the CSV to a writable File, creating it with a header row if
     * it does not yet exist.
     */
    public File resolveOrCreateFile() {
        try {
            URL url = getClass().getResource(CSV_RESOURCE_PATH);
            if (url != null && "file".equals(url.getProtocol())) {
                return new File(url.toURI());
            }
        } catch (Exception ignored) {}
        try {
            URL base = getClass().getResource("/org/example/motorphui/data/");
            if (base != null && "file".equals(base.getProtocol())) {
                File f = new File(new File(base.toURI()), "motorph_leave_records.csv");
                if (!f.exists()) {
                    try (BufferedWriter w = new BufferedWriter(
                            new FileWriter(f, StandardCharsets.UTF_8))) {
                        w.write(HEADER);
                    }
                }
                return f;
            }
        } catch (Exception e) {
            System.err.println("[LeaveRequestDAO] Cannot create CSV file: " + e.getMessage());
        }
        return null;
    }
}
