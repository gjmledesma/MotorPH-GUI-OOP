package org.example.motorphui.ui;

import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.example.motorphui.dao.TicketRequestDAO;
import org.example.motorphui.model.AllEmployee;
import org.example.motorphui.model.TicketRequest;
import org.example.motorphui.session.SessionManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Controller for the Employee Ticket Requests screen.
 *
 * Responsibilities:
 *   1. Allow the logged-in employee to submit a new IT support ticket.
 *   2. Display only the current employee's ticket history.
 */
public class EmployeeTicketRequests {

    // ── FXML fields ────────────────────────────────────────────────────────────

    @FXML private AnchorPane root;
    @FXML private Label ticketrequests_label;
    @FXML private Label emp_name_label;
    @FXML private Label emp_id_label;

    // Form
    @FXML private ComboBox<String>  category_combo;
    @FXML private TextField         subject_field;
    @FXML private TextArea          description_area;
    @FXML private Button            submit_button;
    @FXML private Button            clear_button;

    // History table
    @FXML private TableView<TicketRequest>           history_table;
    @FXML private TableColumn<TicketRequest, String> ticketIdColumn;
    @FXML private TableColumn<TicketRequest, String> categoryColumn;
    @FXML private TableColumn<TicketRequest, String> subjectColumn;
    @FXML private TableColumn<TicketRequest, String> dateFiledColumn;
    @FXML private TableColumn<TicketRequest, String> statusColumn;
    @FXML private TableColumn<TicketRequest, String> remarksColumn;

    // ── Private state ──────────────────────────────────────────────────────────

    private final TicketRequestDAO dao = new TicketRequestDAO();
    private AllEmployee currentEmployee;

    // ── Initialization ─────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        // Load the currently logged-in employee from the session
        currentEmployee = SessionManager.getInstance().getCurrentEmployee();

        // Populate employee labels
        if (currentEmployee != null) {
            emp_name_label.setText(
                currentEmployee.getFirstName() + " " + currentEmployee.getLastName());
            emp_id_label.setText("ID: " + currentEmployee.getEmployeeNumber());
        }

        // Category combo items
        category_combo.setItems(FXCollections.observableArrayList(TicketRequest.CATEGORIES));

        // Wire table columns
        ticketIdColumn.setCellValueFactory(c -> c.getValue().ticketIdProperty());
        categoryColumn.setCellValueFactory(c -> c.getValue().categoryProperty());
        subjectColumn .setCellValueFactory(c -> c.getValue().subjectProperty());
        dateFiledColumn.setCellValueFactory(c -> c.getValue().dateFiledProperty());
        statusColumn  .setCellValueFactory(c -> c.getValue().statusProperty());
        remarksColumn .setCellValueFactory(c -> c.getValue().itRemarksProperty());

        // Colour-code the Status column
        statusColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status) {
                        case TicketRequest.STATUS_OPEN        -> setStyle("-fx-text-fill: #C5172E; -fx-font-weight: bold;");
                        case TicketRequest.STATUS_IN_PROGRESS -> setStyle("-fx-text-fill: #E67E00; -fx-font-weight: bold;");
                        case TicketRequest.STATUS_RESOLVED    -> setStyle("-fx-text-fill: #28A745; -fx-font-weight: bold;");
                        case TicketRequest.STATUS_CLOSED      -> setStyle("-fx-text-fill: #6C757D; -fx-font-weight: bold;");
                        default                               -> setStyle("");
                    }
                }
            }
        });

        loadHistory();
    }

    // ── Handlers ───────────────────────────────────────────────────────────────

    @FXML
    private void handleSubmit() {
        if (currentEmployee == null) {
            showAlert(Alert.AlertType.ERROR, "Session Error",
                    "No employee session found. Please log in again.");
            return;
        }

        String category    = category_combo.getValue();
        String subject     = subject_field.getText().trim();
        String description = description_area.getText().trim();

        // Validation
        if (category == null || category.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Missing Field", "Please select a category.");
            return;
        }
        if (subject.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Field", "Please enter a subject.");
            return;
        }
        if (description.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Field", "Please enter a description.");
            return;
        }

        String dateFiled = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));

        TicketRequest saved = dao.submitTicket(
            currentEmployee.getEmployeeNumber(),
            currentEmployee.getLastName(),
            currentEmployee.getFirstName(),
            category, subject, description, dateFiled
        );

        if (saved != null) {
            showAlert(Alert.AlertType.INFORMATION, "Ticket Submitted",
                    "Your ticket has been filed successfully.\nTicket ID: " + saved.getTicketId());
            handleClear();
            loadHistory();
        } else {
            showAlert(Alert.AlertType.ERROR, "Submission Failed",
                    "Could not save your ticket. Please try again.");
        }
    }

    @FXML
    private void handleClear() {
        category_combo.setValue(null);
        subject_field.clear();
        description_area.clear();
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    private void loadHistory() {
        if (currentEmployee == null) return;
        var tickets = dao.getTicketsForEmployee(currentEmployee.getEmployeeNumber());
        SortedList<TicketRequest> sorted = new SortedList<>(tickets);
        sorted.comparatorProperty().bind(history_table.comparatorProperty());
        history_table.setItems(sorted);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
