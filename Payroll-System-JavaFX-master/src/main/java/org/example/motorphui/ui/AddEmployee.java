package org.example.motorphui.ui;

import org.example.motorphui.model.AllEmployee;
import org.example.motorphui.model.AllEmployeePublic;
import org.example.motorphui.util.EmployeeFieldFilter;
import org.example.motorphui.util.EmployeeValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class AddEmployee {

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

    @FXML private Button addEmpButton;
    @FXML private Button cancelButton;

    private HREmployeeView parentController;

    // ── Init ──────────────────────────────────────────────────────────────────

    @FXML
    private void initialize() {
        // ── Input filters (applied before any keystroke reaches the field) ────
        //
        //  Digits only — letters and symbols are silently blocked
        EmployeeFieldFilter.digitsOnly(employeeNumberField);
        EmployeeFieldFilter.digitsOnly(philHealthField);
        EmployeeFieldFilter.digitsOnly(pagIbigField);
        //  Digits + hyphens — letters blocked, only '-' allowed as separator
        EmployeeFieldFilter.digitsAndHyphens(phoneNumberField);
        EmployeeFieldFilter.digitsAndHyphens(sssField);
        EmployeeFieldFilter.digitsAndHyphens(tinField);
        //  Digits + slash — for birthday date separators
        EmployeeFieldFilter.digitsAndSlash(birthdayField);
        //  Digits + decimal point — for monetary and rate fields
        EmployeeFieldFilter.digitsAndDecimal(basicSalaryField);
        EmployeeFieldFilter.digitsAndDecimal(riceSubsidyField);
        EmployeeFieldFilter.digitsAndDecimal(phoneAllowanceField);
        EmployeeFieldFilter.digitsAndDecimal(clothingAllowanceField);
        EmployeeFieldFilter.digitsAndDecimal(hourlyRateField);
        //  Letters only (+ common name punctuation: spaces, hyphens, periods)
        EmployeeFieldFilter.lettersOnly(lastNameField);
        EmployeeFieldFilter.lettersOnly(firstNameField);
        //  Letters + spaces — digits blocked
        EmployeeFieldFilter.lettersAndSpaces(statusField);
        EmployeeFieldFilter.lettersAndSpaces(positionField);
        //  Free text — address and supervisor allow mixed alphanumeric input
        EmployeeFieldFilter.freeText(addressField);
        EmployeeFieldFilter.freeText(immediateSupervisorField);

        // ── Disable Add button until every field has content ──────────────────
        addEmpButton.setDisable(true);
        for (TextField f : allFields()) {
            f.textProperty().addListener((obs, ov, nv) ->
                    addEmpButton.setDisable(!allFilled()));
        }

        // ── Prompt text ───────────────────────────────────────────────────────
        employeeNumberField   .setPromptText("e.g. 10052");
        lastNameField         .setPromptText("e.g. Santos");
        firstNameField        .setPromptText("e.g. Maria");
        birthdayField         .setPromptText("MM/DD/YYYY");
        addressField          .setPromptText("Full address");
        phoneNumberField      .setPromptText("NNN-NNN-NNN");
        sssField              .setPromptText("NN-NNNNNNN-N");
        philHealthField       .setPromptText("12 digits");
        tinField              .setPromptText("NNN-NNN-NNN-NNN");
        pagIbigField          .setPromptText("12 digits");
        statusField           .setPromptText("Regular or Probationary");
        positionField         .setPromptText("e.g. Software Engineer");
        immediateSupervisorField.setPromptText("Full name or N/A");
        basicSalaryField      .setPromptText("e.g. 30000");
        riceSubsidyField      .setPromptText("e.g. 1500");
        phoneAllowanceField   .setPromptText("e.g. 2000");
        clothingAllowanceField.setPromptText("e.g. 1000");
        hourlyRateField       .setPromptText("e.g. 178.57");
    }

    public void setParentController(HREmployeeView parent) {
        this.parentController = parent;
    }

    // ── Add button ────────────────────────────────────────────────────────────

    @FXML
    private void onAddButtonClick(ActionEvent event) {
        java.util.Set<String> existingIds =
                parentController != null ? parentController.getExistingEmpNumbers()
                                         : java.util.Collections.emptySet();

        List<String> errors = EmployeeValidator.validateAll(
                employeeNumberField.getText(),
                lastNameField.getText(),
                firstNameField.getText(),
                birthdayField.getText(),
                addressField.getText(),
                phoneNumberField.getText(),
                sssField.getText(),
                philHealthField.getText(),
                tinField.getText(),
                pagIbigField.getText(),
                statusField.getText(),
                positionField.getText(),
                immediateSupervisorField.getText(),
                basicSalaryField.getText(),
                riceSubsidyField.getText(),
                phoneAllowanceField.getText(),
                clothingAllowanceField.getText(),
                hourlyRateField.getText(),
                true,
                existingIds
        );

        if (!errors.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Errors",
                    String.join("\n", errors));
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Add Employee");
        confirm.setHeaderText("Add New Employee");
        confirm.setContentText("Are you sure you want to add this employee?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        double basic     = Double.parseDouble(basicSalaryField.getText().trim());
        String grossSemi = String.valueOf(basic / 2.0);

        AllEmployee emp = new AllEmployeePublic(
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
                grossSemi,
                hourlyRateField.getText().trim()
        );

        if (parentController != null) {
            parentController.addEmployee(emp);
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Employee added successfully.");
            clearFields();
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        }
    }

    // ── Cancel ────────────────────────────────────────────────────────────────

    @FXML
    private void handleCancel(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private TextField[] allFields() {
        return new TextField[]{
                employeeNumberField, lastNameField, firstNameField, birthdayField,
                addressField, phoneNumberField, sssField, philHealthField, tinField,
                pagIbigField, statusField, positionField, immediateSupervisorField,
                basicSalaryField, riceSubsidyField, phoneAllowanceField,
                clothingAllowanceField, hourlyRateField
        };
    }

    private boolean allFilled() {
        for (TextField f : allFields()) {
            if (f.getText().trim().isEmpty()) return false;
        }
        return true;
    }

    private void clearFields() {
        for (TextField f : allFields()) f.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.getDialogPane().setMinWidth(460);
        a.showAndWait();
    }
}
