package org.example.motorphui.ui;

import org.example.motorphui.dao.EmployeeAttendanceDAO;
import org.example.motorphui.model.AllEmployee;
import org.example.motorphui.model.AttendanceRecord;
import org.example.motorphui.session.SessionManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller for the Employee Attendance screen.
 *
 * Responsibilities:
 *  - Identify the currently logged-in employee via {@link SessionManager}
 *  - Display a live clock and today's date
 *  - Allow exactly one Time-In and one Time-Out per calendar day
 *  - Persist both actions to the shared CSV through {@link EmployeeAttendanceDAO}
 *  - Show the employee's full attendance history in a sortable TableView
 */
public class EmployeeAttendance {

    // ── FXML injections ──────────────────────────────────────────────────────

    @FXML private AnchorPane root;

    // Header info
    @FXML private Label attendance_label;
    @FXML private Label emp_name_label;
    @FXML private Label emp_id_label;
    @FXML private Label date_label;
    @FXML private Label clock_label;

    // Action area
    @FXML private Button time_in_button;
    @FXML private Button time_out_button;
    @FXML private Label  status_label;

    // History table
    @FXML private TableView<AttendanceRecord>           attendance_table;
    @FXML private TableColumn<AttendanceRecord, String> empNumColumn;
    @FXML private TableColumn<AttendanceRecord, String> lastNameColumn;
    @FXML private TableColumn<AttendanceRecord, String> firstNameColumn;
    @FXML private TableColumn<AttendanceRecord, String> dateColumn;
    @FXML private TableColumn<AttendanceRecord, String> logInColumn;
    @FXML private TableColumn<AttendanceRecord, String> logOutColumn;
    @FXML private TableColumn<AttendanceRecord, String> statusColumn;

    // ── State ────────────────────────────────────────────────────────────────

    private AllEmployee              currentEmployee;
    private EmployeeAttendanceDAO    dao;
    private ObservableList<AttendanceRecord> recordList;

    // Track the session state for today
    private boolean hasTimedInToday  = false;
    private boolean hasTimedOutToday = false;

    private static final DateTimeFormatter DATE_DISPLAY =
            DateTimeFormatter.ofPattern("MMMM dd, yyyy");
    private static final DateTimeFormatter TIME_DISPLAY =
            DateTimeFormatter.ofPattern("hh:mm:ss a");

    // ── Initialisation ───────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        dao             = new EmployeeAttendanceDAO();
        currentEmployee = SessionManager.getInstance().getCurrentEmployee();

        if (currentEmployee == null) {
            showAlert(Alert.AlertType.ERROR,
                    "Session Error",
                    "No employee is logged in. Please log in again.");
            disableButtons();
            return;
        }

        populateEmployeeInfo();
        startLiveClock();
        bindTableColumns();
        loadHistory();
        refreshDayState();
    }

    // ── Setup helpers ────────────────────────────────────────────────────────

    private void populateEmployeeInfo() {
        emp_name_label.setText(
        currentEmployee.getFirstName() + " " + currentEmployee.getLastName());
        emp_id_label.setText("ID: " + currentEmployee.getEmployeeNumber());
        date_label.setText(LocalDate.now().format(DATE_DISPLAY));
    }

    /** Ticks the clock label every second. */
    private void startLiveClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e ->
                clock_label.setText(LocalTime.now().format(TIME_DISPLAY))));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
        // Set immediately so there is no 1-second blank
        clock_label.setText(LocalTime.now().format(TIME_DISPLAY));
    }

    private void bindTableColumns() {
        empNumColumn  .setCellValueFactory(new PropertyValueFactory<>("empNumber"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        dateColumn    .setCellValueFactory(new PropertyValueFactory<>("date"));
        logInColumn   .setCellValueFactory(new PropertyValueFactory<>("logIn"));
        logOutColumn  .setCellValueFactory(new PropertyValueFactory<>("logOut"));
        statusColumn  .setCellValueFactory(new PropertyValueFactory<>("status"));

        styleStatusColumn();
    }

    /** Loads the employee's attendance history from the CSV into the TableView. */
    private void loadHistory() {
        recordList = dao.getRecordsForEmployee(currentEmployee.getEmployeeNumber());

        // Make the table sortable while keeping the underlying list stable
        SortedList<AttendanceRecord> sorted = new SortedList<>(recordList);
        sorted.comparatorProperty().bind(attendance_table.comparatorProperty());
        attendance_table.setItems(sorted);
    }

    /**
     * Checks today's existing record (if any) so the buttons reflect the
     * correct state even when the user navigates away and comes back.
     */
    private void refreshDayState() {
        AttendanceRecord today = dao.getTodayRecord(currentEmployee.getEmployeeNumber());

        if (today == null) {
            // No record yet — only Time-In is available
            hasTimedInToday  = false;
            hasTimedOutToday = false;
            setStatus("Not yet timed in today.", "status-idle");

        } else if (today.getLogOut().isBlank()) {
            // Timed in but not out
            hasTimedInToday  = true;
            hasTimedOutToday = false;
            setStatus("Timed in at " + today.getLogIn() + ". Ready to time out.", "status-in");

        } else {
            // Both recorded
            hasTimedInToday  = true;
            hasTimedOutToday = true;
            setStatus("Timed in at " + today.getLogIn()
                    + " · Timed out at " + today.getLogOut(), "status-done");
        }

        syncButtonState();
    }

    // ── Button actions ───────────────────────────────────────────────────────

    @FXML
    private void handleTimeIn() {
        if (hasTimedInToday) {
            showAlert(Alert.AlertType.WARNING,
                    "Already Timed In",
                    "You have already timed in today. You cannot time in again.");
            return;
        }

        AttendanceRecord record = dao.writeTimeIn(
                currentEmployee.getEmployeeNumber(),
                currentEmployee.getLastName(),
                currentEmployee.getFirstName());

        if (record == null) {
            showAlert(Alert.AlertType.ERROR,
                    "Time-In Failed",
                    "Could not save your time-in. Please contact IT support.");
            return;
        }

        hasTimedInToday = true;
        recordList.add(record);                        // live-update the TableView
        setStatus("Timed in at " + record.getLogIn() + ". Have a great day!", "status-in");
        syncButtonState();
    }

    @FXML
    private void handleTimeOut() {
        if (!hasTimedInToday) {
            showAlert(Alert.AlertType.WARNING,
                    "Not Timed In",
                    "You have not timed in today. Please time in first.");
            return;
        }
        if (hasTimedOutToday) {
            showAlert(Alert.AlertType.WARNING,
                    "Already Timed Out",
                    "You have already timed out today.");
            return;
        }

        AttendanceRecord updated = dao.writeTimeOut(currentEmployee.getEmployeeNumber());

        if (updated == null) {
            showAlert(Alert.AlertType.ERROR,
                    "Time-Out Failed",
                    "Could not save your time-out. Please contact IT support.");
            return;
        }

        hasTimedOutToday = true;

        // Replace the open record in the list with the completed one
        for (int i = 0; i < recordList.size(); i++) {
            AttendanceRecord r = recordList.get(i);
            if (r.getDate().equals(updated.getDate()) && r.getLogOut().isBlank()) {
                recordList.set(i, updated);
                break;
            }
        }

        setStatus("Timed out at " + updated.getLogOut() + ". See you tomorrow!", "status-done");
        syncButtonState();
    }

    // ── UI state helpers ─────────────────────────────────────────────────────

    /**
     * Enables / disables buttons based on the current day's state:
     *  - Before time-in  → Time-In enabled, Time-Out disabled
     *  - After time-in   → Time-In disabled, Time-Out enabled
     *  - After time-out  → both disabled (day is complete)
     */
    private void syncButtonState() {
        time_in_button .setDisable(hasTimedInToday);
        time_out_button.setDisable(!hasTimedInToday || hasTimedOutToday);
    }

    private void disableButtons() {
        time_in_button .setDisable(true);
        time_out_button.setDisable(true);
    }

    /**
     * Updates the status label text and swaps its CSS style class so the
     * stylesheet can colour it differently per state.
     */
    private void setStatus(String message, String styleClass) {
        status_label.setText(message);
        status_label.getStyleClass().removeAll("status-idle", "status-in", "status-done");
        status_label.getStyleClass().add(styleClass);
    }

    /** Colours the Status column cells: green for On-Time, red for Late. */
    private void styleStatusColumn() {
        statusColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String value, boolean empty) {
                super.updateItem(value, empty);
                getStyleClass().removeAll("status-on-time", "status-late");
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(value);
                    getStyleClass().add(
                            "On Time".equals(value) ? "status-on-time" : "status-late");
                }
            }
        });
    }

    // ── Utility ──────────────────────────────────────────────────────────────

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}