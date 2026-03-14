package org.example.motorphui.ui;

import java.text.NumberFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.example.motorphui.dao.AttendanceDAO;
import org.example.motorphui.model.AllEmployee;
import org.example.motorphui.model.SalaryTaxCalculator;
import org.example.motorphui.service.AttendanceService;
import org.example.motorphui.service.SalaryTaxCalculatorService;

/**
 * Controller for the Finance Payslip panel (hr_payslip.fxml).
 *
 * Finance selects an employee from {@link FinancePayroll}, which calls
 * {@link #setEmployee}. The Finance user then picks both a <em>month</em>
 * and a <em>year</em> to generate the payslip for that exact pay period.
 * Changing either picker immediately recomputes the displayed figures.
 */
public class FinancePayslip {

    // ── Services ─────────────────────────────────────────────────────────────

    private final SalaryTaxCalculatorService salaryCalculator  = new SalaryTaxCalculator();
    private final AttendanceService           attendanceService = new AttendanceDAO();

    private static final NumberFormat PESO =
            NumberFormat.getCurrencyInstance(new Locale("fil", "PH"));

    // ── FXML — Header ─────────────────────────────────────────────────────────

    @FXML private AnchorPane root;
    @FXML private Label      periodLabel;        // "Pay Period: June 2024"
    @FXML private ChoiceBox<String>  MonthCHBox;
    @FXML private ChoiceBox<Integer> YearCHBox;  // ← new year picker

    // ── FXML — Employee Info ──────────────────────────────────────────────────

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
    @FXML private Label basicPayLabel;
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
        // ── Month picker ──────────────────────────────────────────────────
        MonthCHBox.setItems(FXCollections.observableArrayList(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"));

        int currentYear = Year.now().getValue();
        List<Integer> years = new ArrayList<>();
        for (int y = currentYear - 5; y <= currentYear + 0; y++) {
            years.add(y);
        }
        YearCHBox.setItems(FXCollections.observableArrayList(years));

        // Default: 2024 if in the list (matches the sample CSV data), else current year
        int defaultYear = years.contains(2024) ? 2024 : currentYear;
        YearCHBox.getSelectionModel().select(Integer.valueOf(defaultYear));

        // ── Listeners — either picker change triggers recalculation ───────
        MonthCHBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, month) -> tryRecalculate());

        YearCHBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, year) -> tryRecalculate());
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Called by {@link FinancePayroll} when an employee row is selected.
     * Fills all static employee fields; computed fields wait for the user
     * to choose a pay period.
     */
    public void setEmployee(AllEmployee employee) {
        this.employee = employee;

        // Reset month but keep year at its default
        MonthCHBox.getSelectionModel().clearSelection();
        periodLabel.setText("Pay Period: — Select month & year —");

        populateEmployeeInfo(employee);
        populateStaticRates(employee);
        clearDynamicLabels();

        // Show deductions immediately (based on contractual basic salary)
        // so Finance can see them before a month is chosen
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

    /**
     * Guards recalculation: requires both a month and a year to be chosen
     * as well as an employee to have been set.
     */
    private void tryRecalculate() {
        String  month = MonthCHBox.getSelectionModel().getSelectedItem();
        Integer year  = YearCHBox.getSelectionModel().getSelectedItem();
        if (employee == null || month == null || year == null) return;

        String yearStr = String.valueOf(year);
        periodLabel.setText("Pay Period: " + month + " " + yearStr);
        recalculate(month, yearStr);
    }

    private void populateEmployeeInfo(AllEmployee e) {
        empNumLabel    .setText(e.getEmployeeNumber());
        NameLabel      .setText(e.getFirstName() + " " + e.getLastName());
        positionLabel  .setText(e.getPosition());
        statusLabel    .setText(e.getStatus());
        supervisorLabel.setText(e.getImmediateSupervisor());
        BdayLabel      .setText(e.getBirthday());
        PhoneLabel     .setText(e.getPhoneNumber());
        AddressLabel   .setText(e.getAddress());
        sssLabel       .setText(e.getSss());
        philHealthLabel.setText(e.getPhilHealth());
        tinLabel       .setText(e.getTin());
        pagIbigLabel   .setText(e.getPagIbig());
    }

    private void populateStaticRates(AllEmployee e) {
        RateLabel      .setText(PESO.format(parse(e.getHourlyRate())));
        RiceLabel      .setText(PESO.format(parse(e.getRiceSubsidy())));
        PhoneAllowLabel.setText(PESO.format(parse(e.getPhoneAllowance())));
        ClothingLabel  .setText(PESO.format(parse(e.getClothingAllowance())));
    }

    private void clearDynamicLabels() {
        hoursWorkedLabel.setText("—");
        basicPayLabel   .setText("—");
        GrossLabel      .setText("—");
        NetLabel        .setText("—");
    }

    /**
     * Fetches attendance hours for the given month + year, then recomputes
     * all earnings, deductions, and net pay.
     */
    private void recalculate(String month, String year) {
        double hours       = attendanceService.getMonthlyHours(
                                 employee.getEmployeeNumber(), month, year);
        double hourlyRate  = parse(employee.getHourlyRate());
        double basicPay    = hours * hourlyRate;
        double rice        = parse(employee.getRiceSubsidy());
        double phone       = parse(employee.getPhoneAllowance());
        double clothing    = parse(employee.getClothingAllowance());
        double grossPay    = basicPay + rice + phone + clothing;

        // Deductions are always computed against the contractual basic salary
        double basic           = parse(employee.getBasicSalary());
        double sss             = salaryCalculator.calculateSSSContribution(basic);
        double ph              = salaryCalculator.calculatePhilHealthContribution(basic);
        double pi              = salaryCalculator.calculatePagibigContribution(basic);
        double tax             = salaryCalculator.calculateWithholdingTax(basic, sss, ph, pi);
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
            System.err.println("[FinancePayslip] Cannot parse numeric value: '" + value + "'");
            return 0.0;
        }
    }
}
