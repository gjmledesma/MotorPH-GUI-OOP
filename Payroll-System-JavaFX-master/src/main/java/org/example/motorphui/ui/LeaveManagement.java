package org.example.motorphui.ui;

import org.example.motorphui.dao.LeaveRequestDAO;
import org.example.motorphui.model.LeaveRequest;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.util.Optional;

/**
 * HR controller for the Leave Management screen.
 *
 * Responsibilities:
 *  - Load all leave requests from the CSV into the TableView
 *  - Allow HR to select a row and Approve or Deny it
 *  - Deny prompts for remarks; both actions persist to the CSV
 */
public class LeaveManagement {

    // ── FXML injections ──────────────────────────────────────────────────────

    @FXML private AnchorPane root;
    @FXML private Label emp_info_label;

    @FXML private Button approve_button;
    @FXML private Button deny_button;

    @FXML private TableView<LeaveRequest>           leave_table;
    @FXML private TableColumn<LeaveRequest, String> leaveIdColumn;
    @FXML private TableColumn<LeaveRequest, String> lastNameColumn;
    @FXML private TableColumn<LeaveRequest, String> firstNameColumn;
    @FXML private TableColumn<LeaveRequest, String> startDateColumn;
    @FXML private TableColumn<LeaveRequest, String> endDateColumn;
    @FXML private TableColumn<LeaveRequest, String> daysColumn;
    @FXML private TableColumn<LeaveRequest, String> leaveTypeColumn;
    @FXML private TableColumn<LeaveRequest, String> reasonColumn;
    @FXML private TableColumn<LeaveRequest, String> approvalStatusColumn;

    // ── State ────────────────────────────────────────────────────────────────

    private LeaveRequestDAO              dao;
    private ObservableList<LeaveRequest> leaveRequests;

    // ── Initialisation ───────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        dao = new LeaveRequestDAO();
        bindColumns();
        loadData();
        setupButtonState();
    }

    // ── Setup helpers ────────────────────────────────────────────────────────

    private void bindColumns() {
        leaveIdColumn       .setCellValueFactory(new PropertyValueFactory<>("leaveId"));
        lastNameColumn      .setCellValueFactory(new PropertyValueFactory<>("lastName"));
        firstNameColumn     .setCellValueFactory(new PropertyValueFactory<>("firstName"));
        startDateColumn     .setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn       .setCellValueFactory(new PropertyValueFactory<>("endDate"));
        daysColumn          .setCellValueFactory(new PropertyValueFactory<>("days"));
        leaveTypeColumn     .setCellValueFactory(new PropertyValueFactory<>("leaveType"));
        reasonColumn        .setCellValueFactory(new PropertyValueFactory<>("reason"));
        approvalStatusColumn.setCellValueFactory(new PropertyValueFactory<>("approvalStatus"));

        styleStatusColumn();
    }

    private void loadData() {
        leaveRequests = dao.getAllLeaveRequests();
        SortedList<LeaveRequest> sorted = new SortedList<>(leaveRequests);
        sorted.comparatorProperty().bind(leave_table.comparatorProperty());
        leave_table.setItems(sorted);
    }

    /**
     * Approve and Deny are disabled until a row is selected.
     * They are also disabled if the selected request is already resolved.
     */
    private void setupButtonState() {
        approve_button.setDisable(true);
        deny_button   .setDisable(true);

        leave_table.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, selected) -> {
                    boolean selectable = selected != null
                            && selected.getApprovalStatus().equals("Pending");
                    approve_button.setDisable(!selectable);
                    deny_button   .setDisable(!selectable);
                });
    }

    // ── Button actions ────────────────────────────────────────────────────────

    @FXML
    private void handleApprove() {
        LeaveRequest selected = leave_table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        boolean ok = dao.updateStatus(selected.getLeaveId(), "Approved");
        if (ok) {
            selected.setApprovalStatus("Approved");
            leave_table.refresh();
            approve_button.setDisable(true);
            deny_button   .setDisable(true);
            showAlert(Alert.AlertType.INFORMATION, "Approved",
                    "Leave request " + selected.getLeaveId() + " has been approved.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Could not update the record. Please check that the CSV file is accessible.");
        }
    }

    @FXML
    private void handleDeny() {
        LeaveRequest selected = leave_table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        // Ask HR for denial remarks
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Deny Leave Request");
        dialog.setHeaderText("Deny: " + selected.getLeaveId()
                + " — " + selected.getFirstName() + " " + selected.getLastName());
        dialog.setContentText("Enter remarks (reason for denial):");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return;          // HR cancelled the dialog

        String remarks = result.get().trim();
        if (remarks.isEmpty()) remarks = "No remarks provided";

        String newStatus = "Denied: " + remarks;
        boolean ok = dao.updateStatus(selected.getLeaveId(), newStatus);

        if (ok) {
            selected.setApprovalStatus(newStatus);
            leave_table.refresh();
            approve_button.setDisable(true);
            deny_button   .setDisable(true);
            showAlert(Alert.AlertType.INFORMATION, "Denied",
                    "Leave request " + selected.getLeaveId() + " has been denied.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Could not update the record. Please check that the CSV file is accessible.");
        }
    }

    // ── UI helpers ────────────────────────────────────────────────────────────

    /** Colours the Approved? column: green = Approved, red = Denied, grey = Pending. */
    private void styleStatusColumn() {
        approvalStatusColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String value, boolean empty) {
                super.updateItem(value, empty);
                getStyleClass().removeAll(
                        "status-approved", "status-denied", "status-pending");
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(value);
                    if (value.equals("Approved"))        getStyleClass().add("status-approved");
                    else if (value.startsWith("Denied")) getStyleClass().add("status-denied");
                    else                                 getStyleClass().add("status-pending");
                }
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
