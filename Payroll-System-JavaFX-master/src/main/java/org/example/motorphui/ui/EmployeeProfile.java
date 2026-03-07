package org.example.motorphui.ui;

import org.example.motorphui.dao.LeaveRequestDAO;
import org.example.motorphui.model.AllEmployee;
import org.example.motorphui.model.LeaveRequest;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class EmployeeProfile {

    @FXML private Label nameLabel;
    @FXML private Label positionLabel;
    @FXML private Label statusLabel;
    @FXML private Label supervisorNameLabel;
    @FXML private Label empNumberLabel;
    @FXML private Label bdayLabel;
    @FXML private Label addressLabel;
    @FXML private Label phoneNumLabel;
    @FXML private Label basicSalaryLabel;
    @FXML private Label hourlyRateLabel;

    @FXML private Label leaveTypeLabel;
    @FXML private Label durationLabel;
    @FXML private Label statusLeaveLabel;

    @FXML private Label sssLabel;
    @FXML private Label riceSubLabel;
    @FXML private Label philhealthLabel;
    @FXML private Label phoneAllowanceLabel;
    @FXML private Label tinLabel;
    @FXML private Label clothingAllowanceLabel;
    @FXML private Label pagibigLabel;

    public void setEmployeeData(AllEmployee employee) {
        // ── Personal info ─────────────────────────────────────────────────────
        nameLabel          .setText(employee.getFirstName() + " " + employee.getLastName());
        positionLabel      .setText(employee.getPosition());
        statusLabel        .setText(employee.getStatus());
        supervisorNameLabel.setText(employee.getImmediateSupervisor());
        empNumberLabel     .setText(employee.getEmployeeNumber());
        bdayLabel          .setText(employee.getBirthday());
        addressLabel       .setText(employee.getAddress());
        phoneNumLabel      .setText(employee.getPhoneNumber());
        basicSalaryLabel   .setText(employee.getBasicSalary());
        hourlyRateLabel    .setText(employee.getHourlyRate());

        // ── Tax & Benefits ────────────────────────────────────────────────────
        sssLabel              .setText(employee.getSss());
        riceSubLabel          .setText(employee.getRiceSubsidy());
        philhealthLabel       .setText(employee.getPhilHealth());
        phoneAllowanceLabel   .setText(employee.getPhoneAllowance());
        tinLabel              .setText(employee.getTin());
        clothingAllowanceLabel.setText(employee.getClothingAllowance());
        pagibigLabel          .setText(employee.getPagIbig());

        // ── Leave Approval — pull the most recent non-pending request ─────────
        populateLeaveApproval(employee.getEmployeeNumber());
    }

    /**
     * Finds the most recent resolved leave request for this employee and
     * shows it in the Leave Approval card.  Falls back to "No leave records"
     * if nothing exists yet.
     */
    private void populateLeaveApproval(String empId) {
        LeaveRequestDAO dao = new LeaveRequestDAO();
        ObservableList<LeaveRequest> requests = dao.getRequestsForEmployee(empId);

        if (requests.isEmpty()) {
            leaveTypeLabel  .setText("—");
            durationLabel   .setText("No leave records");
            statusLeaveLabel.setText("—");
            makeLeaveLabelsVisible(true);
            return;
        }

        // Prefer the most recent approved/denied request; fall back to latest pending
        LeaveRequest display = requests.stream()
                .filter(r -> !r.getApprovalStatus().equals("Pending"))
                .reduce((first, second) -> second)        // last resolved
                .orElse(requests.get(requests.size() - 1)); // latest pending

        leaveTypeLabel  .setText(display.getLeaveType());
        durationLabel   .setText(display.getStartDate() + " – " + display.getEndDate());
        statusLeaveLabel.setText(display.getApprovalStatus());
        makeLeaveLabelsVisible(true);
    }

    private void makeLeaveLabelsVisible(boolean visible) {
        leaveTypeLabel  .setVisible(visible);
        durationLabel   .setVisible(visible);
        statusLeaveLabel.setVisible(visible);
    }
}
