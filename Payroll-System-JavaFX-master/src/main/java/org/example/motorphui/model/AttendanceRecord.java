package org.example.motorphui.model;

import javafx.beans.property.SimpleStringProperty;

/**
 * Model class representing a single attendance record from the CSV.
 * Each field uses JavaFX StringProperty so the TableView can bind to it directly.
 */
public class AttendanceRecord {

    private final SimpleStringProperty empNumber;
    private final SimpleStringProperty lastName;
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty date;
    private final SimpleStringProperty logIn;
    private final SimpleStringProperty logOut;
    private final SimpleStringProperty status;

    // Standard work start time (HH:mm). Arrivals after this are marked Late.
    private static final String STANDARD_LOGIN = "08:00";

    public AttendanceRecord(String empNumber,
                            String lastName,
                            String firstName,
                            String date,
                            String logIn,
                            String logOut) {
        this.empNumber  = new SimpleStringProperty(empNumber);
        this.lastName   = new SimpleStringProperty(lastName);
        this.firstName  = new SimpleStringProperty(firstName);
        this.date       = new SimpleStringProperty(date);
        this.logIn      = new SimpleStringProperty(logIn);
        this.logOut     = new SimpleStringProperty(logOut);
        this.status     = new SimpleStringProperty(computeStatus(logIn));
    }

    // ── Status logic ─────────────────────────────────────────────────────────

    /**
     * Compares the employee's log-in time against the standard start time.
     * Returns "On Time" or "Late".
     */
    private String computeStatus(String loginTime) {
        try {
            // Normalise "H:mm" → "HH:mm" so the comparison works correctly
            String normalised = normaliseTime(loginTime);
            return normalised.compareTo(STANDARD_LOGIN) <= 0 ? "On Time" : "Late";
        } catch (Exception e) {
            return "Unknown";
        }
    }

    /** Pads single-digit hours so times sort lexicographically (e.g. "8:30" → "08:30"). */
    private String normaliseTime(String time) {
        if (time == null || time.isBlank()) return "00:00";
        String[] parts = time.trim().split(":");
        String hour   = parts[0].length() == 1 ? "0" + parts[0] : parts[0];
        String minute = parts.length > 1 ? parts[1] : "00";
        return hour + ":" + minute;
    }

    // ── JavaFX Property getters (required by PropertyValueFactory) ────────────

    public SimpleStringProperty empNumberProperty()  { return empNumber; }
    public SimpleStringProperty lastNameProperty()   { return lastName; }
    public SimpleStringProperty firstNameProperty()  { return firstName; }
    public SimpleStringProperty dateProperty()       { return date; }
    public SimpleStringProperty logInProperty()      { return logIn; }
    public SimpleStringProperty logOutProperty()     { return logOut; }
    public SimpleStringProperty statusProperty()     { return status; }

    // ── Plain getters (convenient for filtering logic) ────────────────────────

    public String getEmpNumber()  { return empNumber.get(); }
    public String getLastName()   { return lastName.get(); }
    public String getFirstName()  { return firstName.get(); }
    public String getDate()       { return date.get(); }
    public String getLogIn()      { return logIn.get(); }
    public String getLogOut()     { return logOut.get(); }
    public String getStatus()     { return status.get(); }
}
