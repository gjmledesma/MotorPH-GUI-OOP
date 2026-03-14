package org.example.motorphui.model;

/**
 * {@link SystemUser} specialisation representing an authenticated
 * Finance staff member.
 *
 * <p>Finance users are identified solely by their {@code username} (stored in
 * {@code motorph_finance_credentials.csv}). They have no employee record in the
 * main employee data file, so this class holds only Finance-specific identity
 * and role information.
 *
 */
public class FinanceUser extends SystemUser {

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Creates a {@code FinanceUser} with the given Finance portal username.
     *
     */
    public FinanceUser(String username) {
        super(username);
    }

    @Override
    public String getDisplayRole() {
        return "Finance Officer";
    }
    
    @Override
    public String getSystemRole() {
        return "FINANCE";
    }
}
