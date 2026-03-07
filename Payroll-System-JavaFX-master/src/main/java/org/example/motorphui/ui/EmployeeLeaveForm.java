package org.example.motorphui.ui;

import org.example.motorphui.dao.LeaveRequestDAO;
import org.example.motorphui.model.AllEmployee;
import org.example.motorphui.model.LeaveRequest;
import org.example.motorphui.session.SessionManager;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Controller for the Employee Leave Form screen.
 *
 * Responsibilities:
 *  - Pre-fill employee info from {@link SessionManager}
 *  - Validate all form inputs before submission
 *  - Write the new leave request to motorph_leave_records.csv via {@link LeaveRequestDAO}
 *  - Display the employee's own leave history in a TableView
 */
public class EmployeeLeaveForm {

    // ── FXML injections ──────────────────────────────────────────────────────

    @FXML private AnchorPane root;
    @FXML private Label leaveform_label;

    // Employee info (read-only display)
    @FXML private Label emp_name_label;
    @FXML private Label emp_id_label;

    // Form inputs
    @FXML private ComboBox<String>  leave_type_combo;
    @FXML private DatePicker        start_date_picker;
    @FXML private DatePicker        end_date_picker;
    @FXML private TextArea          reason_field;
    @FXML private Label             days_label;

    // Action
    @FXML private Button submit_button;
    @FXML private Button clear_button;

    // History table
    @FXML private TableView<LeaveRequest>           history_table;
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

    private AllEmployee      currentEmployee;
    private LeaveRequestDAO  dao;
    private ObservableList<LeaveRequest> myLeaveRequests;

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MM/dd/yyyy");

    // ── Initialisation ───────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        dao             = new LeaveRequestDAO();
        currentEmployee = SessionManager.getInstance().getCurrentEmployee();

        if (currentEmployee == null) {
            showAlert(Alert.AlertType.ERROR,
                    "Session Error", "No employee is logged in. Please log in again.");
            submit_button.setDisable(true);
            return;
        }

        populateEmployeeInfo();
        setupLeaveTypeOptions();
        setupDateListeners();
        bindTableColumns();
        loadHistory();
    }

    // ── Setup helpers ────────────────────────────────────────────────────────

    private void populateEmployeeInfo() {
        emp_name_label.setText(
                currentEmployee.getFirstName() + " " + currentEmployee.getLastName());
        emp_id_label.setText("ID: " + currentEmployee.getEmployeeNumber());
    }

    private void setupLeaveTypeOptions() {
        leave_type_combo.getItems().addAll(
                "Vacation Leave",
                "Sick Leave",
                "Emergency Leave",
                "Maternity/Paternity Leave",
                "Bereavement Leave",
                "Unpaid Leave"
        );
        leave_type_combo.setPromptText("Select leave type");
    }

    /**
     * Whenever the start or end date changes, recalculate and display the
     * number of days so the employee sees it before submitting.
     */
    private void setupDateListeners() {
        start_date_picker.valueProperty().addListener((o, ov, nv) -> updateDaysPreview());
        end_date_picker.valueProperty()  .addListener((o, ov, nv) -> updateDaysPreview());
    }

    private void updateDaysPreview() {
        LocalDate start = start_date_picker.getValue();
        LocalDate end   = end_date_picker.getValue();
        if (start != null && end != null && !end.isBefore(start)) {
            long days = ChronoUnit.DAYS.between(start, end) + 1;
            days_label.setText(days + (days == 1 ? " day" : " days"));
        } else {
            days_label.setText("—");
        }
    }

    private void bindTableColumns() {
        leaveIdColumn      .setCellValueFactory(new PropertyValueFactory<>("leaveId"));
        lastNameColumn     .setCellValueFactory(new PropertyValueFactory<>("lastName"));
        firstNameColumn    .setCellValueFactory(new PropertyValueFactory<>("firstName"));
        startDateColumn    .setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn      .setCellValueFactory(new PropertyValueFactory<>("endDate"));
        daysColumn         .setCellValueFactory(new PropertyValueFactory<>("days"));
        leaveTypeColumn    .setCellValueFactory(new PropertyValueFactory<>("leaveType"));
        reasonColumn       .setCellValueFactory(new PropertyValueFactory<>("reason"));
        approvalStatusColumn.setCellValueFactory(new PropertyValueFactory<>("approvalStatus"));

        styleStatusColumn();
    }

    private void loadHistory() {
        myLeaveRequests = dao.getRequestsForEmployee(currentEmployee.getEmployeeNumber());
        SortedList<LeaveRequest> sorted = new SortedList<>(myLeaveRequests);
        sorted.comparatorProperty().bind(history_table.comparatorProperty());
        history_table.setItems(sorted);
    }

    // ── Button actions ────────────────────────────────────────────────────────

    @FXML
    private void handleSubmit() {
        // ── Validation ────────────────────────────────────────────────────────
        if (leave_type_combo.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Incomplete Form", "Please select a leave type.");
            return;
        }
        LocalDate start = start_date_picker.getValue();
        LocalDate end   = end_date_picker.getValue();

        if (start == null || end == null) {
            showAlert(Alert.AlertType.WARNING, "Incomplete Form",
                    "Please select both a start and end date.");
            return;
        }
        if (end.isBefore(start)) {
            showAlert(Alert.AlertType.WARNING, "Invalid Dates",
                    "End date cannot be before start date.");
            return;
        }
        if (start.isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.WARNING, "Invalid Dates",
                    "Start date cannot be in the past.");
            return;
        }

        String reason = reason_field.getText().trim();
        if (reason.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Incomplete Form",
                    "Please provide a reason for your leave request.");
            return;
        }

        // ── Submit ────────────────────────────────────────────────────────────
        LeaveRequest saved = dao.submitLeaveRequest(
                currentEmployee.getEmployeeNumber(),
                currentEmployee.getLastName(),
                currentEmployee.getFirstName(),
                start.format(DATE_FMT),
                end.format(DATE_FMT),
                leave_type_combo.getValue(),
                reason
        );

        if (saved == null) {
            showAlert(Alert.AlertType.ERROR, "Submission Failed",
                    "Could not save your leave request. Please contact IT support.");
            return;
        }

        myLeaveRequests.add(saved);   // live-updates the TableView
        clearForm();

        showAlert(Alert.AlertType.INFORMATION, "Request Submitted",
                "Your leave request (" + saved.getLeaveId()
                        + ") has been submitted and is pending approval.");
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    // ── UI helpers ────────────────────────────────────────────────────────────

    private void clearForm() {
        leave_type_combo  .setValue(null);
        start_date_picker .setValue(null);
        end_date_picker   .setValue(null);
        reason_field      .clear();
        days_label        .setText("—");
    }

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
                    if (value.equals("Approved"))           getStyleClass().add("status-approved");
                    else if (value.startsWith("Denied"))    getStyleClass().add("status-denied");
                    else                                    getStyleClass().add("status-pending");
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
