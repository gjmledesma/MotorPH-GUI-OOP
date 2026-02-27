package org.example.motorphui.ui;

import org.example.motorphui.service.NewEmployee;
import org.example.motorphui.model.Employee;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Optional;

public class HRViewAndUpdateEmployee {

    @FXML private TextField employeeNumberField;
    @FXML private TextField lastNameField;
    @FXML private TextField firstNameField;
    @FXML private TextField birthdayField;
    @FXML private TextField addressField;
    @FXML private TextField phoneNumberField;
    @FXML private TextField sssField;
    @FXML private TextField philHealthField;
    @FXML private TextField tinField;
    @FXML private TextField pagIbigField;
    @FXML private TextField statusField;
    @FXML private TextField positionField;
    @FXML private TextField immediateSupervisorField;
    @FXML private TextField basicSalaryField;
    @FXML private TextField riceSubsidyField;
    @FXML private TextField phoneAllowanceField;
    @FXML private TextField clothingAllowanceField;
    @FXML private TextField hourlyRateField;

    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Employee employee;
    private HREmployeeView parentController;

    public void setParentController(HREmployeeView controller) {
        this.parentController = controller;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;

        employeeNumberField.setText(employee.getEmployeeNumber());
        lastNameField.setText(employee.getLastName());
        firstNameField.setText(employee.getFirstName());
        birthdayField.setText(employee.getBirthday());
        addressField.setText(employee.getAddress());
        phoneNumberField.setText(employee.getPhoneNumber());
        sssField.setText(employee.getSss());
        philHealthField.setText(employee.getPhilHealth());
        tinField.setText(employee.getTin());
        pagIbigField.setText(employee.getPagIbig());
        statusField.setText(employee.getStatus());
        positionField.setText(employee.getPosition());
        immediateSupervisorField.setText(employee.getImmediateSupervisor());
        basicSalaryField.setText(employee.getBasicSalary());
        riceSubsidyField.setText(employee.getRiceSubsidy());
        phoneAllowanceField.setText(employee.getPhoneAllowance());
        clothingAllowanceField.setText(employee.getClothingAllowance());
        hourlyRateField.setText(employee.getHourlyRate());

        employeeNumberField.setEditable(false);
    }


    @FXML
    private void handleSaveButton(ActionEvent event) {
    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
    confirmAlert.setTitle("Confirm Update");
    confirmAlert.setHeaderText("Update Employee Information");
    confirmAlert.setContentText("Are you sure you want to update this employee's information?");

    Optional<ButtonType> result = confirmAlert.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {

        Employee updatedEmployee = new NewEmployee(
            employee.getEmployeeNumber(),
            lastNameField.getText().trim(),
            firstNameField.getText().trim(),
            birthdayField.getText().trim(),
            addressField.getText().trim(),
            phoneNumberField.getText().trim(),
            sssField.getText().trim(),
            philHealthField.getText().trim(),
            tinField.getText().trim(),
            pagIbigField.getText().trim(),
            statusField.getText().trim(),
            positionField.getText().trim(),
            immediateSupervisorField.getText().trim(),
            basicSalaryField.getText().trim(),
            riceSubsidyField.getText().trim(),
            phoneAllowanceField.getText().trim(),
            clothingAllowanceField.getText().trim(),
            employee.getGrossSemiMonthlyRate(),
            hourlyRateField.getText().trim()
        );

        // Update in parent controller
        if (parentController != null) {
            parentController.updateEmployee(updatedEmployee);
            
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Success");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Employee information updated successfully.");
            successAlert.showAndWait();
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        }
    }
}

    @FXML
    private void handleCancelButton() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}

// When the "View and Update" button is clicked, this opens a pop-up window (hr_view_and_update_employee.fxml) which I've already added the UI for
// to display the employee's current information for viewing and updating.

// The pop-up window allows the HR user to view and edit the selected employee's details.
// Once the user makes any changes, they can click the "Save" button to save the updated information.

// A confirmation dialog should appear when the "Save" button is clicked, asking the user:
// "Are you sure you want to save these changes?" This ensures the user is aware of the action being performed.

// If the user confirms (clicks "Yes"), the updated information should be saved
// and the employee's record should be updated accordingly (in the CSV file).

// If the user clicks "No" on the confirmation prompt, the changes will not be saved,
// and the pop-up window will remain open for further editing or cancellation.

// After saving, the pop-up window should either close automatically or provide the option
// to cancel the action and close without saving any changes.

// In case of errors (e.g., file issues or invalid input), an error alert should be displayed
// to inform the user about the issue.