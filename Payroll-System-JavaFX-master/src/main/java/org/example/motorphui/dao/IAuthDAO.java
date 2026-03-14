package org.example.motorphui.dao;

import org.example.motorphui.model.AllEmployee;
import org.example.motorphui.model.EmployeeUser;
import org.example.motorphui.model.FinanceUser;
import org.example.motorphui.model.HRUser;
import org.example.motorphui.model.ITUser;

/**
 * Data-access contract for authentication operations.
 *
 * <h3>What changed</h3>
 * <p>Four new factory methods have been added — one for each user type —
 * that return a fully constructed {@code SystemUser} subclass after successful
 * authentication.  The original five methods are <em>unchanged</em>.</p>
 */
public interface IAuthDAO {

    // ── Original authentication methods (UNCHANGED) ───────────────────────────
    boolean authenticateEmployee(String empId, String username, String password);
    boolean authenticateHR(String username, String password);
    boolean authenticateFinance(String username, String password);
    boolean authenticateIT(String username, String password);
    AllEmployee getEmployeeData(String empId);

    // ── New user-model factory methods ────────────────────────────────────────

    /**
     * Authenticates an employee and, on success, returns a fully populated
     * {@link EmployeeUser} wrapping the employee's data record.
     *
     * @param empId    the employee ID from the login form
     * @param username the username from the login form
     * @param password the password from the login form
     * @return an {@link EmployeeUser} if credentials are valid, or {@code null}
     */
    EmployeeUser getEmployeeUser(String empId, String username, String password);

    /**
     * Authenticates an HR user and, on success, returns an {@link HRUser}
     * model containing the verified username.
     *
     * @param username the HR portal username
     * @param password the HR portal password
     * @return an {@link HRUser} if credentials are valid, or {@code null}
     */
    HRUser getHRUser(String username, String password);

    /**
     * Authenticates a Finance user and, on success, returns a {@link FinanceUser}
     * model containing the verified username.
     *
     * @param username the Finance portal username
     * @param password the Finance portal password
     * @return a {@link FinanceUser} if credentials are valid, or {@code null}
     */
    FinanceUser getFinanceUser(String username, String password);

    /**
     * Authenticates an IT user and, on success, returns an {@link ITUser}
     * model containing the verified username.
     *
     * @param username the IT portal username
     * @param password the IT portal password
     * @return an {@link ITUser} if credentials are valid, or {@code null}
     */
    ITUser getITUser(String username, String password);
}
