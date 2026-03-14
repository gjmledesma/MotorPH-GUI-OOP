package org.example.motorphui.service;

/**
 * Payroll deduction calculator.
 *
 * Moved from the model package to the service package where business logic
 * correctly belongs.  The model-layer SalaryTaxCalculator is retained as a
 * thin subclass for backward compatibility.
 *
 */
public class PayrollCalculatorImpl implements IPayrollService, SalaryTaxCalculatorService {

    // ── SSS bracket constants ─────────────────────────────────────────────────
    private static final double SSS_FLOOR_SALARY      = 3_250.0;
    private static final double SSS_BAND_WIDTH        =   500.0;
    private static final double SSS_BASE_CONTRIBUTION =   135.0;
    private static final double SSS_STEP              =    22.5;
    private static final double SSS_MAX_CONTRIBUTION  = 1_125.0;

    // ── Tax bracket table ─────────────────────────────────────────────────────
    // Each row: { lower bound (exclusive), base tax, marginal rate }
    private static final double[][] TAX_BRACKETS = {
        { 666_667.00,  200_833.33, 0.35 },
        { 166_667.00,   40_833.33, 0.32 },
        {  66_667.00,   10_833.00, 0.30 },
        {  33_333.00,    2_500.00, 0.25 },
        {  20_833.00,        0.00, 0.20 },
    };

    // ── IPayrollService / SalaryTaxCalculatorService ──────────────────────────

    @Override
    public double calculateSSSContribution(double basicSalary) {
        if (basicSalary < SSS_FLOOR_SALARY) return SSS_BASE_CONTRIBUTION;
        int bracket = (int) Math.min(
                Math.floor((basicSalary - SSS_FLOOR_SALARY) / SSS_BAND_WIDTH) + 1,
                (long) ((SSS_MAX_CONTRIBUTION - SSS_BASE_CONTRIBUTION) / SSS_STEP));
        return SSS_BASE_CONTRIBUTION + bracket * SSS_STEP;
    }

    @Override
    public double calculatePhilHealthContribution(double basicSalary) {
        double premium = basicSalary * 0.03;
        return Math.min(premium, 1_800.0) / 2.0;
    }

    @Override
    public double calculatePagibigContribution(double basicSalary) {
        if (basicSalary >= 1_000 && basicSalary <= 1_500) return basicSalary * 0.01;
        if (basicSalary > 1_500)                           return basicSalary * 0.02;
        return 0;
    }

    /**
     * Calculates withholding tax on the standard four deductions.
     * POLYMORPHISM — overloaded (standard form).
     */
    @Override
    public double calculateWithholdingTax(double basicSalary, double sss,
                                           double philhealth, double pagibig) {
        return computeTax(basicSalary - (sss + philhealth + pagibig));
    }

    /**
     * Calculates withholding tax with additional deductions applied before tax.
     * POLYMORPHISM — overloaded (extended form, adds extraDeductions parameter).
     *
     * @param extraDeductions any other pre-tax deductions (e.g. loan repayments)
     */
    public double calculateWithholdingTax(double basicSalary, double sss,
                                           double philhealth, double pagibig,
                                           double extraDeductions) {
        return computeTax(basicSalary - (sss + philhealth + pagibig + extraDeductions));
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    private double computeTax(double taxable) {
        for (double[] bracket : TAX_BRACKETS) {
            if (taxable > bracket[0]) {
                return bracket[1] + (taxable - bracket[0]) * bracket[2];
            }
        }
        return 0; // taxable income ≤ ₱20,832 — tax-exempt
    }
}
