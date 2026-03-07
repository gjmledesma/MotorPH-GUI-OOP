package org.example.motorphui.ui;

import org.example.motorphui.dao.AttendanceDAO;
import org.example.motorphui.model.AllEmployee;
import org.example.motorphui.model.SalaryTaxCalculator;
import org.example.motorphui.service.AttendanceService;
import org.example.motorphui.service.SalaryTaxCalculatorService;
import org.example.motorphui.session.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Controller for the Employee View Salary screen.
 *
 * Shows a monthly salary summary for the logged-in employee.
 * A "View Full Payslip" button opens the EmployeePayslip modal.
 */
public class EmployeeViewSalary {

    // ── Services ─────────────────────────────────────────────────────────────

    private final SalaryTaxCalculatorService calc      = new SalaryTaxCalculator();
    private final AttendanceService           attendance = new AttendanceDAO();

    private static final NumberFormat PESO =
            NumberFormat.getCurrencyInstance(new Locale("fil", "PH"));

    // ── FXML ─────────────────────────────────────────────────────────────────

    @FXML private AnchorPane root;
    @FXML private Label viewsalary_label;

    // Employee header strip
    @FXML private Label emp_name_label;
    @FXML private Label emp_id_label;
    @FXML private Label emp_position_label;

    // Month selector
    @FXML private ComboBox<String> month_combo;

    // Summary card — Earnings
    @FXML private Label hours_value;
    @FXML private Label rate_value;
    @FXML private Label basic_pay_value;
    @FXML private Label rice_value;
    @FXML private Label phone_allow_value;
    @FXML private Label clothing_value;
    @FXML private Label gross_value;

    // Summary card — Deductions
    @FXML private Label sss_value;
    @FXML private Label philhealth_value;
    @FXML private Label pagibig_value;
    @FXML private Label tax_value;
    @FXML private Label total_deduct_value;

    // Net Pay
    @FXML private Label net_value;

    // Actions
    @FXML private Button view_payslip_btn;

    // ── State ─────────────────────────────────────────────────────────────────

    private AllEmployee currentEmployee;

    // Cached values so the payslip modal can receive them without recomputing
    private double cachedHours;
    private double cachedGross;
    private double cachedSss;
    private double cachedPh;
    private double cachedPi;
    private double cachedTax;
    private double cachedNet;
    private String selectedMonth;

    // ── Init ──────────────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        currentEmployee = SessionManager.getInstance().getCurrentEmployee();

        if (currentEmployee == null) {
            showAlert("Session Error", "No employee is logged in. Please log in again.");
            view_payslip_btn.setDisable(true);
            return;
        }

        populateHeader();
        setupMonthCombo();
        clearSummary();
        view_payslip_btn.setDisable(true);
    }

    // ── Setup helpers ─────────────────────────────────────────────────────────

    private void populateHeader() {
        emp_name_label    .setText(currentEmployee.getFirstName() + " " + currentEmployee.getLastName());
        emp_id_label      .setText("Employee #" + currentEmployee.getEmployeeNumber());
        emp_position_label.setText(currentEmployee.getPosition());
    }

    private void setupMonthCombo() {
        // Only the months that actually have attendance data
        month_combo.setItems(FXCollections.observableArrayList(
                "June", "July", "August", "September",
                "October", "November", "December"));
        month_combo.setPromptText("Select month…");

        month_combo.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, month) -> {
                    if (month != null) {
                        selectedMonth = month;
                        recalculate(month);
                        view_payslip_btn.setDisable(false);
                    }
                });
    }

    // ── Calculation ───────────────────────────────────────────────────────────

    private void recalculate(String month) {
        double hours      = attendance.getMonthlyHours(currentEmployee.getEmployeeNumber(), month, "2024");
        double hourlyRate = parse(currentEmployee.getHourlyRate());
        double basicPay   = hours * hourlyRate;
        double rice       = parse(currentEmployee.getRiceSubsidy());
        double phone      = parse(currentEmployee.getPhoneAllowance());
        double clothing   = parse(currentEmployee.getClothingAllowance());
        double gross      = basicPay + rice + phone + clothing;

        double basic      = parse(currentEmployee.getBasicSalary());
        double sss        = calc.calculateSSSContribution(basic);
        double ph         = calc.calculatePhilHealthContribution(basic);
        double pi         = calc.calculatePagibigContribution(basic);
        double tax        = calc.calculateWithholdingTax(basic, sss, ph, pi);
        double totalDeduct = sss + ph + pi + tax;
        double net        = gross - totalDeduct;

        // Cache for payslip modal
        cachedHours = hours;
        cachedGross = gross;
        cachedSss   = sss;
        cachedPh    = ph;
        cachedPi    = pi;
        cachedTax   = tax;
        cachedNet   = net;

        // ── Update earnings labels ─────────────────────────────────────────
        hours_value      .setText(String.format("%.2f hrs", hours));
        rate_value       .setText(PESO.format(hourlyRate));
        basic_pay_value  .setText(PESO.format(basicPay));
        rice_value       .setText(PESO.format(rice));
        phone_allow_value.setText(PESO.format(phone));
        clothing_value   .setText(PESO.format(clothing));
        gross_value      .setText(PESO.format(gross));

        // ── Update deduction labels ────────────────────────────────────────
        sss_value       .setText(PESO.format(sss));
        philhealth_value.setText(PESO.format(ph));
        pagibig_value   .setText(PESO.format(pi));
        tax_value       .setText(PESO.format(tax));
        total_deduct_value.setText(PESO.format(totalDeduct));

        // ── Net pay ───────────────────────────────────────────────────────
        net_value.setText(PESO.format(net));

        // Colour net pay red when negative (unexpected edge case)
        net_value.getStyleClass().removeAll("net-positive", "net-negative");
        net_value.getStyleClass().add(net >= 0 ? "net-positive" : "net-negative");
    }

    private void clearSummary() {
        Label[] all = { hours_value, rate_value, basic_pay_value, rice_value,
                        phone_allow_value, clothing_value, gross_value,
                        sss_value, philhealth_value, pagibig_value,
                        tax_value, total_deduct_value, net_value };
        for (Label l : all) l.setText("—");
    }

    // ── View Payslip button ───────────────────────────────────────────────────

    @FXML
    private void handleViewPayslip() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/motorphui/employee_payslip.fxml"));
            Parent root = loader.load();

            EmployeePayslip controller = loader.getController();
            controller.setPayslipData(
                    currentEmployee,
                    selectedMonth,
                    cachedHours,
                    cachedGross,
                    cachedSss, cachedPh, cachedPi, cachedTax,
                    cachedNet);

            Stage modal = new Stage();
            modal.setTitle("Payslip — " + selectedMonth + " 2024");
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.initOwner(view_payslip_btn.getScene().getWindow());
            modal.setScene(new Scene(root));
            modal.setResizable(false);
            modal.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not open the payslip window.");
        }
    }

    // ── Utility ───────────────────────────────────────────────────────────────

    private double parse(String value) {
        if (value == null) return 0.0;
        try { return Double.parseDouble(value.replace(",", "").trim()); }
        catch (NumberFormatException e) { return 0.0; }
    }

    private void showAlert(String title, String content) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }
}
