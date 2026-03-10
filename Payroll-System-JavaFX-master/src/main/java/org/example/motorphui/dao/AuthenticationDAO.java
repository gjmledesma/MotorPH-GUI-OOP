package org.example.motorphui.dao;

import org.example.motorphui.model.AllEmployee;
import org.example.motorphui.model.AllEmployeePublic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * DAO for all authentication operations.  Reads credential CSVs and validates
 * login attempts for all four user roles.
 *
 * OOP PRINCIPLES DEMONSTRATED:
 *   INHERITANCE   — Extends BaseDAO, inheriting file-resolution helpers.
 *   ABSTRACTION   — Implements IAuthDAO, hiding CSV parsing from callers.
 *   ENCAPSULATION — File paths and parsing logic are private; callers use
 *                   only the typed public methods.
 *
 * BACKWARD COMPATIBILITY: Static delegate methods are retained so that
 * existing login controllers that call AuthenticationDAO.authenticateHR(…)
 * continue to compile unchanged.
 */
public class AuthenticationDAO extends BaseDAO implements IAuthDAO {

    // ── Singleton used by the static delegate methods ─────────────────────────
    private static final AuthenticationDAO INSTANCE = new AuthenticationDAO();

    // ── CSV resource paths ─────────────────────────────────────────────────────
    private static final String EMPLOYEE_CREDENTIALS   = "/org/example/motorphui/data/motorph_employee_credentials.csv";
    private static final String HR_CREDENTIALS         = "/org/example/motorphui/data/motorph_hr_credentials.csv";
    private static final String FINANCE_CREDENTIALS    = "/org/example/motorphui/data/motorph_finance_credentials.csv";
    private static final String IT_CREDENTIALS         = "/org/example/motorphui/data/motorph_it_credentials.csv";
    private static final String EMPLOYEE_DATA          = "/org/example/motorphui/data/motorph_employee_data.csv";

    // ── IAuthDAO implementation (instance methods) ────────────────────────────

    @Override
    public boolean authenticateEmployee(String empId, String username, String password) {
        return authenticate(empId, username, password);
    }

    @Override
    public boolean authenticateHR(String username, String password) {
        return checkCredentials(HR_CREDENTIALS, username, password, 2);
    }

    @Override
    public boolean authenticateFinance(String username, String password) {
        return checkCredentials(FINANCE_CREDENTIALS, username, password, 2);
    }

    @Override
    public boolean authenticateIT(String username, String password) {
        return checkCredentials(IT_CREDENTIALS, username, password, 2);
    }

    @Override
    public AllEmployee getEmployeeData(String empId) {
        return findEmployee(empId);
    }

    // ── Static delegate methods (backward compatibility) ─────────────────────

    /**
     * Authenticates a HR user.  Static delegate — callers may use either form:
     *   AuthenticationDAO.authenticateHR(u, p)
     *   new AuthenticationDAO().authenticateHR(u, p)
     */
//    public static boolean authenticateHR(String username, String password) {
//        return INSTANCE.checkCredentials(HR_CREDENTIALS, username, password, 2);
//    }
//
//    /** Static delegate for Finance authentication. */
//    public static boolean authenticateFinance(String username, String password) {
//        return INSTANCE.checkCredentials(FINANCE_CREDENTIALS, username, password, 2);
//    }
//
//    /** Static delegate for IT authentication. */
//    public static boolean authenticateIT(String username, String password) {
//        return INSTANCE.checkCredentials(IT_CREDENTIALS, username, password, 2);
//    }

    /**
     * Authenticates an employee using employee-ID, username and password.
     * Static delegate.
     */
    public static boolean authenticate(String empId, String username, String password) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                AuthenticationDAO.class.getResourceAsStream(EMPLOYEE_CREDENTIALS)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 3) {
                    if (data[0].trim().equals(empId.trim())
                            && data[1].trim().equals(username.trim())
                            && data[2].trim().equals(password.trim())) {
                        return true;
                    }
                }
            }
        } catch (IOException | NullPointerException e) {
            System.err.println("[AuthenticationDAO] Error authenticating employee: " + e.getMessage());
        }
        return false;
    }

    /**
     * Fetches the full employee record for the given employee ID.
     * Static delegate retained for backward compatibility.
     */
//    public static AllEmployee getEmployeeData(String empId) {
//        return INSTANCE.findEmployee(empId);
//    }

    // ── Private helpers ────────────────────────────────────────────────────────

    /**
     * Generic 2-column credential check (username, password).
     *
     * @param resourcePath classpath path to the credentials CSV
     * @param username     username to match in column 0
     * @param password     password to match in column 1
     * @param minCols      minimum expected columns in the CSV row
     */
    private boolean checkCredentials(String resourcePath, String username,
                                     String password, int minCols) {
        try (BufferedReader reader = openReader(resourcePath)) {
            if (reader == null) return false;
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= minCols) {
                    if (data[0].trim().equals(username.trim())
                            && data[1].trim().equals(password.trim())) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[AuthenticationDAO] Error reading credentials: " + e.getMessage());
        }
        return false;
    }

    /** Reads the employee data CSV and returns the matching employee record. */
    private AllEmployee findEmployee(String empId) {
        try (BufferedReader reader = openReader(EMPLOYEE_DATA)) {
            if (reader == null) return null;
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) { header = false; continue; }
                String[] data = line.split(",", -1);
                if (data.length == 19 && data[0].trim().equals(empId.trim())) {
                    return new AllEmployeePublic(
                            data[0], data[1], data[2], data[3], data[4],
                            data[5], data[6], data[7], data[8], data[9],
                            data[10], data[11], data[12], data[13], data[14],
                            data[15], data[16], data[17], data[18]);
                }
            }
        } catch (IOException e) {
            System.err.println("[AuthenticationDAO] Error fetching employee data: " + e.getMessage());
        }
        return null;
    }
}
