package org.example.motorphui.ui;

import org.example.motorphui.model.AllEmployeePublic;
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

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

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

    private final ObservableList<AllEmployee> employeeList = FXCollections.observableArrayList();

    // Classpath path — used for reading only
    public static final String EMPLOYEE_DATA_FILE =
            "/org/example/motorphui/data/motorph_employee_data.csv";

    private static final String CSV_HEADER =
            "Employee #,Last Name,First Name,Birthday,Address,Phone Number," +
            "SSS #,PhilHealth #,TIN #,Pag-Ibig #,Status,Position," +
            "Immediate Supervisor,Basic Salary,Rice Subsidy,Phone Allowance," +
            "Clothing Allowance,Gross Semi-monthly Rate,Hourly Rate";

    // ── Init ──────────────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        // ── Horizontal scroll: UNCONSTRAINED so all 19 columns are reachable ──
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
        grossSemiMonthlyColumn.setCellValueFactory(new PropertyValueFactory<>("grossSemiMonthlyRate"));
        hourlyRateColumn     .setCellValueFactory(new PropertyValueFactory<>("hourlyRate"));

        loadEmployeesFromCSV();
    }

    // ── CSV read ──────────────────────────────────────────────────────────────

    private void loadEmployeesFromCSV() {
        employeeList.clear();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream(EMPLOYEE_DATA_FILE),
                StandardCharsets.UTF_8))) {

            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] d = line.split(",", -1);
                if (d.length == 19) {
                    employeeList.add(new AllEmployeePublic(
                            d[0], d[1], d[2], d[3], d[4], d[5], d[6], d[7], d[8],
                            d[9], d[10], d[11], d[12], d[13], d[14], d[15], d[16],
                            d[17], d[18]));
                }
            }
            emp_table.setItems(employeeList);

        } catch (IOException e) {
            showErrorAlert("Load Error", "Failed to load employee data.", e.getMessage());
        }
    }

    // ── CSV write — THE FIX ───────────────────────────────────────────────────

    /**
     * Resolves the classpath resource path to a real on-disk {@link File}
     * so that {@link FileWriter} can write to it.
     *
     * Root cause of the original bug: {@code new FileWriter(classpathString)}
     * treats the classpath string as a literal OS path, which does not exist.
     * We must resolve via {@code getClass().getResource()} first.
     */
    private File resolveCSVFile() {
        try {
            URL url = getClass().getResource(EMPLOYEE_DATA_FILE);
            if (url != null && "file".equals(url.getProtocol())) {
                return new File(url.toURI());
            }
        } catch (Exception e) {
            System.err.println("[HREmployeeView] Cannot resolve CSV: " + e.getMessage());
        }
        showErrorAlert("File Error",
                "Cannot locate employee data file on disk.",
                "Make sure the project is run from an IDE/Gradle, not from inside a JAR.\n"
                        + "Path: " + EMPLOYEE_DATA_FILE);
        return null;
    }

    private void saveEmployeesToCSV() {
        File file = resolveCSVFile();
        if (file == null) return; // error already shown

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(file, StandardCharsets.UTF_8))) {

            writer.write(CSV_HEADER);
            writer.newLine();

            for (AllEmployee emp : employeeList) {
                writer.write(String.join(",",
                        emp.getEmployeeNumber(),
                        emp.getLastName(),
                        emp.getFirstName(),
                        emp.getBirthday(),
                        emp.getAddress(),
                        emp.getPhoneNumber(),
                        emp.getSss(),
                        emp.getPhilHealth(),
                        emp.getTin(),
                        emp.getPagIbig(),
                        emp.getStatus(),
                        emp.getPosition(),
                        emp.getImmediateSupervisor(),
                        emp.getBasicSalary(),
                        emp.getRiceSubsidy(),
                        emp.getPhoneAllowance(),
                        emp.getClothingAllowance(),
                        emp.getGrossSemiMonthlyRate(),
                        emp.getHourlyRate()
                ));
                writer.newLine();
            }

        } catch (IOException e) {
            showErrorAlert("Save Error", "Failed to save employee data.", e.getMessage());
        }
    }

    // ── Public API used by child windows ─────────────────────────────────────

    /** Returns a snapshot of all current employee numbers for duplicate checking. */
    public java.util.Set<String> getExistingEmpNumbers() {
        java.util.Set<String> ids = new java.util.HashSet<>();
        for (AllEmployee e : employeeList) ids.add(e.getEmployeeNumber());
        return ids;
    }

    public void addEmployee(AllEmployee employee) {
        employeeList.add(employee);
        saveEmployeesToCSV();
        refreshTable();
    }

    public void updateEmployee(AllEmployee updated) {
        for (int i = 0; i < employeeList.size(); i++) {
            if (employeeList.get(i).getEmployeeNumber()
                    .equals(updated.getEmployeeNumber())) {
                employeeList.set(i, updated);
                break;
            }
        }
        saveEmployeesToCSV();
        refreshTable();
    }

    public void refreshTable() {
        loadEmployeesFromCSV();
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
            showWarningAlert("No Selection",
                    "Please select an employee in the table to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete: " + selected.getFirstName()
                + " " + selected.getLastName() + "?");
        confirm.setContentText(
                "This action cannot be undone. Are you sure?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            employeeList.remove(selected);
            saveEmployeesToCSV();
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
