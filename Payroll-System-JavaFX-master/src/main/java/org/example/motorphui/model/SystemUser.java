package org.example.motorphui.model;

/**
 * Abstract base class representing any authenticated user of the MotorPH system.
 * Every concrete user type (Employee, HR, Finance, IT) extends this class and
 * provides its own implementation of the two abstract methods, enabling
 * polymorphic handling of sessions regardless of the user's role.
 */
public abstract class SystemUser {

    // ── Private field (ENCAPSULATION) ─────────────────────────────────────────
    private final String username;

    // ── Constructor ───────────────────────────────────────────────────────────
    protected SystemUser(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("SystemUser: username cannot be blank.");
        }
        this.username = username.trim();
    }

    // ── Getter ────────────────────────────────────────────────────────────────
    public String getUsername() {
        return username;
    }

    // ── Abstract methods (ABSTRACTION + POLYMORPHISM) ─────────────────────────

    /**
     * Human-readable role label shown in UI elements (e.g. "HR Staff",
     * "Finance Officer", "IT Administrator", "Regular Employee").
     * <p>POLYMORPHISM — each subclass returns its own label.</p>
     */
    public abstract String getDisplayRole();

    /**
     * Machine-readable role identifier used internally for routing and
     * access-control decisions (e.g. "HR", "FINANCE", "IT", "EMPLOYEE").
     * <p>POLYMORPHISM — each subclass returns its own constant.</p>
     */
    public abstract String getSystemRole();

    // ── toString ──────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return "[" + getSystemRole() + "] " + username;
    }
}
