package org.example.motorphui.model;

import javafx.beans.property.SimpleStringProperty;

/**
 * Model for a single leave request row.
 */
public class LeaveRequest {

    private final SimpleStringProperty leaveId;
    private final SimpleStringProperty lastName;
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty startDate;
    private final SimpleStringProperty endDate;
    private final SimpleStringProperty days;
    private final SimpleStringProperty leaveType;
    private final SimpleStringProperty reason;
    private final SimpleStringProperty approvalStatus;

    public LeaveRequest(String leaveId, String lastName, String firstName,
                        String startDate, String endDate, String days,
                        String leaveType, String reason, String approvalStatus) {
        this.leaveId        = new SimpleStringProperty(leaveId);
        this.lastName       = new SimpleStringProperty(lastName);
        this.firstName      = new SimpleStringProperty(firstName);
        this.startDate      = new SimpleStringProperty(startDate);
        this.endDate        = new SimpleStringProperty(endDate);
        this.days           = new SimpleStringProperty(days);
        this.leaveType      = new SimpleStringProperty(leaveType);
        this.reason         = new SimpleStringProperty(reason);
        this.approvalStatus = new SimpleStringProperty(approvalStatus);
    }

    // ── JavaFX property accessors (required by PropertyValueFactory) ──────────

    public SimpleStringProperty leaveIdProperty()        { return leaveId; }
    public SimpleStringProperty lastNameProperty()       { return lastName; }
    public SimpleStringProperty firstNameProperty()      { return firstName; }
    public SimpleStringProperty startDateProperty()      { return startDate; }
    public SimpleStringProperty endDateProperty()        { return endDate; }
    public SimpleStringProperty daysProperty()           { return days; }
    public SimpleStringProperty leaveTypeProperty()      { return leaveType; }
    public SimpleStringProperty reasonProperty()         { return reason; }
    public SimpleStringProperty approvalStatusProperty() { return approvalStatus; }

    // ── Plain getters ─────────────────────────────────────────────────────────

    public String getLeaveId()        { return leaveId.get(); }
    public String getLastName()       { return lastName.get(); }
    public String getFirstName()      { return firstName.get(); }
    public String getStartDate()      { return startDate.get(); }
    public String getEndDate()        { return endDate.get(); }
    public String getDays()           { return days.get(); }
    public String getLeaveType()      { return leaveType.get(); }
    public String getReason()         { return reason.get(); }
    public String getApprovalStatus() { return approvalStatus.get(); }

    // ── Setters (needed for approve/deny rewrite) ─────────────────────────────

    public void setApprovalStatus(String status) { approvalStatus.set(status); }
}
