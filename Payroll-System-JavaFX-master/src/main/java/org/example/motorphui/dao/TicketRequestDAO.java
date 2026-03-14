package org.example.motorphui.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.motorphui.model.TicketRequest;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-access object for IT support ticket records.
 * Extends BaseDAO to use shared file-resolution and I/O helpers.
 */
public class TicketRequestDAO extends BaseDAO {

    public static final String CSV_HEADER =
        "Ticket ID,Employee ID,Last Name,First Name,Category,Subject,Description,Date Filed,Status,IT Remarks";

    private static final String RESOURCE_PATH =
        "/org/example/motorphui/data/motorph_ticket_requests.csv";

    private static final int EXPECTED_COLS = 10;

    // ── Public API ─────────────────────────────────────────────────────────────

    public ObservableList<TicketRequest> getAllTickets() {
        ObservableList<TicketRequest> list = FXCollections.observableArrayList();
        try (BufferedReader reader = openReader(RESOURCE_PATH)) {
            if (reader == null) return list;
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                TicketRequest t = parseLine(line);
                if (t != null) list.add(t);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ObservableList<TicketRequest> getTicketsForEmployee(String employeeId) {
        ObservableList<TicketRequest> result = FXCollections.observableArrayList();
        for (TicketRequest t : getAllTickets()) {
            if (t.getEmployeeId().trim().equals(employeeId.trim())) result.add(t);
        }
        return result;
    }

    public TicketRequest submitTicket(String employeeId, String lastName,
                                      String firstName, String category,
                                      String subject, String description,
                                      String dateFiled) {
        String ticketId = generateTicketId(employeeId);
        TicketRequest ticket = new TicketRequest(
            ticketId, employeeId, lastName, firstName,
            category, subject, description, dateFiled,
            TicketRequest.STATUS_OPEN, "");
        appendRow(RESOURCE_PATH, CSV_HEADER, ticket.toCsvRow());
        return ticket;
    }

    public boolean updateTicket(String ticketId, String newStatus, String itRemarks) {
        ObservableList<TicketRequest> all = getAllTickets();
        boolean found = false;
        for (TicketRequest t : all) {
            if (t.getTicketId().equals(ticketId)) {
                t.setStatus(newStatus);
                t.setItRemarks(itRemarks);
                found = true;
                break;
            }
        }
        if (found) rewriteAllTickets(all);
        return found;
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    private String generateTicketId(String employeeId) {
        int seq = getTicketsForEmployee(employeeId).size() + 1;
        return String.format("TKT-%s-%03d", employeeId, seq);
    }

    private TicketRequest parseLine(String line) {
        if (line == null || line.isBlank()) return null;
        String[] fields = parseCsvLine(line);
        if (fields.length < EXPECTED_COLS) return null;
        try {
            return new TicketRequest(
                fields[0].trim(), fields[1].trim(), fields[2].trim(),
                fields[3].trim(), fields[4].trim(), fields[5].trim(),
                fields[6].trim(), fields[7].trim(), fields[8].trim(),
                fields[9].trim());
        } catch (Exception e) {
            System.err.println("TicketRequestDAO: could not parse line: " + line);
            return null;
        }
    }

    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"'); i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(sb.toString()); sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        result.add(sb.toString());
        return result.toArray(new String[0]);
    }

    private void rewriteAllTickets(ObservableList<TicketRequest> tickets) {
        List<String> lines = new ArrayList<>();
        lines.add(CSV_HEADER);
        for (TicketRequest t : tickets) lines.add(t.toCsvRow());
        rewriteFile(RESOURCE_PATH, lines);
    }
}
