package org.example.motorphui.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.example.motorphui.dao.TicketRequestDAO;
import org.example.motorphui.model.TicketRequest;

/**
 * Controller for the IT Admin Ticket Requests screen.
 *
 * Responsibilities:
 *   1. Display all submitted ticket records from the CSV.
 *   2. Allow IT admin to update a ticket's status and add remarks.
 *
 * ENCAPSULATION – All @FXML fields are private; updates delegated to DAO.
 * ABSTRACTION   – Hides CSV I/O behind {@link TicketRequestDAO}.
 */
public class ITAdminTicketRequests {

    // ── FXML fields — main table ───────────────────────────────────────────────

    @FXML private AnchorPane root;
    @FXML private Label ticketrequests_label;

    @FXML private TableView<TicketRequest>           ticket_table;
    @FXML private TableColumn<TicketRequest, String> ticketIdColumn;
    @FXML private TableColumn<TicketRequest, String> empIdColumn;
    @FXML private TableColumn<TicketRequest, String> lastNameColumn;
    @FXML private TableColumn<TicketRequest, String> firstNameColumn;
    @FXML private TableColumn<TicketRequest, String> categoryColumn;
    @FXML private TableColumn<TicketRequest, String> subjectColumn;
    @FXML private TableColumn<TicketRequest, String> dateFiledColumn;
    @FXML private TableColumn<TicketRequest, String> statusColumn;

    // ── FXML fields — detail / update panel ───────────────────────────────────

    @FXML private Label   detail_ticket_id_label;
    @FXML private Label   detail_employee_label;
    @FXML private Label   detail_category_label;
    @FXML private Label   detail_subject_label;
    @FXML private TextArea detail_description_area;

    @FXML private ComboBox<String> status_combo;
    @FXML private TextField        remarks_field;
    @FXML private Button           update_button;

    // ── Private state ──────────────────────────────────────────────────────────

    private final TicketRequestDAO dao = new TicketRequestDAO();
    private ObservableList<TicketRequest> allTickets;

    // ── Initialization ─────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        // Wire table columns
        ticketIdColumn  .setCellValueFactory(c -> c.getValue().ticketIdProperty());
        empIdColumn     .setCellValueFactory(c -> c.getValue().employeeIdProperty());
        lastNameColumn  .setCellValueFactory(c -> c.getValue().lastNameProperty());
        firstNameColumn .setCellValueFactory(c -> c.getValue().firstNameProperty());
        categoryColumn  .setCellValueFactory(c -> c.getValue().categoryProperty());
        subjectColumn   .setCellValueFactory(c -> c.getValue().subjectProperty());
        dateFiledColumn .setCellValueFactory(c -> c.getValue().dateFiledProperty());
        statusColumn    .setCellValueFactory(c -> c.getValue().statusProperty());

        // Colour-code Status column
        statusColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null); setStyle("");
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

        // Status combo for update panel
        status_combo.setItems(FXCollections.observableArrayList(
            TicketRequest.STATUS_OPEN,
            TicketRequest.STATUS_IN_PROGRESS,
            TicketRequest.STATUS_RESOLVED,
            TicketRequest.STATUS_CLOSED
        ));

        // When a row is selected, populate the detail panel
        ticket_table.getSelectionModel().selectedItemProperty()
            .addListener((obs, prev, selected) -> populateDetail(selected));

        // Start with detail panel blank
        clearDetail();

        loadAllTickets();
    }

    // ── Handlers ───────────────────────────────────────────────────────────────

    @FXML
    private void handleUpdate() {
        TicketRequest selected = ticket_table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection",
                    "Please select a ticket from the table to update.");
            return;
        }

        String newStatus = status_combo.getValue();
        String remarks   = remarks_field.getText().trim();

        if (newStatus == null || newStatus.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Missing Status",
                    "Please select a status before updating.");
            return;
        }

        boolean ok = dao.updateTicket(selected.getTicketId(), newStatus, remarks);
        if (ok) {
            showAlert(Alert.AlertType.INFORMATION, "Updated",
                    "Ticket " + selected.getTicketId() + " has been updated.");
            loadAllTickets();
            clearDetail();
        } else {
            showAlert(Alert.AlertType.ERROR, "Update Failed",
                    "Could not update the ticket. Please try again.");
        }
    }

    @FXML
    private void handleRefresh() {
        loadAllTickets();
        clearDetail();
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    private void loadAllTickets() {
        allTickets = dao.getAllTickets();
        SortedList<TicketRequest> sorted = new SortedList<>(allTickets);
        sorted.comparatorProperty().bind(ticket_table.comparatorProperty());
        ticket_table.setItems(sorted);
    }

    private void populateDetail(TicketRequest t) {
        if (t == null) { clearDetail(); return; }
        detail_ticket_id_label.setText(t.getTicketId());
        detail_employee_label .setText(t.getFullName() + "  (ID: " + t.getEmployeeId() + ")");
        detail_category_label .setText(t.getCategory());
        detail_subject_label  .setText(t.getSubject());
        detail_description_area.setText(t.getDescription());
        status_combo  .setValue(t.getStatus());
        remarks_field .setText(t.getItRemarks());
    }

    private void clearDetail() {
        detail_ticket_id_label.setText("—");
        detail_employee_label .setText("—");
        detail_category_label .setText("—");
        detail_subject_label  .setText("—");
        detail_description_area.clear();
        status_combo  .setValue(null);
        remarks_field .clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
