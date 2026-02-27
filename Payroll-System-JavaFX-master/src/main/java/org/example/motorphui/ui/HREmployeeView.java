package org.example.motorphui.ui;

import org.example.motorphui.service.NewEmployee;
import org.example.motorphui.model.Employee;
import org.example.motorphui.service.Authentication;
import org.example.motorphui.service.SalaryTaxCalculator;
import org.example.motorphui.service.AttendanceServiceCSV;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

public class HREmployeeView {

    @FXML
    private AnchorPane root;
    @FXML
    private TableView<Employee> emp_table;
    @FXML
    private Label emp_info_label;
    @FXML
    private Button viewandupdate_button;
    @FXML
    private Button addemp_button;
    @FXML
    private Button deleteemp_button;

    // Declare columns for each property in Employee class
    @FXML
    private TableColumn<Employee, String> empNumColumn;
    @FXML
    private TableColumn<Employee, String> lastNameColumn;
    @FXML
    private TableColumn<Employee, String> firstNameColumn;
    @FXML
    private TableColumn<Employee, String> birthdayColumn;
    @FXML
    private TableColumn<Employee, String> addressColumn;
    @FXML
    private TableColumn<Employee, String> phoneNumberColumn;
    @FXML
    private TableColumn<Employee, String> sssColumn;
    @FXML
    private TableColumn<Employee, String> philHealthColumn;
    @FXML
    private TableColumn<Employee, String> tinColumn;
    @FXML
    private TableColumn<Employee, String> pagIbigColumn;
    @FXML
    private TableColumn<Employee, String> statusColumn;
    @FXML
    private TableColumn<Employee, String> positionColumn;
    @FXML
    private TableColumn<Employee, String> supervisorColumn;
    @FXML
    private TableColumn<Employee, String> basicSalaryColumn;
    @FXML
    private TableColumn<Employee, String> riceSubsidyColumn;
    @FXML
    private TableColumn<Employee, String> phoneAllowanceColumn;
    @FXML
    private TableColumn<Employee, String> clothingAllowanceColumn;
    @FXML
    private TableColumn<Employee, String> grossSemiMonthlyColumn;
    @FXML
    private TableColumn<Employee, String> hourlyRateColumn;

    private final ObservableList<Employee> employeeList = FXCollections.observableArrayList();

    private static final String EMPLOYEE_DATA_FILE = "/org/example/motorphui/data/motorph_employee_data.csv";

    @FXML
    public void initialize() {
        root.setMinWidth(1440);
        root.setMinHeight(1024);
        emp_table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Set column cell value factories for each property in the Employee class
        empNumColumn.setCellValueFactory(new PropertyValueFactory<>("employeeNumber"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        birthdayColumn.setCellValueFactory(new PropertyValueFactory<>("birthday"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        sssColumn.setCellValueFactory(new PropertyValueFactory<>("sss"));
        philHealthColumn.setCellValueFactory(new PropertyValueFactory<>("philHealth"));
        tinColumn.setCellValueFactory(new PropertyValueFactory<>("tin"));
        pagIbigColumn.setCellValueFactory(new PropertyValueFactory<>("pagIbig"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        supervisorColumn.setCellValueFactory(new PropertyValueFactory<>("immediateSupervisor"));
        basicSalaryColumn.setCellValueFactory(new PropertyValueFactory<>("basicSalary"));
        riceSubsidyColumn.setCellValueFactory(new PropertyValueFactory<>("riceSubsidy"));
        phoneAllowanceColumn.setCellValueFactory(new PropertyValueFactory<>("phoneAllowance"));
        clothingAllowanceColumn.setCellValueFactory(new PropertyValueFactory<>("clothingAllowance"));
        grossSemiMonthlyColumn.setCellValueFactory(new PropertyValueFactory<>("grossSemiMonthlyRate"));
        hourlyRateColumn.setCellValueFactory(new PropertyValueFactory<>("hourlyRate"));


        // Load employees from CSV
        loadEmployeesFromCSV();
    }

    private void loadEmployeesFromCSV() {
        employeeList.clear();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(EMPLOYEE_DATA_FILE)))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length == 19) {
                    Employee emp = new NewEmployee(
                            data[0], // employeeNumber
                            data[1], // lastName
                            data[2], // firstName
                            data[3], // birthday
                            data[4], // address
                            data[5], // phoneNumber
                            data[6], // sss
                            data[7], // philHealth
                            data[8], // tin
                            data[9], // pagIbig
                            data[10], // status
                            data[11], // position
                            data[12], // immediateSupervisor
                            data[13], // basicSalary
                            data[14], // riceSubsidy
                            data[15], // phoneAllowance
                            data[16], // clothingAllowance
                            data[17], // grossSemiMonthlyRate
                            data[18]  // hourlyRate
                    );
                    employeeList.add(emp);
                }
            }
            emp_table.setItems(employeeList);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Load Error");
            alert.setHeaderText("Failed to load employee data.");
            alert.setContentText("An error occurred while loading the employee data.");
            alert.showAndWait();
        }
    }

    public void updateEmployee(Employee updatedEmployee) {
        for (int i = 0; i < employeeList.size(); i++) {
            if (employeeList.get(i).getEmployeeNumber().equals(updatedEmployee.getEmployeeNumber())) {
                employeeList.set(i, updatedEmployee);
                break;
            }
        }
        saveEmployeesToCSV(EMPLOYEE_DATA_FILE);
        refreshTable();
    }

    @FXML
    public void handleViewAndUpdateButton() {
        Employee selectedEmployee = emp_table.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/motorphui/hr_view_and_update_employee.fxml"));
                Parent parent = loader.load();

                HRViewAndUpdateEmployee controller = loader.getController();
                controller.setEmployee(selectedEmployee);
                controller.setParentController(this);

                Stage stage = new Stage();
                stage.setTitle("View and Update Employee");
                stage.setScene(new Scene(parent));
                stage.showAndWait(); // Wait for the pop-up to close

                // After the pop-up closes, refresh the table data
                refreshTable();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Could not open the update window.");
                alert.setContentText("An error occurred while trying to load the employee update form.");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void handleDeleteEmployeeButton() {
        Employee selectedEmployee = emp_table.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete Employee: " + selectedEmployee.getFirstName() + " " + selectedEmployee.getLastName() + "?");
            alert.setContentText("Are you sure you want to delete this employee record? This action cannot be undone.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                employeeList.remove(selectedEmployee);
                saveEmployeesToCSV("/org/example/motorphui/data/motorph_employee_data.csv");
                refreshTable();
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Deletion Successful");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Employee record deleted successfully.");
                successAlert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Employee Selected");
            alert.setContentText("Please select an employee in the table to delete.");
            alert.showAndWait();
        }
    }

    public void refreshTable() {
        loadEmployeesFromCSV();
    }

    private void saveEmployeesToCSV(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Employee #,Last Name,First Name,Birthday,Address,Phone Number,SSS #,PhilHealth #,TIN #,Pag-Ibig #,Status,Position,Immediate Supervisor,Basic Salary,Rice Subsidy,Phone Allowance,Clothing Allowance,Gross Semi-monthly Rate,Hourly Rate\n");

            for (Employee emp : employeeList) {
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
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save Error");
            alert.setHeaderText("Failed to save employee data.");
            alert.setContentText("An error occurred while writing to the CSV file.");
            alert.showAndWait();
        }
    }

    public void addEmployee(Employee employee) {
        employeeList.add(employee);
        saveEmployeesToCSV(EMPLOYEE_DATA_FILE);
        refreshTable();
    }

    @FXML
    public void openAddEmployeeWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/motorphui/add_employee.fxml"));
            Parent root = loader.load();

            AddEmployee addController = loader.getController();
            addController.SetParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Add New Employee");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            showErrorAlert("Load Error", "Failed to open Add Employee form.", e.getMessage());
        }
    }

    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}