package org.example.motorphui.ui;

import org.example.motorphui.model.Employee;
import org.example.motorphui.service.SalaryTaxCalculator;
import org.example.motorphui.dao.AttendanceService;
import org.example.motorphui.service.AttendanceServiceCSV;
import org.example.motorphui.dao.ImpSalaryTaxCalculator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HRPayslip {

    // --- Services (Abstraction) ---
    private final ImpSalaryTaxCalculator salaryCalculator;
    private final AttendanceService attendanceService;

    // --- FXML UI Elements ---
    @FXML private ChoiceBox<String> MonthCHBox;
    @FXML private Label hoursWorkedLabel;
    @FXML private Label empNumLabel, NameLabel, sssLabel, philHealthLabel, tinLabel, pagIbigLabel;
    @FXML private Label BdayLabel, AddressLabel, PhoneLabel, positionLabel, RiceLabel;
    @FXML private Label PhoneAllowLabel, ClothingLabel, RateLabel;
    @FXML private Label GrossLabel, SConLabel, PHConLabel, PConLabel, WTLabel, DeductLabel;
    @FXML private Label NetLabel;

    private Employee employee;

    
    public HRPayslip() {
        this.salaryCalculator = new SalaryTaxCalculator();
        this.attendanceService = new AttendanceServiceCSV();
    }

    @FXML
    public void initialize() {
        MonthCHBox.setItems(FXCollections.observableArrayList(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        ));

        MonthCHBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (employee != null && newVal != null) {
                // Using the Abstraction
                double hours = attendanceService.getMonthlyHours(employee.getEmployeeNumber(), newVal, "2024");
                updateMonthlyCalculations(hours);
            }
        });
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;

        double basicSalary = parseDoubleSafe(employee.getBasicSalary());
        
        // Using the Abstraction for Math
        double sss = salaryCalculator.calculateSSSContribution(basicSalary);
        double ph = salaryCalculator.calculatePhilHealthContribution(basicSalary);
        double pi = salaryCalculator.calculatePagibigContribution(basicSalary);
        double tax = salaryCalculator.calculateWithholdingTax(basicSalary, sss, ph, pi);

        // Update UI Text
        updateStaticLabels(employee);
        updateDeductionLabels(sss, ph, pi, tax);
        
        double totalDeductions = sss + ph + pi + tax;
        DeductLabel.setText(String.format("Total Deductions: %.2f", totalDeductions));
    }

    private void updateMonthlyCalculations(double hours) {
        hoursWorkedLabel.setText("Hours Worked: " + String.format("%.2f", hours));

        if (employee != null) {
            double hourlyRate = parseDoubleSafe(employee.getHourlyRate());
            double grossPay = hours * hourlyRate;
            GrossLabel.setText(String.format("Gross Pay: %.2f", grossPay));

            double basic = parseDoubleSafe(employee.getBasicSalary());
            
            // Re-calculate using Abstraction
            double sss = salaryCalculator.calculateSSSContribution(basic);
            double ph = salaryCalculator.calculatePhilHealthContribution(basic);
            double pi = salaryCalculator.calculatePagibigContribution(basic);
            double tax = salaryCalculator.calculateWithholdingTax(basic, sss, ph, pi);
            
            double totalDeductions = sss + ph + pi + tax;
            double netSalary = grossPay - totalDeductions;
            
            NetLabel.setText(String.format("Net Salary: %.2f", netSalary));
        }
    }

    // --- Helper Methods to keep code clean ---

    private void updateStaticLabels(Employee e) {
        empNumLabel.setText("Emp #: " + e.getEmployeeNumber());
        NameLabel.setText("Name: " + e.getFirstName() + " " + e.getLastName());
        sssLabel.setText("SSS: " + e.getSss());
        philHealthLabel.setText("PhilHealth: " + e.getPhilHealth());
        tinLabel.setText("TIN: " + e.getTin());
        pagIbigLabel.setText("Pag-Ibig: " + e.getPagIbig());
        BdayLabel.setText("Birthday: " + e.getBirthday());
        AddressLabel.setText("Address: " + e.getAddress());
        PhoneLabel.setText("Phone Number: " + e.getPhoneNumber());
        positionLabel.setText("Position: " + e.getPosition());
        RiceLabel.setText("Rice Subsidy: " + e.getRiceSubsidy());
        PhoneAllowLabel.setText("Phone Allowance: " + e.getPhoneAllowance());
        ClothingLabel.setText("Clothing Allowance: " + e.getClothingAllowance());
        RateLabel.setText("Hourly Rate: " + e.getHourlyRate());
    }

    private void updateDeductionLabels(double sss, double ph, double pi, double tax) {
        SConLabel.setText(String.format("SSS Contribution: %.2f", sss));
        PHConLabel.setText(String.format("PhilHealth Contribution: %.2f", ph));
        PConLabel.setText(String.format("Pag-Ibig Contribution: %.2f", pi));
        WTLabel.setText(String.format("Withholding Tax: %.2f", tax));
    }

    private double parseDoubleSafe(String s) {
        try {
            return Double.parseDouble(s.replace(",", "").trim());
        } catch (Exception e) {
            return 0.0;
        }
    }
}