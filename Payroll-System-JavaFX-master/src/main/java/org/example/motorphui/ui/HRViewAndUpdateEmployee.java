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

    private AllEmployee    employee;
    private HREmployeeView parentController;

    // ── Init ──────────────────────────────────────────────────────────────────

    @FXML
    private void initialize() {
        // ── Input filters ─────────────────────────────────────────────────────
        // Employee number field is locked (non-editable) so no filter needed there.
        //
        //  Digits only — letters and symbols are silently blocked
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

        // ── Prompt text ───────────────────────────────────────────────────────
        birthdayField         .setPromptText("MM/DD/YYYY");
        phoneNumberField      .setPromptText("NNN-NNN-NNN");
        sssField              .setPromptText("NN-NNNNNNN-N");
        philHealthField       .setPromptText("12 digits");
        tinField              .setPromptText("NNN-NNN-NNN-NNN");
        pagIbigField          .setPromptText("12 digits");
        statusField           .setPromptText("Regular or Probationary");
        basicSalaryField      .setPromptText("e.g. 30000");
        riceSubsidyField      .setPromptText("e.g. 1500");
        phoneAllowanceField   .setPromptText("e.g. 2000");
        clothingAllowanceField.setPromptText("e.g. 1000");
        hourlyRateField       .setPromptText("e.g. 178.57");
    }

    // ── Public API ────────────────────────────────────────────────────────────

    public void setParentController(HREmployeeView controller) {
        this.parentController = controller;
    }

    public void setEmployee(AllEmployee employee) {
        this.employee = employee;

        employeeNumberField    .setText(employee.getEmployeeNumber());
        lastNameField          .setText(employee.getLastName());
        firstNameField         .setText(employee.getFirstName());
        birthdayField          .setText(employee.getBirthday());
        addressField           .setText(employee.getAddress());
        phoneNumberField       .setText(employee.getPhoneNumber());
        sssField               .setText(employee.getSss());
        philHealthField        .setText(employee.getPhilHealth());
        tinField               .setText(employee.getTin());
        pagIbigField           .setText(employee.getPagIbig());
        statusField            .setText(employee.getStatus());
        positionField          .setText(employee.getPosition());
        immediateSupervisorField.setText(employee.getImmediateSupervisor());
        basicSalaryField       .setText(employee.getBasicSalary());
        riceSubsidyField       .setText(employee.getRiceSubsidy());
        phoneAllowanceField    .setText(employee.getPhoneAllowance());
        clothingAllowanceField .setText(employee.getClothingAllowance());
        hourlyRateField        .setText(employee.getHourlyRate());

        // Employee number is the primary key — lock it after populating
        employeeNumberField.setEditable(false);
        employeeNumberField.setStyle("-fx-background-color: #F5F5F5; -fx-text-fill: #888;");
    }

    // ── Save ──────────────────────────────────────────────────────────────────

    @FXML
    private void handleSaveButton(ActionEvent event) {
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
                false,
                java.util.Collections.emptySet()
        );

        if (!errors.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Errors",
                    String.join("\n", errors));
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Update");
        confirm.setHeaderText("Update Employee Information");
        confirm.setContentText("Are you sure you want to save these changes?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        double basic     = Double.parseDouble(basicSalaryField.getText().trim());
        String grossSemi = String.valueOf(basic / 2.0);

        AllEmployee updated = new AllEmployeePublic(
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
                grossSemi,
                hourlyRateField.getText().trim()
        );

        if (parentController != null) {
            parentController.updateEmployee(updated);
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Employee information updated successfully.");
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        }
    }

    // ── Cancel ────────────────────────────────────────────────────────────────

    @FXML
    private void handleCancelButton() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.getDialogPane().setMinWidth(460);
        a.showAndWait();
    }
}
