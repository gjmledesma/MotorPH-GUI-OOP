package org.example.motorphui.ui;

import org.example.motorphui.model.Employee;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class EmployeeProfile {

    @FXML
    private Label nameLabel;
    @FXML private Label positionLabel;
    @FXML private Label statusLabel;
    @FXML private Label supervisorNameLabel;
    @FXML private Label empNumberLabel;
    @FXML private Label bdayLabel;
    @FXML private Label addressLabel;
    @FXML private Label phoneNumLabel;
    @FXML private Label basicSalaryLabel;
    @FXML private Label hourlyRateLabel;

    @FXML private Label leaveTypeLabel;
    @FXML private Label durationLabel;
    @FXML private Label statusLeaveLabel;

    @FXML private Label sssLabel;
    @FXML private Label riceSubLabel;
    @FXML private Label philhealthLabel;
    @FXML private Label phoneAllowanceLabel;
    @FXML private Label tinLabel;
    @FXML private Label clothingAllowanceLabel;
    @FXML private Label pagibigLabel;

    public void setEmployeeData(Employee employee) {
        nameLabel.setText(employee.getFirstName() + " " + employee.getLastName());
        positionLabel.setText(employee.getPosition());
        statusLabel.setText(employee.getStatus());
        supervisorNameLabel.setText(employee.getImmediateSupervisor());
        empNumberLabel.setText(employee.getEmployeeNumber());
        bdayLabel.setText(employee.getBirthday());
        addressLabel.setText(employee.getAddress());
        phoneNumLabel.setText(employee.getPhoneNumber());
        basicSalaryLabel.setText(employee.getBasicSalary());
        hourlyRateLabel.setText(employee.getHourlyRate());

        leaveTypeLabel.setText("Vacation Leave");
        durationLabel.setText("Nov 15 - Nov 29");
        statusLeaveLabel.setText("Approved");

        sssLabel.setText(employee.getSss());
        riceSubLabel.setText(employee.getRiceSubsidy());
        philhealthLabel.setText(employee.getPhilHealth());
        phoneAllowanceLabel.setText(employee.getPhoneAllowance());
        tinLabel.setText(employee.getTin());
        clothingAllowanceLabel.setText(employee.getClothingAllowance());
        pagibigLabel.setText(employee.getPagIbig());
    }
}
