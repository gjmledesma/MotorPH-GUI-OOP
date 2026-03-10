package org.example.motorphui.service;

import org.example.motorphui.model.LeaveRequest;
import javafx.collections.ObservableList;

/**
 * Business-logic contract for leave request operations.
 *
 * OOP — ABSTRACTION: Exposes leave business rules independently of the
 * underlying CSV data store.
 */
public interface ILeaveService {
    ObservableList<LeaveRequest> getAllLeaveRequests();
    ObservableList<LeaveRequest> getRequestsForEmployee(String empId);

    LeaveRequest submitLeaveRequest(String empId, String lastName, String firstName,
                                    String startDate, String endDate,
                                    String leaveType, String reason);

    boolean approveLeave(String leaveId);
    boolean denyLeave(String leaveId, String remarks);
    long countPendingRequests();
}
