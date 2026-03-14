package org.example.motorphui.model;

/**
 * {@link SystemUser} specialisation representing an authenticated
 * <em>IT Administrator</em>.
 *
 * <p>IT users are identified solely by their {@code username} (stored in
 * {@code motorph_it_credentials.csv}). They have no employee record in the main
 * employee data file, so this class holds only IT-specific identity and role
 * information.
 *
 * <h3>OOP principles demonstrated</h3>
 * <ul>
 *   <li><b>Encapsulation</b> — all fields are inherited as {@code private}
 *       from {@link SystemUser}; this class adds no mutable state.</li>
 *   <li><b>Polymorphism</b> — {@link #getDisplayRole()} and
 *       {@link #getSystemRole()} override the abstract methods in
 *       {@link SystemUser} with IT-specific values.</li>
 *   <li><b>Single Responsibility</b> — this class knows only about the IT
 *       role; ticket management logic lives in the DAO and UI layers.</li>
 * </ul>
 */
public class ITUser extends SystemUser {

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Creates an {@code ITUser} with the given IT portal username.
     *
     * @param username the IT credentials username (must not be blank)
     */
    public ITUser(String username) {
        super(username);
    }

    // ── SystemUser abstract method implementations ─────────────────────────────

    /**
     * Returns the human-readable role label for IT administrators.
     * POLYMORPHISM — overrides abstract method from {@link SystemUser}.
     */
    @Override
    public String getDisplayRole() {
        return "IT Administrator";
    }

    /**
     * Returns the machine-readable role constant used for session routing.
     * POLYMORPHISM — overrides abstract method from {@link SystemUser}.
     */
    @Override
    public String getSystemRole() {
        return "IT";
    }
}
