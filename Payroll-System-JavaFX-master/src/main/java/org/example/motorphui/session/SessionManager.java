package org.example.motorphui.session;

import org.example.motorphui.model.AllEmployee;
import org.example.motorphui.model.SystemUser;

/**
 * Application-wide singleton that stores the authenticated user for the
 * current session.
 *
 * <h3>What changed</h3>
 * <p>A second field, {@link #currentUser}, now tracks the {@link SystemUser}
 * model (Employee, HR, Finance, or IT) that has logged in.  All existing
 * {@code currentEmployee} methods remain <em>identical</em> so every UI
 * controller continues to compile and run without modification.</p>
 *
 * <h3>OOP principles</h3>
 * <ul>
 *   <li><b>Encapsulation</b> — both fields are {@code private}; exposed only
 *       through typed getters and validated setters.</li>
 *   <li><b>Single Responsibility</b> — this class only manages session
 *       state; authentication logic stays in the DAO layer.</li>
 *   <li><b>Polymorphism</b> — {@code currentUser} is typed as the abstract
 *       {@code SystemUser}, so the same field holds any concrete user type
 *       at runtime.</li>
 * </ul>
 */
public class SessionManager {

    private static final SessionManager INSTANCE = new SessionManager();

    // ── Private fields (ENCAPSULATION) ────────────────────────────────────────

    /** Legacy field — kept intact so all existing UI controllers compile. */
    private AllEmployee currentEmployee;

    /**
     * New field — holds the concrete {@link SystemUser} (EmployeeUser, HRUser,
     * FinanceUser, or ITUser) that is currently logged in.
     * Typed as the abstract base so the session manager is role-agnostic.
     */
    private SystemUser currentUser;

    // ── Singleton constructor ─────────────────────────────────────────────────
    private SessionManager() {}

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Existing API — UNCHANGED (all existing UI controllers remain compatible)
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Returns the {@link AllEmployee} currently stored in session, or
     * {@code null} if no employee session is active.
     *
     * <p>Unchanged from the original; called by {@code EmployeeViewSalary},
     * {@code EmployeeProfile}, {@code EmployeeAttendance}, etc.</p>
     */
    public AllEmployee getCurrentEmployee() {
        return currentEmployee;
    }

    /**
     * Returns {@code true} if an employee (not just any user) is logged in.
     * Preserves original behaviour relied on by employee-facing screens.
     */
    public boolean isSessionActive() {
        return currentEmployee != null;
    }

    /**
     * Stores the currently authenticated employee.
     *
     * @param employee the logged-in employee (must not be {@code null})
     * @throws IllegalArgumentException if {@code employee} is {@code null}
     */
    public void setCurrentEmployee(AllEmployee employee) {
        if (employee == null) {
            throw new IllegalArgumentException(
                    "SessionManager: cannot set a null employee. Call clearSession() to log out.");
        }
        this.currentEmployee = employee;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // New API — SystemUser session (supports all four user types)
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Returns the {@link SystemUser} currently stored in session, or
     * {@code null} if no user is logged in.
     *
     * <p>The concrete type at runtime will be one of:
     * {@code EmployeeUser}, {@code HRUser}, {@code FinanceUser}, or
     * {@code ITUser}.</p>
     *
     * <p>POLYMORPHISM — callers that only need {@code getDisplayRole()} or
     * {@code getSystemRole()} can work against the abstract base type
     * without casting.</p>
     */
    public SystemUser getCurrentUser() {
        return currentUser;
    }

    /**
     * Stores the concrete {@link SystemUser} that has just authenticated.
     * Called by each login controller after credentials are verified.
     *
     * @param user the logged-in user (must not be {@code null})
     * @throws IllegalArgumentException if {@code user} is {@code null}
     */
    public void setCurrentUser(SystemUser user) {
        if (user == null) {
            throw new IllegalArgumentException(
                    "SessionManager: cannot set a null user. Call clearSession() to log out.");
        }
        this.currentUser = user;
    }

    /**
     * Returns {@code true} if <em>any</em> user (employee or staff) is
     * currently logged in.
     */
    public boolean isAnySessionActive() {
        return currentUser != null || currentEmployee != null;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Session teardown
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Ends the current session by clearing both stored user references.
     */
    public void clearSession() {
        this.currentEmployee = null;
        this.currentUser     = null;
    }
}
