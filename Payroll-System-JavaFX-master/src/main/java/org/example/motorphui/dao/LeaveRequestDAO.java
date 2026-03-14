package org.example.motorphui.dao;

import org.example.motorphui.model.LeaveRequest;
import org.example.motorphui.util.DataFileManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV-backed implementation of {@link ILeaveDAO}.
 *
 * All reads and writes go through DataFileManager, which targets the persistent
 * external directory (~/.motorphui/data/) instead of the Gradle build output.
 */
public class LeaveRequestDAO extends BaseDAO implements ILeaveDAO {

    private static final String CSV_PATH = "/org/example/motorphui/data/motorph_leave_records.csv";
    private static final String FILENAME = "motorph_leave_records.csv";

    public static final String HEADER =
            "Leave ID,Last Name,First Name,Start Date,End Date,Days,Leave Type,Reason,Approved?";

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MM/dd/yyyy");

    // ── ILeaveDAO implementation ──────────────────────────────────────────────

    @Override
    public ObservableList<LeaveRequest> getAllLeaveRequests() {
        ObservableList<LeaveRequest> list = FXCollections.observableArrayList();
        try (BufferedReader reader = openReader(CSV_PATH)) {
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
        String leaveId = generateLeaveId(empId);
        String days    = calculateDays(startDate, endDate);

        String newRow = String.join(",",
                leaveId, lastName, firstName, startDate, endDate,
                days, leaveType, escapeField(reason), "Pending");

        appendRow(CSV_PATH, HEADER, newRow);

        return new LeaveRequest(leaveId, lastName, firstName, startDate, endDate,
                                days, leaveType, reason, "Pending");
    }

    @Override
    public boolean updateStatus(String leaveId, String newStatus) {
        ObservableList<LeaveRequest> all = getAllLeaveRequests();
        List<String> lines   = new ArrayList<>();
        boolean      updated = false;

        lines.add(HEADER);
        for (LeaveRequest r : all) {
            if (r.getLeaveId().trim().equals(leaveId) && !updated) {
                r.setApprovalStatus(newStatus);
                updated = true;
            }
            lines.add(toCSVRow(r));
        }

        if (!updated) return false;
        rewriteFile(CSV_PATH, lines);
        return true;
    }

    // ── Kept for backward compatibility (called by older code) ────────────────

    /** @deprecated Use {@link #resolveFile(String)} via BaseDAO instead. */
    public File resolveOrCreateFile() {
        return DataFileManager.resolveFile(FILENAME);
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    private LeaveRequest parseLine(String line) {
        String[] f = line.split(",", 9);
        if (f.length < 9) return null;
        return new LeaveRequest(f[0].trim(), f[1].trim(), f[2].trim(),
                f[3].trim(), f[4].trim(), f[5].trim(),
                f[6].trim(), f[7].trim(), f[8].trim());
    }

    private String toCSVRow(LeaveRequest r) {
        return String.join(",",
                r.getLeaveId(), r.getLastName(), r.getFirstName(),
                r.getStartDate(), r.getEndDate(), r.getDays(),
                r.getLeaveType(), escapeField(r.getReason()), r.getApprovalStatus());
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
}
