package org.example.motorphui.ui;

import org.example.motorphui.dao.AttendanceDAOImpl;
import org.example.motorphui.dao.EmployeeDAOImpl;
import org.example.motorphui.dao.IAttendanceDAO;
import org.example.motorphui.dao.IEmployeeDAO;
import org.example.motorphui.model.AllEmployee;

import java.io.IOException;
import java.time.Year;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 * Finance – Payroll view controller.
 *
 * All CSV I/O is delegated to the DAO layer:
 *   - Employee data  → {@link EmployeeDAOImpl} via {@link IEmployeeDAO}
 *   - Attendance data → {@link AttendanceDAOImpl} via {@link IAttendanceDAO}
 *
 * This class contains zero BufferedReader / file-access code.
 *
 * AUTO-REFRESH: the employee table reloads every time this window regains
 * focus, so Finance always reflects the latest HR edits without any coupling
 * between the two controllers.
 */
public class FinancePayroll {

    @FXML private TableView<AllEmployee> emp_table;
    @FXML private TableColumn<AllEmployee, String> empNumColumn;
    @FXML private TableColumn<AllEmployee, String> lastNameColumn;
    @FXML private TableColumn<AllEmployee, String> firstNameColumn;
    @FXML private TableColumn<AllEmployee, String> sssColumn;
    @FXML private TableColumn<AllEmployee, String> philHealthColumn;
    @FXML private TableColumn<AllEmployee, String> tinColumn;
    @FXML private TableColumn<AllEmployee, String> pagIbigColumn;

    // ── DAO dependencies ──────────────────────────────────────────────────────
    private final IEmployeeDAO  employeeDAO   = new EmployeeDAOImpl();
    private final IAttendanceDAO attendanceDAO = new AttendanceDAOImpl();

    private final ObservableList<AllEmployee> employeeData = FXCollections.observableArrayList();

    // ── Init ──────────────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        empNumColumn    .setCellValueFactory(cellData -> cellData.getValue().employeeNumberProperty());
        lastNameColumn  .setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        firstNameColumn .setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        sssColumn       .setCellValueFactory(cellData -> cellData.getValue().sssProperty());
        philHealthColumn.setCellValueFactory(cellData -> cellData.getValue().philHealthProperty());
        tinColumn       .setCellValueFactory(cellData -> cellData.getValue().tinProperty());
        pagIbigColumn   .setCellValueFactory(cellData -> cellData.getValue().pagIbigProperty());

        loadEmployees();

        // AUTO-REFRESH: reload whenever this window regains focus
        emp_table.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) return;
            newScene.windowProperty().addListener((wObs, oldWindow, newWindow) -> {
                if (newWindow == null) return;
                newWindow.focusedProperty().addListener((fObs, wasFocused, isNowFocused) -> {
                    if (isNowFocused) refreshTable();
                });
            });
        });
    }

    // ── Data loading ──────────────────────────────────────────────────────────

    private void loadEmployees() {
        employeeData.clear();
        employeeData.addAll(employeeDAO.getAllEmployees());
        emp_table.setItems(employeeData);
    }

    public void refreshTable() {
        loadEmployees();
    }

    // ── Payroll generation ────────────────────────────────────────────────────

    private void generatePayrollForEmployee(AllEmployee employee) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/motorphui/hr_payslip.fxml"));
            Parent root = loader.load();

            FinancePayslip controller = loader.getController();
            controller.setEmployee(employee);

            Stage stage = new Stage();
            stage.setTitle("Payroll Slip");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onGeneratePayroll() {
        AllEmployee selected = emp_table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            generatePayrollForEmployee(selected);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select an employee to generate payroll.");
            alert.showAndWait();
        }
    }

    // ── Attendance ────────────────────────────────────────────────────────────

    /**
     * Returns the total hours worked by {@code empNumber} in the given month.
     *
     * Delegates entirely to {@link IAttendanceDAO#getMonthlyHours}.
     * No file I/O occurs in this class.
     *
     * @param empNumber  the employee ID string
     * @param monthName  full month name, e.g. {@code "January"}
     */
    private double calculateMonthlyHours(String empNumber, String monthName) {
        String currentYear = String.valueOf(Year.now().getValue());
        return attendanceDAO.getMonthlyHours(empNumber, monthName, currentYear);
    }
}