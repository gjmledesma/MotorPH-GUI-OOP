package org.example.motorphui.session;

import org.example.motorphui.model.AllEmployee;

/**
 * Application-wide singleton that stores the employee currently logged in.
 *
 * OOP PRINCIPLES DEMONSTRATED:
 *   ENCAPSULATION — The currentEmployee field is private; access is controlled
 *                   via a validated setter that rejects null values.
 *   ABSTRACTION   — Provides a clean session API (set, get, clear, isActive)
 *                   without exposing the underlying storage mechanism.
 */
public class SessionManager {

    private static final SessionManager INSTANCE = new SessionManager();

    // ── Private field (ENCAPSULATION) ─────────────────────────────────────────
    private AllEmployee currentEmployee;

    private SessionManager() {}

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public AllEmployee getCurrentEmployee() {
        return currentEmployee;
    }

    /**
     * Returns true if an employee session is currently active.
     */
    public boolean isSessionActive() {
        return currentEmployee != null;
    }

    // ── Setters (validated — ENCAPSULATION) ───────────────────────────────────

    /**
     * Stores the currently authenticated employee.
     *
     * @param employee the logged-in employee (must not be null)
     * @throws IllegalArgumentException if employee is null
     */
    public void setCurrentEmployee(AllEmployee employee) {
        if (employee == null) {
            throw new IllegalArgumentException(
                    "SessionManager: cannot set a null employee. Call clearSession() to log out.");
        }
        this.currentEmployee = employee;
    }

    /**
     * Ends the current session by clearing the stored employee.
     */
    public void clearSession() {
        this.currentEmployee = null;
    }
}
