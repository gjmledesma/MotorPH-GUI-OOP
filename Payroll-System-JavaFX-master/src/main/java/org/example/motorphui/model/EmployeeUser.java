package org.example.motorphui.model;

/**
 * {@link SystemUser} specialisation representing a logged-in <em>Employee</em>.
 *
 * <p>Uses <b>composition</b> to wrap an {@link AllEmployee} instance, delegating
 * all employee-specific data access to it while participating in the
 * {@code SystemUser} hierarchy for unified session management.
 *
 */
public class EmployeeUser extends SystemUser {

    // ── Private field (ENCAPSULATION) ─────────────────────────────────────────
    private final AllEmployee employee;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Creates an {@code EmployeeUser} from an already-loaded {@link AllEmployee}.
     *
     * @param employee the authenticated employee (must not be {@code null})
     * @throws IllegalArgumentException if {@code employee} is {@code null}
     */
    public EmployeeUser(AllEmployee employee) {
        // Use employee number as the canonical username in the SystemUser hierarchy
        super(employee != null ? employee.getEmployeeNumber()
                               : throwNullEmployee());
        if (employee == null) {
            throw new IllegalArgumentException("EmployeeUser: employee must not be null.");
        }
        this.employee = employee;
    }

    // ── Getter ────────────────────────────────────────────────────────────────

    /**
     * Returns the underlying {@link AllEmployee} data object.
     * UI controllers that need full employee data call this method.
     */
    public AllEmployee getEmployee() {
        return employee;
    }

    // ── SystemUser abstract method implementations ─────────────────────────────

    /**
     * Delegates to {@link AllEmployee#getDisplayRole()} so that
     * {@link RegularEmployee} and {@link ProbationaryEmployee} each return
     * their own label without any additional branching.
     * POLYMORPHISM — runtime dispatch through the AllEmployee hierarchy.
     */
    @Override
    public String getDisplayRole() {
        return employee.getDisplayRole();   // e.g. "Regular Employee" or "Probationary Employee"
    }

    /**
     * Returns the system-level role constant used for routing and session checks.
     * POLYMORPHISM — overrides the abstract method from {@link SystemUser}.
     */
    @Override
    public String getSystemRole() {
        return "EMPLOYEE";
    }

    // ── Utility ───────────────────────────────────────────────────────────────

    /** Helper for inline null-check in the super() call. */
    private static String throwNullEmployee() {
        throw new IllegalArgumentException("EmployeeUser: employee must not be null.");
    }

    @Override
    public String toString() {
        return "[EMPLOYEE] " + employee.getFirstName() + " " + employee.getLastName()
                + " (" + employee.getEmployeeNumber() + ")"
                + " – " + employee.getDisplayRole();
    }
}
