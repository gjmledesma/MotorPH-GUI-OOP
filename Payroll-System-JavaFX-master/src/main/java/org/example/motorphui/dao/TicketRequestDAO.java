package org.example.motorphui.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.motorphui.model.TicketRequest;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-access object for IT support ticket records.
 *
 * ENCAPSULATION – All file I/O is private; callers use typed public methods.
 * ABSTRACTION   – Hides CSV parsing, escaping, and file resolution from callers.
 *
 * CSV path (classpath resource):
 *   /org/example/motorphui/data/motorph_ticket_requests.csv
 *
 * CSV header (10 columns):
 *   Ticket ID, Employee ID, Last Name, First Name, Category,
 *   Subject, Description, Date Filed, Status, IT Remarks
 */
public class TicketRequestDAO {

    // ── Constants ──────────────────────────────────────────────────────────────
    public static final String CSV_HEADER =
        "Ticket ID,Employee ID,Last Name,First Name,Category,Subject,Description,Date Filed,Status,IT Remarks";

    private static final String RESOURCE_PATH =
        "/org/example/motorphui/data/motorph_ticket_requests.csv";

    private static final int EXPECTED_COLS = 10;

    // ── Public API ─────────────────────────────────────────────────────────────

    /** Returns all ticket records from the CSV. */
    public ObservableList<TicketRequest> getAllTickets() {
        ObservableList<TicketRequest> list = FXCollections.observableArrayList();
        try (BufferedReader reader = openReader()) {
            if (reader == null) return list;
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // skip header
                TicketRequest t = parseLine(line);
                if (t != null) list.add(t);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Returns only the tickets filed by a specific employee. */
    public ObservableList<TicketRequest> getTicketsForEmployee(String employeeId) {
        ObservableList<TicketRequest> result = FXCollections.observableArrayList();
        for (TicketRequest t : getAllTickets()) {
            if (t.getEmployeeId().trim().equals(employeeId.trim())) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * Submits a new ticket and appends it to the CSV.
     *
     * @return the saved {@link TicketRequest}, or {@code null} on failure.
     */
    public TicketRequest submitTicket(String employeeId, String lastName,
                                      String firstName, String category,
                                      String subject, String description,
                                      String dateFiled) {
        String ticketId = generateTicketId(employeeId);
        TicketRequest ticket = new TicketRequest(
            ticketId, employeeId, lastName, firstName,
            category, subject, description, dateFiled,
            TicketRequest.STATUS_OPEN, ""
        );
        appendRow(ticket.toCsvRow());
        return ticket;
    }

    /**
     * Updates the status and IT remarks for an existing ticket.
     * Rewrites the entire CSV.
     *
     * @return {@code true} if the ticket was found and updated.
     */
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
        if (found) rewriteAll(all);
        return found;
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    /** Generates the next ticket ID for an employee (TKT-empId-001, 002, …). */
    private String generateTicketId(String employeeId) {
        ObservableList<TicketRequest> existing = getTicketsForEmployee(employeeId);
        int seq = existing.size() + 1;
        return String.format("TKT-%s-%03d", employeeId, seq);
    }

    /** Parses a single CSV line into a {@link TicketRequest}. */
    private TicketRequest parseLine(String line) {
        if (line == null || line.isBlank()) return null;
        String[] fields = parseCsvLine(line);
        if (fields.length < EXPECTED_COLS) return null;
        try {
            return new TicketRequest(
                fields[0].trim(), fields[1].trim(), fields[2].trim(),
                fields[3].trim(), fields[4].trim(), fields[5].trim(),
                fields[6].trim(), fields[7].trim(), fields[8].trim(),
                fields[9].trim()
            );
        } catch (Exception e) {
            System.err.println("TicketRequestDAO: could not parse line: " + line);
            return null;
        }
    }

    /**
     * CSV parser that handles double-quoted fields (supports commas inside
     * description/subject without breaking the column count).
     */
    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"'); i++; // escaped quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        result.add(sb.toString());
        return result.toArray(new String[0]);
    }

    /** Opens a BufferedReader for the resource CSV. */
    private BufferedReader openReader() {
        InputStream is = getClass().getResourceAsStream(RESOURCE_PATH);
        if (is == null) {
            System.err.println("TicketRequestDAO: CSV not found at " + RESOURCE_PATH);
            return null;
        }
        return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
    }

    /** Resolves the resource to a writable File on disk. */
    private File resolveFile() {
        try {
            URL url = getClass().getResource(RESOURCE_PATH);
            if (url == null) {
                System.err.println("TicketRequestDAO: cannot resolve resource URL.");
                return null;
            }
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Appends a single data row to the CSV (creates file + header if missing). */
    private void appendRow(String csvRow) {
        File file = resolveFile();
        if (file == null) return;
        boolean needsHeader = !file.exists() || file.length() == 0;
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(file, true))) {
            if (needsHeader) {
                writer.write(CSV_HEADER);
                writer.newLine();
            }
            writer.write(csvRow);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Rewrites the entire CSV from the given list. */
    private void rewriteAll(ObservableList<TicketRequest> tickets) {
        File file = resolveFile();
        if (file == null) return;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            writer.write(CSV_HEADER);
            writer.newLine();
            for (TicketRequest t : tickets) {
                writer.write(t.toCsvRow());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
