package org.example.motorphui.model;

/**
 * Backward-compatible concrete employee model used by legacy code that
 * instantiates AllEmployeePublic directly (e.g., AuthenticationDAO,
 * FinancePayroll).
 *
 * New code should prefer the factory method {@link AllEmployee#create} or
 * use {@link RegularEmployee} / {@link ProbationaryEmployee} directly.
 *
 */
public class AllEmployeePublic extends AllEmployee {

    public AllEmployeePublic(String employeeNumber, String lastName, String firstName,
                             String birthday, String address, String phoneNumber,
                             String sss, String philHealth, String tin, String pagIbig,
                             String status, String position, String immediateSupervisor,
                             String basicSalary, String riceSubsidy, String phoneAllowance,
                             String clothingAllowance, String grossSemiMonthlyRate,
                             String hourlyRate) {
        super(employeeNumber, lastName, firstName, birthday, address, phoneNumber,
              sss, philHealth, tin, pagIbig, status, position, immediateSupervisor,
              basicSalary, riceSubsidy, phoneAllowance, clothingAllowance,
              grossSemiMonthlyRate, hourlyRate);
    }

    /**
     * Derives the display role from the stored status field.
     * POLYMORPHISM — overrides abstract method from Person.
     */
    @Override
    public String getDisplayRole() {
        return isRegular() ? "Regular Employee" : "Probationary Employee";
    }

    /**
     * Returns the employment type based on the stored status value.
     * POLYMORPHISM — overrides abstract method from Person.
     */
    @Override
    public String getEmployeeType() {
        return isRegular() ? "Regular" : "Probationary";
    }

    /**
     * Returns 1.0 for Regular employees, 0.80 for Probationary.
     * POLYMORPHISM — overrides abstract method from Person; result differs
     * from the hard-coded values in RegularEmployee / ProbationaryEmployee,
     * demonstrating runtime behaviour driven by stored state.
     */
    @Override
    public double getBenefitMultiplier() {
        return isRegular() ? 1.00 : 0.80;
    }

    // ── Private helper ────────────────────────────────────────────────────────
    private boolean isRegular() {
        return "Regular".equalsIgnoreCase(getStatus());
    }
}
