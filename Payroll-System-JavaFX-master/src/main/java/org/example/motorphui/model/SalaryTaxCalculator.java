package org.example.motorphui.model;

import org.example.motorphui.service.SalaryTaxCalculatorService;

/**
 * Philippine payroll deduction calculator.
 *
 * SSS  — bracket table replaces 47 if-else chains with a single formula.
 * Tax  — bracket table replaces 6 nested if-else blocks with a single loop.
 * PhilHealth and Pag-Ibig were already formula-based and are left as-is.
 */
public class SalaryTaxCalculator implements SalaryTaxCalculatorService {

    // ── SSS Lookup ────────────────────────────────────────────────────────────
    // Bracket layout (from the official SSS table):
    //   salary < 3,250               → ₱135.00
    //   3,250 ≤ salary < 3,750       → ₱157.50
    //   every additional ₱500 band   → +₱22.50
    //   salary ≥ 24,750              → ₱1,125.00  (ceiling)

    private static final double SSS_FLOOR_SALARY      = 3_250.0;
    private static final double SSS_BAND_WIDTH        =   500.0;
    private static final double SSS_BASE_CONTRIBUTION =   135.0;
    private static final double SSS_STEP              =    22.5;
    private static final double SSS_MAX_CONTRIBUTION  = 1_125.0;

    @Override
    public double calculateSSSContribution(double basicSalary) {
        if (basicSalary < SSS_FLOOR_SALARY) return SSS_BASE_CONTRIBUTION;

        // How many full 500-peso bands above the floor?  (+1 because the first
        // band above the floor already bumps from 135→157.50)
        int bracket = (int) Math.min(
                Math.floor((basicSalary - SSS_FLOOR_SALARY) / SSS_BAND_WIDTH) + 1,
                (long) ((SSS_MAX_CONTRIBUTION - SSS_BASE_CONTRIBUTION) / SSS_STEP));

        return SSS_BASE_CONTRIBUTION + bracket * SSS_STEP;
    }

    // ── PhilHealth ───────────────────────────────────────────────────────────
    // 3 % of basic salary, split equally → employee pays half.
    // Monthly premium is capped at ₱1,800 (i.e. salary cap ₱60,000).

    @Override
    public double calculatePhilHealthContribution(double basicSalary) {
        double premium = basicSalary * 0.03;
        return Math.min(premium, 1_800.0) / 2.0;
    }

    // ── Pag-Ibig ─────────────────────────────────────────────────────────────

    @Override
    public double calculatePagibigContribution(double basicSalary) {
        if (basicSalary >= 1_000 && basicSalary <= 1_500) return basicSalary * 0.01;
        if (basicSalary > 1_500)                          return basicSalary * 0.02;
        return 0;
    }

    // ── Withholding Tax ───────────────────────────────────────────────────────
    // BIR monthly tax table — each row: { lower bound (exclusive), base tax, marginal rate }
    // Sorted descending so the first matching row is applied immediately.

    private static final double[][] TAX_BRACKETS = {
        //  lower bound    base tax      marginal rate
        {  666_667.00,  200_833.33,    0.35 },
        {  166_667.00,   40_833.33,    0.32 },
        {   66_667.00,   10_833.00,    0.30 },
        {   33_333.00,    2_500.00,    0.25 },
        {   20_833.00,        0.00,    0.20 },
    };

    @Override
    public double calculateWithholdingTax(double basicSalary,
                                          double sss,
                                          double philhealth,
                                          double pagibig) {
        double taxable = basicSalary - (sss + philhealth + pagibig);

        for (double[] bracket : TAX_BRACKETS) {
            if (taxable > bracket[0]) {
                return bracket[1] + (taxable - bracket[0]) * bracket[2];
            }
        }
        return 0; // taxable income ≤ ₱20,832 → tax-exempt
    }
}
