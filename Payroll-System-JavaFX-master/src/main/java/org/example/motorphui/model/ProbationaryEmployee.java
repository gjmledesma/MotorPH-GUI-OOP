package org.example.motorphui.model;

/**
 * Concrete employee model for Probationary employees.
 */
public class ProbationaryEmployee extends AllEmployee {

    public ProbationaryEmployee(String employeeNumber, String lastName, String firstName,
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
     * POLYMORPHISM — overrides abstract method from Person with a different value
     * than RegularEmployee.getDisplayRole().
     */
    @Override
    public String getDisplayRole() {
        return "Probationary Employee";
    }

    /**
     * CSV-compatible status string.
     * POLYMORPHISM — overrides abstract method from Person.
     */
    @Override
    public String getEmployeeType() {
        return "Probationary";
    }

    /**
     * Probationary employees receive 80 % of full benefit entitlements.
     * POLYMORPHISM — overrides abstract method from Person with a different
     * multiplier than RegularEmployee.getBenefitMultiplier().
     */
    @Override
    public double getBenefitMultiplier() {
        return 0.80;
    }
}
