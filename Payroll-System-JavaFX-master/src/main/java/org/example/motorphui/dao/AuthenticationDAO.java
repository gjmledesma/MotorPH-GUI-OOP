package org.example.motorphui.dao;

import org.example.motorphui.model.AllEmployee;
import org.example.motorphui.model.AllEmployeePublic;
import org.example.motorphui.model.EmployeeUser;
import org.example.motorphui.model.FinanceUser;
import org.example.motorphui.model.HRUser;
import org.example.motorphui.model.ITUser;
import org.example.motorphui.util.DataFileManager;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * CSV-backed implementation of {@link IAuthDAO}.
 *
 * <h3>What changed</h3>
 * <p>The four new factory methods ({@link #getEmployeeUser},
 * {@link #getHRUser}, {@link #getFinanceUser}, {@link #getITUser}) build and
 * return the appropriate {@code SystemUser} subclass after verifying
 * credentials. All original methods are <em>identical</em> to the previous
 * version.</p>
 *
 * <h3>OOP principles</h3>
 * <ul>
 *   <li><b>Encapsulation</b> — credential checking is kept in the private
 *       helpers; callers never touch CSV parsing directly.</li>
 *   <li><b>Single Responsibility</b> — this class authenticates users and
 *       constructs their model objects; session storage is
 *       {@code SessionManager}'s responsibility.</li>
 *   <li><b>Polymorphism</b> — each factory method returns the correct
 *       {@code SystemUser} subtype; callers can store the result as
 *       {@code SystemUser} without knowing the concrete class.</li>
 * </ul>
 */
public class AuthenticationDAO extends BaseDAO implements IAuthDAO {

    private static final AuthenticationDAO INSTANCE = new AuthenticationDAO();

    // ── CSV file names ────────────────────────────────────────────────────────
    private static final String EMPLOYEE_CREDENTIALS = "motorph_employee_credentials.csv";
    private static final String HR_CREDENTIALS       = "motorph_hr_credentials.csv";
    private static final String FINANCE_CREDENTIALS  = "motorph_finance_credentials.csv";
    private static final String IT_CREDENTIALS       = "motorph_it_credentials.csv";
    private static final String EMPLOYEE_DATA        = "motorph_employee_data.csv";

    // ── IAuthDAO — original methods (UNCHANGED) ───────────────────────────────

    @Override
    public boolean authenticateEmployee(String empId, String username, String password) {
        return authenticate(empId, username, password);
    }

    @Override
    public boolean authenticateHR(String username, String password) {
        return checkCredentials(HR_CREDENTIALS, username, password);
    }

    @Override
    public boolean authenticateFinance(String username, String password) {
        return checkCredentials(FINANCE_CREDENTIALS, username, password);
    }

    @Override
    public boolean authenticateIT(String username, String password) {
        return checkCredentials(IT_CREDENTIALS, username, password);
    }

    @Override
    public AllEmployee getEmployeeData(String empId) {
        return INSTANCE.findEmployee(empId);
    }

    // ── IAuthDAO — new user-model factory methods ─────────────────────────────

    /**
     * Verifies employee credentials and, on success, returns an
     * {@link EmployeeUser} wrapping the employee's full data record.
     *
     */
    @Override
    public EmployeeUser getEmployeeUser(String empId, String username, String password) {
        if (!authenticate(empId, username, password)) return null;
        AllEmployee emp = findEmployee(empId);
        return (emp != null) ? new EmployeeUser(emp) : null;
    }

    /**
     * Verifies HR credentials and, on success, returns an {@link HRUser} model.
     *
     * POLYMORPHISM — the returned {@code HRUser} can be stored as a
     * {@code SystemUser} in {@code SessionManager} alongside other user types.
     */
    @Override
    public HRUser getHRUser(String username, String password) {
        return checkCredentials(HR_CREDENTIALS, username, password)
                ? new HRUser(username)
                : null;
    }

    /**
     * Verifies Finance credentials and, on success, returns a
     * {@link FinanceUser} model.
     */
    @Override
    public FinanceUser getFinanceUser(String username, String password) {
        return checkCredentials(FINANCE_CREDENTIALS, username, password)
                ? new FinanceUser(username)
                : null;
    }

    /**
     * Verifies IT credentials and, on success, returns an {@link ITUser} model.
     */
    @Override
    public ITUser getITUser(String username, String password) {
        return checkCredentials(IT_CREDENTIALS, username, password)
                ? new ITUser(username)
                : null;
    }

    // ── Static delegate (backward-compat with EmployeeLogin) ──────────────────

    /**
     * Authenticates an employee (empId + username + password).
     * Static so that {@link org.example.motorphui.ui.EmployeeLogin} can call
     * it without constructing an instance, preserving the original call-site.
     */
    public static boolean authenticate(String empId, String username, String password) {
        try (BufferedReader reader = DataFileManager.openReader(EMPLOYEE_CREDENTIALS)) {
            if (reader == null) return false;
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 3
                        && data[0].trim().equals(empId.trim())
                        && data[1].trim().equals(username.trim())
                        && data[2].trim().equals(password.trim())) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("[AuthenticationDAO] authenticate error: " + e.getMessage());
        }
        return false;
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    private boolean checkCredentials(String filename, String username, String password) {
        try (BufferedReader reader = DataFileManager.openReader(filename)) {
            if (reader == null) return false;
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2
                        && data[0].trim().equals(username.trim())
                        && data[1].trim().equals(password.trim())) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("[AuthenticationDAO] checkCredentials error: " + e.getMessage());
        }
        return false;
    }

    private AllEmployee findEmployee(String empId) {
        try (BufferedReader reader = DataFileManager.openReader(EMPLOYEE_DATA)) {
            if (reader == null) return null;
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) { header = false; continue; }
                String[] data = line.split(",", -1);
                if (data.length >= 19 && data[0].trim().equals(empId.trim())) {
                    return new AllEmployeePublic(
                            data[0], data[1], data[2], data[3], data[4],
                            data[5], data[6], data[7], data[8], data[9],
                            data[10], data[11], data[12], data[13], data[14],
                            data[15], data[16], data[17], data[18]);
                }
            }
        } catch (IOException e) {
            System.err.println("[AuthenticationDAO] findEmployee error: " + e.getMessage());
        }
        return null;
    }
}
