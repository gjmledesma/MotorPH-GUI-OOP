package org.example.motorphui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model representing a single IT support ticket.
 *
 * ENCAPSULATION – All fields are private StringProperty; access via getters/setters.
 * ABSTRACTION   – Callers work with typed helpers (isPending, isResolved, etc.)
 *                 rather than raw status strings.
 *
 * CSV column order (10 fields):
 *   Ticket ID | Employee ID | Last Name | First Name |
 *   Category  | Subject     | Description | Date Filed |
 *   Status    | IT Remarks
 */
public class TicketRequest {

    // ── Status constants ───────────────────────────────────────────────────────
    public static final String STATUS_OPEN        = "Open";
    public static final String STATUS_IN_PROGRESS = "In Progress";
    public static final String STATUS_RESOLVED    = "Resolved";
    public static final String STATUS_CLOSED      = "Closed";

    // ── Category constants ─────────────────────────────────────────────────────
    public static final String[] CATEGORIES = {
        "System Issue", "Account Issue", "Software Request",
        "Hardware Request", "Other"
    };

    // ── Private fields ─────────────────────────────────────────────────────────
    private final StringProperty ticketId;
    private final StringProperty employeeId;
    private final StringProperty lastName;
    private final StringProperty firstName;
    private final StringProperty category;
    private final StringProperty subject;
    private final StringProperty description;
    private final StringProperty dateFiled;
    private final StringProperty status;
    private final StringProperty itRemarks;

    // ── Constructor ────────────────────────────────────────────────────────────
    public TicketRequest(String ticketId, String employeeId, String lastName,
                         String firstName, String category, String subject,
                         String description, String dateFiled,
                         String status, String itRemarks) {
        this.ticketId    = new SimpleStringProperty(ticketId);
        this.employeeId  = new SimpleStringProperty(employeeId);
        this.lastName    = new SimpleStringProperty(lastName);
        this.firstName   = new SimpleStringProperty(firstName);
        this.category    = new SimpleStringProperty(category);
        this.subject     = new SimpleStringProperty(subject);
        this.description = new SimpleStringProperty(description);
        this.dateFiled   = new SimpleStringProperty(dateFiled);
        this.status      = new SimpleStringProperty(status);
        this.itRemarks   = new SimpleStringProperty(itRemarks == null ? "" : itRemarks);
    }

    // ── Property accessors (for TableView cell value factories) ────────────────
    public StringProperty ticketIdProperty()    { return ticketId; }
    public StringProperty employeeIdProperty()  { return employeeId; }
    public StringProperty lastNameProperty()    { return lastName; }
    public StringProperty firstNameProperty()   { return firstName; }
    public StringProperty categoryProperty()    { return category; }
    public StringProperty subjectProperty()     { return subject; }
    public StringProperty descriptionProperty() { return description; }
    public StringProperty dateFiledProperty()   { return dateFiled; }
    public StringProperty statusProperty()      { return status; }
    public StringProperty itRemarksProperty()   { return itRemarks; }

    // ── Plain getters ──────────────────────────────────────────────────────────
    public String getTicketId()    { return ticketId.get(); }
    public String getEmployeeId()  { return employeeId.get(); }
    public String getLastName()    { return lastName.get(); }
    public String getFirstName()   { return firstName.get(); }
    public String getCategory()    { return category.get(); }
    public String getSubject()     { return subject.get(); }
    public String getDescription() { return description.get(); }
    public String getDateFiled()   { return dateFiled.get(); }
    public String getStatus()      { return status.get(); }
    public String getItRemarks()   { return itRemarks.get(); }

    // ── Setters (validated) ────────────────────────────────────────────────────
    public void setStatus(String s) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException("Status cannot be blank.");
        status.set(s.trim());
    }
    public void setItRemarks(String r) {
        itRemarks.set(r == null ? "" : r.trim());
    }

    // ── Convenience helpers ────────────────────────────────────────────────────
    public boolean isOpen()       { return STATUS_OPEN.equals(status.get()); }
    public boolean isInProgress() { return STATUS_IN_PROGRESS.equals(status.get()); }
    public boolean isResolved()   { return STATUS_RESOLVED.equals(status.get()); }
    public boolean isClosed()     { return STATUS_CLOSED.equals(status.get()); }

    /** Full name for display. */
    public String getFullName() {
        return firstName.get() + " " + lastName.get();
    }

    /**
     * Serialises this record to a single CSV row (pipe-delimited description
     * to avoid conflicts with commas inside free-text fields).
     * Actual separator used in the CSV is a comma; description uses
     * double-quote escaping via {@link #escapeCsv}.
     */
    public String toCsvRow() {
        return String.join(",",
            escapeCsv(ticketId.get()),
            escapeCsv(employeeId.get()),
            escapeCsv(lastName.get()),
            escapeCsv(firstName.get()),
            escapeCsv(category.get()),
            escapeCsv(subject.get()),
            escapeCsv(description.get()),
            escapeCsv(dateFiled.get()),
            escapeCsv(status.get()),
            escapeCsv(itRemarks.get())
        );
    }

    /** Wraps a value in quotes if it contains a comma, quote, or newline. */
    private static String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
