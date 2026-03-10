package org.example.motorphui.model;

/**
 * Concrete employee model for Regular (permanent) employees.
 *
 * OOP PRINCIPLES DEMONSTRATED:
 *   INHERITANCE   — Extends AllEmployee (which extends Person).
 *   POLYMORPHISM  — Overrides all three abstract methods from Person.
 *   ENCAPSULATION — Inherits all private fields; adds no new mutable state.
 */
public class RegularEmployee extends AllEmployee {

    public RegularEmployee(String employeeNumber, String lastName, String firstName,
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
     * Display label used in UI headings and reports.
     * POLYMORPHISM — overrides abstract method from Person.
     */
    @Override
    public String getDisplayRole() {
        return "Regular Employee";
    }

    /**
     * CSV-compatible status string.
     * POLYMORPHISM — overrides abstract method from Person.
     */
    @Override
    public String getEmployeeType() {
        return "Regular";
    }

    /**
     * Regular employees receive 100% of all benefits.
     * POLYMORPHISM — overrides abstract method from Person.
     */
    @Override
    public double getBenefitMultiplier() {
        return 1.00;
    }
}
