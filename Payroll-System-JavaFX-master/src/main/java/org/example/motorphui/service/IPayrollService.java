package org.example.motorphui.service;

/**
 * Business-logic contract for Philippine payroll calculations.
 */
public interface IPayrollService {
    double calculateSSSContribution(double basicSalary);
    double calculatePhilHealthContribution(double basicSalary);
    double calculatePagibigContribution(double basicSalary);
    double calculateWithholdingTax(double basicSalary, double sss,
                                   double philHealth, double pagIbig);

    /**
     * Calculates net pay after all mandatory deductions.
     * Default implementation — overridable.
     */
    default double calculateNetPay(double basicSalary, double sss,
                                   double philHealth, double pagIbig,
                                   double withholdingTax) {
        return basicSalary - sss - philHealth - pagIbig - withholdingTax;
    }
}
