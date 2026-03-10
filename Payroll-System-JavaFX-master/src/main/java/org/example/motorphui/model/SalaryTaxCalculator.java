package org.example.motorphui.model;

import org.example.motorphui.service.PayrollCalculatorImpl;
import org.example.motorphui.service.SalaryTaxCalculatorService;

/**
 * Retained for backward compatibility.
 *
 * The calculation logic has been moved to {@link PayrollCalculatorImpl} in
 * the service package, where business rules correctly belong.
 * This class extends PayrollCalculatorImpl so that any code instantiating
 * {@code new SalaryTaxCalculator()} continues to work without changes.
 *
 * OOP — INHERITANCE: Extends PayrollCalculatorImpl.
 *
 * @deprecated Use {@link PayrollCalculatorImpl} directly or via
 *             {@link org.example.motorphui.service.IPayrollService}.
 */
@Deprecated
public class SalaryTaxCalculator extends PayrollCalculatorImpl
        implements SalaryTaxCalculatorService {
    // All methods are inherited from PayrollCalculatorImpl.
}
