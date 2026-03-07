package org.example.motorphui.ui;

import org.example.motorphui.model.AllEmployee;
import org.example.motorphui.model.SalaryTaxCalculator;
import org.example.motorphui.dao.AttendanceDAO;
import org.example.motorphui.service.AttendanceService;
import org.example.motorphui.service.SalaryTaxCalculatorService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Controller for the HR Payslip panel.
 *
 * Gross Pay  = (Hours Worked × Hourly Rate) + Rice Subsidy + Phone Allowance + Clothing Allowance
 * Deductions = SSS + PhilHealth + Pag-Ibig + Withholding Tax
 * Net Pay    = Gross Pay − Total Deductions
 *
 * Deductions are computed against Basic Salary (not gross) per Philippine payroll rules.
 */
public class HRPayslip {

    // ── Services ─────────────────────────────────────────────────────────────

    private final SalaryTaxCalculatorService salaryCalculator = new SalaryTaxCalculator();
    private final AttendanceService           attendanceService = new AttendanceDAO();

    // Peso formatter — e.g. "₱ 45,000.00"
    private static final NumberFormat PESO =
            NumberFormat.getCurrencyInstance(new Locale("fil", "PH"));

    // ── FXML — Employee Info ──────────────────────────────────────────────────

    @FXML private AnchorPane root;
    @FXML private Label periodLabel;        // "Pay Period: June 2024"
    @FXML private ChoiceBox<String> MonthCHBox;

    // Left column
    @FXML private Label empNumLabel;
    @FXML private Label NameLabel;
    @FXML private Label positionLabel;
    @FXML private Label statusLabel;
    @FXML private Label supervisorLabel;
    @FXML private Label BdayLabel;
    @FXML private Label PhoneLabel;

    // Right column
    @FXML private Label AddressLabel;
    @FXML private Label sssLabel;
    @FXML private Label philHealthLabel;
    @FXML private Label tinLabel;
    @FXML private Label pagIbigLabel;

    // ── FXML — Earnings ───────────────────────────────────────────────────────

    @FXML private Label hoursWorkedLabel;
    @FXML private Label RateLabel;
    @FXML private Label basicPayLabel;      // hours × rate
    @FXML private Label RiceLabel;
    @FXML private Label PhoneAllowLabel;
    @FXML private Label ClothingLabel;
    @FXML private Label GrossLabel;

    // ── FXML — Deductions ─────────────────────────────────────────────────────

    @FXML private Label SConLabel;
    @FXML private Label PHConLabel;
    @FXML private Label PConLabel;
    @FXML private Label WTLabel;
    @FXML private Label DeductLabel;

    // ── FXML — Net Pay ────────────────────────────────────────────────────────

    @FXML private Label NetLabel;

    // ── State ─────────────────────────────────────────────────────────────────

    private AllEmployee employee;

    // ── Init ──────────────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        MonthCHBox.setItems(FXCollections.observableArrayList(
                "June", "July", "August", "September", "October", "November", "December"));

        MonthCHBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, month) -> {
                    if (employee != null && month != null) {
                        periodLabel.setText("Pay Period: " + month + " 2024");
                        recalculate(month);
                    }
                });
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Called by the parent controller when HR selects an employee.
     * Populates all static fields; earnings/net are shown only after a month is chosen.
     */
    public void setEmployee(AllEmployee employee) {
        this.employee = employee;
        MonthCHBox.getSelectionModel().clearSelection();
        periodLabel.setText("Pay Period: — Select a month —");
        populateEmployeeInfo(employee);
        populateStaticRates(employee);
        clearDynamicLabels();

        // Pre-calculate deductions from basic salary so they're visible immediately
        double basic = parse(employee.getBasicSalary());
        double sss   = salaryCalculator.calculateSSSContribution(basic);
        double ph    = salaryCalculator.calculatePhilHealthContribution(basic);
        double pi    = salaryCalculator.calculatePagibigContribution(basic);
        double tax   = salaryCalculator.calculateWithholdingTax(basic, sss, ph, pi);

        SConLabel  .setText(PESO.format(sss));
        PHConLabel .setText(PESO.format(ph));
        PConLabel  .setText(PESO.format(pi));
        WTLabel    .setText(PESO.format(tax));
        DeductLabel.setText(PESO.format(sss + ph + pi + tax));
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void populateEmployeeInfo(AllEmployee e) {
        empNumLabel   .setText(e.getEmployeeNumber());
        NameLabel     .setText(e.getFirstName() + " " + e.getLastName());
        positionLabel .setText(e.getPosition());
        statusLabel   .setText(e.getStatus());
        supervisorLabel.setText(e.getImmediateSupervisor());
        BdayLabel     .setText(e.getBirthday());
        PhoneLabel    .setText(e.getPhoneNumber());
        AddressLabel  .setText(e.getAddress());
        sssLabel      .setText(e.getSss());
        philHealthLabel.setText(e.getPhilHealth());
        tinLabel      .setText(e.getTin());
        pagIbigLabel  .setText(e.getPagIbig());
    }

    private void populateStaticRates(AllEmployee e) {
        RateLabel     .setText(PESO.format(parse(e.getHourlyRate())));
        RiceLabel     .setText(PESO.format(parse(e.getRiceSubsidy())));
        PhoneAllowLabel.setText(PESO.format(parse(e.getPhoneAllowance())));
        ClothingLabel .setText(PESO.format(parse(e.getClothingAllowance())));
    }

    private void clearDynamicLabels() {
        hoursWorkedLabel.setText("—");
        basicPayLabel   .setText("—");
        GrossLabel      .setText("—");
        NetLabel        .setText("—");
    }

    /**
     * Fetches attendance hours for the selected month, then recomputes
     * all earnings and net pay.
     */
    private void recalculate(String month) {
        double hours       = attendanceService.getMonthlyHours(
                                employee.getEmployeeNumber(), month, "2024");
        double hourlyRate  = parse(employee.getHourlyRate());
        double basicPay    = hours * hourlyRate;           // wage from time worked
        double rice        = parse(employee.getRiceSubsidy());
        double phone       = parse(employee.getPhoneAllowance());
        double clothing    = parse(employee.getClothingAllowance());
        double grossPay    = basicPay + rice + phone + clothing;

        // Deductions computed against the contractual basic salary (not hours-based)
        double basic = parse(employee.getBasicSalary());
        double sss   = salaryCalculator.calculateSSSContribution(basic);
        double ph    = salaryCalculator.calculatePhilHealthContribution(basic);
        double pi    = salaryCalculator.calculatePagibigContribution(basic);
        double tax   = salaryCalculator.calculateWithholdingTax(basic, sss, ph, pi);
        double totalDeductions = sss + ph + pi + tax;
        double netPay          = grossPay - totalDeductions;

        // ── Update UI ──────────────────────────────────────────────────────
        hoursWorkedLabel.setText(String.format("%.2f hrs", hours));
        basicPayLabel   .setText(PESO.format(basicPay));
        GrossLabel      .setText(PESO.format(grossPay));

        SConLabel  .setText(PESO.format(sss));
        PHConLabel .setText(PESO.format(ph));
        PConLabel  .setText(PESO.format(pi));
        WTLabel    .setText(PESO.format(tax));
        DeductLabel.setText(PESO.format(totalDeductions));

        NetLabel.setText(PESO.format(netPay));
    }

    /** Strips commas, trims whitespace, and parses to double. Returns 0 on failure. */
    private double parse(String value) {
        if (value == null) return 0.0;
        try {
            return Double.parseDouble(value.replace(",", "").trim());
        } catch (NumberFormatException e) {
            System.err.println("[HRPayslip] Cannot parse numeric value: '" + value + "'");
            return 0.0;
        }
    }
}
