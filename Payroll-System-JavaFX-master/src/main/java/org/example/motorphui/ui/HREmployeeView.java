package org.example.motorphui.ui;

import org.example.motorphui.dao.EmployeeDAOImpl;
import org.example.motorphui.dao.IEmployeeDAO;
import org.example.motorphui.model.AllEmployee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

/**
 * HR Employee View controller.
 *
 * Refactored to delegate all CSV reading and writing to {@link EmployeeDAOImpl}
 * (via the {@link IEmployeeDAO} interface) instead of containing raw file I/O.
 *
 * OOP PRINCIPLES DEMONSTRATED:
 *   ENCAPSULATION — The DAO reference is private; UI logic does not touch CSV files.
 *   ABSTRACTION   — Depends on IEmployeeDAO, not on the concrete implementation.
 *   LAYERED ARCHITECTURE — UI layer delegates data operations to DAO layer.
 */
public class HREmployeeView {

    @FXML private AnchorPane root;
    @FXML private TableView<AllEmployee> emp_table;
    @FXML private Label emp_info_label;
    @FXML private Button viewandupdate_button;
    @FXML private Button addemp_button;
    @FXML private Button deleteemp_button;

    @FXML private TableColumn<AllEmployee, String> empNumColumn;
    @FXML private TableColumn<AllEmployee, String> lastNameColumn;
    @FXML private TableColumn<AllEmployee, String> firstNameColumn;
    @FXML private TableColumn<AllEmployee, String> birthdayColumn;
    @FXML private TableColumn<AllEmployee, String> addressColumn;
    @FXML private TableColumn<AllEmployee, String> phoneNumberColumn;
    @FXML private TableColumn<AllEmployee, String> sssColumn;
    @FXML private TableColumn<AllEmployee, String> philHealthColumn;
    @FXML private TableColumn<AllEmployee, String> tinColumn;
    @FXML private TableColumn<AllEmployee, String> pagIbigColumn;
    @FXML private TableColumn<AllEmployee, String> statusColumn;
    @FXML private TableColumn<AllEmployee, String> positionColumn;
    @FXML private TableColumn<AllEmployee, String> supervisorColumn;
    @FXML private TableColumn<AllEmployee, String> basicSalaryColumn;
    @FXML private TableColumn<AllEmployee, String> riceSubsidyColumn;
    @FXML private TableColumn<AllEmployee, String> phoneAllowanceColumn;
    @FXML private TableColumn<AllEmployee, String> clothingAllowanceColumn;
    @FXML private TableColumn<AllEmployee, String> grossSemiMonthlyColumn;
    @FXML private TableColumn<AllEmployee, String> hourlyRateColumn;

    // ── DAO dependency (UI no longer reads/writes CSV directly) ───────────────
    private final IEmployeeDAO employeeDAO = new EmployeeDAOImpl();
    private final ObservableList<AllEmployee> employeeList = FXCollections.observableArrayList();

    // Kept for backward-compat with AddEmployee / HRViewAndUpdateEmployee
//    public static final String EMPLOYEE_DATA_FILE =
//            "/org/example/motorphui/data/motorph_employee_data.csv";

    // ── Init ──────────────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        emp_table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        empNumColumn         .setCellValueFactory(new PropertyValueFactory<>("employeeNumber"));
        lastNameColumn       .setCellValueFactory(new PropertyValueFactory<>("lastName"));
        firstNameColumn      .setCellValueFactory(new PropertyValueFactory<>("firstName"));
        birthdayColumn       .setCellValueFactory(new PropertyValueFactory<>("birthday"));
        addressColumn        .setCellValueFactory(new PropertyValueFactory<>("address"));
        phoneNumberColumn    .setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        sssColumn            .setCellValueFactory(new PropertyValueFactory<>("sss"));
        philHealthColumn     .setCellValueFactory(new PropertyValueFactory<>("philHealth"));
        tinColumn            .setCellValueFactory(new PropertyValueFactory<>("tin"));
        pagIbigColumn        .setCellValueFactory(new PropertyValueFactory<>("pagIbig"));
        statusColumn         .setCellValueFactory(new PropertyValueFactory<>("status"));
        positionColumn       .setCellValueFactory(new PropertyValueFactory<>("position"));
        supervisorColumn     .setCellValueFactory(new PropertyValueFactory<>("immediateSupervisor"));
        basicSalaryColumn    .setCellValueFactory(new PropertyValueFactory<>("basicSalary"));
        riceSubsidyColumn    .setCellValueFactory(new PropertyValueFactory<>("riceSubsidy"));
        phoneAllowanceColumn .setCellValueFactory(new PropertyValueFactory<>("phoneAllowance"));
        clothingAllowanceColumn.setCellValueFactory(new PropertyValueFactory<>("clothingAllowance"));
        grossSemiMonthlyColumn .setCellValueFactory(new PropertyValueFactory<>("grossSemiMonthlyRate"));
        hourlyRateColumn     .setCellValueFactory(new PropertyValueFactory<>("hourlyRate"));

        loadEmployees();
    }

    // ── Load data from DAO ────────────────────────────────────────────────────

    private void loadEmployees() {
        employeeList.clear();
        employeeList.addAll(employeeDAO.getAllEmployees());
        emp_table.setItems(employeeList);
    }

    // ── Public API used by child windows ─────────────────────────────────────

    public Set<String> getExistingEmpNumbers() {
        return employeeDAO.getExistingEmployeeNumbers();
    }

    public void addEmployee(AllEmployee employee) {
        employeeDAO.addEmployee(employee);
        refreshTable();
    }

    public void updateEmployee(AllEmployee updated) {
        employeeDAO.updateEmployee(updated);
        refreshTable();
    }

    public void refreshTable() {
        loadEmployees();
    }

    // ── Button handlers ───────────────────────────────────────────────────────

    @FXML
    public void handleViewAndUpdateButton() {
        AllEmployee selected = emp_table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarningAlert("No Selection", "Please select an employee row first.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/org/example/motorphui/hr_view_and_update_employee.fxml"));
            Parent parent = loader.load();
            HRViewAndUpdateEmployee ctrl = loader.getController();
            ctrl.setEmployee(selected);
            ctrl.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("View and Update Employee");
            stage.setScene(new Scene(parent));
            stage.setResizable(false);
            stage.showAndWait();
            refreshTable();
        } catch (IOException e) {
            showErrorAlert("Error", "Could not open the update window.", e.getMessage());
        }
    }

    @FXML
    public void openAddEmployeeWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/org/example/motorphui/add_employee.fxml"));
            Parent root = loader.load();
            AddEmployee ctrl = loader.getController();
            ctrl.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Add New Employee");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            showErrorAlert("Load Error", "Failed to open Add Employee form.", e.getMessage());
        }
    }

    @FXML
    private void handleDeleteEmployeeButton() {
        AllEmployee selected = emp_table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarningAlert("No Selection", "Please select an employee in the table to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete: " + selected.getFirstName() + " " + selected.getLastName() + "?");
        confirm.setContentText("This action cannot be undone. Are you sure?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            employeeDAO.deleteEmployee(selected.getEmployeeNumber());
            refreshTable();

            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setTitle("Deleted");
            ok.setHeaderText(null);
            ok.setContentText("Employee record deleted successfully.");
            ok.showAndWait();
        }
    }

    // ── Alert helpers ─────────────────────────────────────────────────────────

    private void showErrorAlert(String title, String header, String content) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(header);
        a.setContentText(content);
        a.showAndWait();
    }

    private void showWarningAlert(String title, String content) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }
}
