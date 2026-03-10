package org.example.motorphui.dao;

import org.example.motorphui.model.LeaveRequest;
import javafx.collections.ObservableList;

/**
 * Data-access contract for leave request operations.
 *
 * OOP — ABSTRACTION: Exposes leave request operations without leaking
 * CSV file paths or parsing logic to callers.
 */
public interface ILeaveDAO {
    /** Returns all leave requests in the data store. */
    ObservableList<LeaveRequest> getAllLeaveRequests();

    /** Returns only the requests filed by the given employee ID. */
    ObservableList<LeaveRequest> getRequestsForEmployee(String empId);

    /**
     * Submits a new leave request and persists it.
     * @return the saved LeaveRequest, or null on failure.
     */
    LeaveRequest submitLeaveRequest(String empId, String lastName, String firstName,
                                    String startDate, String endDate,
                                    String leaveType, String reason);

    /**
     * Updates the approval status of an existing leave request.
     * @param newStatus "Approved" or "Denied: remarks"
     * @return true on success.
     */
    boolean updateStatus(String leaveId, String newStatus);
}
