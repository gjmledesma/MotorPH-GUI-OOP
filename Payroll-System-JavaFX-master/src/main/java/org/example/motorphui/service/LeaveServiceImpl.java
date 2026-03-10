package org.example.motorphui.service;

import org.example.motorphui.dao.ILeaveDAO;
import org.example.motorphui.dao.LeaveRequestDAO;
import org.example.motorphui.model.LeaveRequest;
import javafx.collections.ObservableList;

/**
 * Business-logic implementation for leave request management.
 *
 * OOP PRINCIPLES DEMONSTRATED:
 *   ABSTRACTION   — Implements ILeaveService; depends on ILeaveDAO.
 *   ENCAPSULATION — The DAO reference is private.
 */
public class LeaveServiceImpl implements ILeaveService {

    private final ILeaveDAO leaveDAO;

    public LeaveServiceImpl() {
        this.leaveDAO = new LeaveRequestDAO();
    }

    public LeaveServiceImpl(ILeaveDAO leaveDAO) {
        this.leaveDAO = leaveDAO;
    }

    @Override
    public ObservableList<LeaveRequest> getAllLeaveRequests() {
        return leaveDAO.getAllLeaveRequests();
    }

    @Override
    public ObservableList<LeaveRequest> getRequestsForEmployee(String empId) {
        return leaveDAO.getRequestsForEmployee(empId);
    }

    @Override
    public LeaveRequest submitLeaveRequest(String empId, String lastName, String firstName,
                                           String startDate, String endDate,
                                           String leaveType, String reason) {
        return leaveDAO.submitLeaveRequest(empId, lastName, firstName,
                                           startDate, endDate, leaveType, reason);
    }

    @Override
    public boolean approveLeave(String leaveId) {
        return leaveDAO.updateStatus(leaveId, "Approved");
    }

    @Override
    public boolean denyLeave(String leaveId, String remarks) {
        String status = (remarks == null || remarks.isBlank())
                ? "Denied" : "Denied: " + remarks.trim();
        return leaveDAO.updateStatus(leaveId, status);
    }

    @Override
    public long countPendingRequests() {
        return leaveDAO.getAllLeaveRequests().stream()
                .filter(r -> "Pending".equalsIgnoreCase(r.getApprovalStatus()))
                .count();
    }
}
