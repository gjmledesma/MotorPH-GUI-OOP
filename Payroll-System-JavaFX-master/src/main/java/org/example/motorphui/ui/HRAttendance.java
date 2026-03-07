package org.example.motorphui.ui;

import org.example.motorphui.dao.NewAttendanceDAO;
import org.example.motorphui.model.AttendanceRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HRAttendance {

    // ── FXML Injections ──────────────────────────────────────────────────────

    @FXML private AnchorPane root;

    @FXML private TableView<AttendanceRecord>              attendance_table;
    @FXML private TableColumn<AttendanceRecord, String>    empNumColumn;
    @FXML private TableColumn<AttendanceRecord, String>    lastNameColumn;
    @FXML private TableColumn<AttendanceRecord, String>    firstNameColumn;
    @FXML private TableColumn<AttendanceRecord, String>    dateColumn;
    @FXML private TableColumn<AttendanceRecord, String>    logInColumn;
    @FXML private TableColumn<AttendanceRecord, String>    logOutColumn;
    @FXML private TableColumn<AttendanceRecord, String>    statusColumn;

    @FXML private DatePicker  data_picker;
    @FXML private TextField   search_field;

    // ── State ────────────────────────────────────────────────────────────────

    // Master list – loaded once from the CSV
    private ObservableList<AttendanceRecord> masterList;

    // Wraps master list; predicate is updated on every search/filter change
    private FilteredList<AttendanceRecord>   filteredList;

    // CSV date format: MM/dd/yyyy  →  matches the picker's default ISO format
    private static final DateTimeFormatter CSV_DATE_FMT =
            DateTimeFormatter.ofPattern("MM/dd/yyyy");

    // ── Initialisation ───────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        bindColumns();
        loadData();
        setupFilters();
        styleStatusColumn();
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    /** Binds each TableColumn to the matching property on AttendanceRecord. */
    private void bindColumns() {
        empNumColumn  .setCellValueFactory(new PropertyValueFactory<>("empNumber"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        dateColumn    .setCellValueFactory(new PropertyValueFactory<>("date"));
        logInColumn   .setCellValueFactory(new PropertyValueFactory<>("logIn"));
        logOutColumn  .setCellValueFactory(new PropertyValueFactory<>("logOut"));
        statusColumn  .setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    /** Reads all records from the CSV via AttendanceDAO. */
    private void loadData() {
        NewAttendanceDAO dao = new NewAttendanceDAO();
        masterList = dao.getAllAttendanceRecords();
    }

    /**
     * Wraps masterList in a FilteredList, then wires the search field and
     * date picker so the predicate refreshes whenever either control changes.
     */
    private void setupFilters() {
        filteredList = new FilteredList<>(masterList, r -> true);

        // Re-apply the predicate every time the search text changes
        search_field.textProperty().addListener(
                (obs, oldVal, newVal) -> applyFilters());

        // Re-apply when a date is chosen or cleared
        data_picker.valueProperty().addListener(
                (obs, oldVal, newVal) -> applyFilters());

        // SortedList keeps the TableView's column-sort working correctly
        SortedList<AttendanceRecord> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(attendance_table.comparatorProperty());

        attendance_table.setItems(sortedList);
    }

    /**
     * Builds a compound predicate from the current search text and selected date,
     * then assigns it to the FilteredList.
     */
    private void applyFilters() {
        String  keyword      = search_field.getText().trim().toLowerCase();
        LocalDate pickedDate = data_picker.getValue();

        filteredList.setPredicate(record -> {
            // ── Date filter ──────────────────────────────────────────────────
            if (pickedDate != null) {
                try {
                    LocalDate recordDate = LocalDate.parse(record.getDate(), CSV_DATE_FMT);
                    if (!recordDate.equals(pickedDate)) return false;
                } catch (Exception e) {
                    return false; // unparseable date → hide row
                }
            }

            // ── Search filter (employee number, first name, last name) ───────
            if (!keyword.isEmpty()) {
                boolean matchesEmpNum    = record.getEmpNumber().toLowerCase().contains(keyword);
                boolean matchesLastName  = record.getLastName().toLowerCase().contains(keyword);
                boolean matchesFirstName = record.getFirstName().toLowerCase().contains(keyword);
                return matchesEmpNum || matchesLastName || matchesFirstName;
            }

            return true; // no keyword → row passes
        });
    }

    /**
     * Adds conditional CSS style classes to the Status column so "Late" rows
     * are visually distinguishable from "On Time" rows.
     */
    private void styleStatusColumn() {
        statusColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                getStyleClass().removeAll("status-on-time", "status-late");

                if (empty || status == null) {
                    setText(null);
                } else {
                    setText(status);
                    getStyleClass().add(
                            "On Time".equals(status) ? "status-on-time" : "status-late");
                }
            }
        });
    }
}