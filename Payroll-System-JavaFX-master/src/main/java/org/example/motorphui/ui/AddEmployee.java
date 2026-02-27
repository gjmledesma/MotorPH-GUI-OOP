package org.example.motorphui.ui;

import org.example.motorphui.service.NewEmployee;
import org.example.motorphui.model.Employee;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.*;
import java.util.Optional;

public class AddEmployee {
    @FXML
    private TextField employeeNumberField, lastNameField, firstNameField, birthdayField, addressField,
            phoneNumberField, sssField, philHealthField, tinField, pagIbigField, positionField,
            basicSalaryField, riceSubsidyField, phoneAllowanceField, clothingAllowanceField, hourlyRateField,
            statusField, immediateSupervisorField;

    @FXML
    private Button addEmpButton;
    @FXML
    private Button cancelButton;

    private final String employeeDataFile = "/src/main/resources/org/example/motorphui/data/motorph_employee_data.csv";
    private HREmployeeView parentController;

    public void SetParentController(HREmployeeView parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void initialize() {
        addEmpButton.setDisable(true);
        TextField[] fields = {employeeNumberField, lastNameField, firstNameField, birthdayField, addressField,
                phoneNumberField, sssField, philHealthField, tinField, pagIbigField, positionField,
                basicSalaryField, riceSubsidyField, phoneAllowanceField, clothingAllowanceField, hourlyRateField,
                statusField, immediateSupervisorField};

        for (TextField field : fields) {
            field.textProperty().addListener((obs, oldVal, newVal) -> {
                addEmpButton.setDisable(!allFieldsFilled());
            });
        }
    }

    @FXML
    private void onAddButtonClick(ActionEvent event) {
        if (!allFieldsFilled()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields before submitting.");
            return;
        }

        // Show confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Add Employee");
        confirmAlert.setHeaderText("Add New Employee");
        confirmAlert.setContentText("Are you sure you want to add this employee?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            Employee newEmployee = new NewEmployee(
                    employeeNumberField.getText().trim(),
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
                    "0",                                // grossSemiMonthlyRate - you might want to add this field
                    hourlyRateField.getText().trim()
);

            // Add to parent's list and save
            if (parentController != null) {
                parentController.addEmployee(newEmployee);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Employee added successfully.");
                clearFields();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
            }
        }
    }

    private boolean allFieldsFilled() {
        TextField[] fields = {employeeNumberField, lastNameField, firstNameField, birthdayField, addressField,
                phoneNumberField, sssField, philHealthField, tinField, pagIbigField, positionField,
                basicSalaryField, riceSubsidyField, phoneAllowanceField, clothingAllowanceField, hourlyRateField,
                statusField, immediateSupervisorField};

        for (TextField f : fields) {
            if (f.getText().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void clearFields() {
        TextField[] fields = {employeeNumberField, lastNameField, firstNameField, birthdayField, addressField,
                phoneNumberField, sssField, philHealthField, tinField, pagIbigField, positionField,
                basicSalaryField, riceSubsidyField, phoneAllowanceField, clothingAllowanceField, hourlyRateField,
                statusField, immediateSupervisorField};
        
        for (TextField field : fields) {
            field.clear();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}