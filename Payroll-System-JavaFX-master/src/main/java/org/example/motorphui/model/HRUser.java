package org.example.motorphui.model;

/**
 * {@link SystemUser} specialisation representing an authenticated
 * <em>Human Resources</em> staff member.
 *
 * <p>HR users are identified solely by their {@code username} (stored in
 * {@code motorph_hr_credentials.csv}). They have no employee record in the
 * main employee data file, so this class holds only the HR-specific identity
 * and role information.
 *
 * <h3>OOP principles demonstrated</h3>
 * <ul>
 *   <li><b>Encapsulation</b> — all fields are inherited as {@code private}
 *       from {@link SystemUser}; this class adds no mutable state.</li>
 *   <li><b>Polymorphism</b> — {@link #getDisplayRole()} and
 *       {@link #getSystemRole()} override the abstract methods in
 *       {@link SystemUser} with HR-specific values.</li>
 *   <li><b>Single Responsibility</b> — this class knows only about the
 *       HR role; payroll and employee-data concerns belong elsewhere.</li>
 * </ul>
 */
public class HRUser extends SystemUser {

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Creates an {@code HRUser} with the given HR portal username.
     *
     * @param username the HR credentials username (must not be blank)
     */
    public HRUser(String username) {
        super(username);
    }

    // ── SystemUser abstract method implementations ─────────────────────────────

    /**
     * Returns the human-readable role label for HR staff.
     * POLYMORPHISM — overrides abstract method from {@link SystemUser}.
     */
    @Override
    public String getDisplayRole() {
        return "HR Staff";
    }

    /**
     * Returns the machine-readable role constant used for session routing.
     * POLYMORPHISM — overrides abstract method from {@link SystemUser}.
     */
    @Override
    public String getSystemRole() {
        return "HR";
    }
}
