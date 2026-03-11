package org.example.motorphui.dao;

import org.example.motorphui.model.AllEmployee;
import org.example.motorphui.model.AllEmployeePublic;
import org.example.motorphui.util.DataFileManager;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * DAO for all authentication operations.
 *
 * All CSV reads now go through DataFileManager so they target the same
 * persistent external directory (~/.motorphui/data/) that the write operations
 * use.  Previously the static authenticate() method opened the classpath
 * stream directly, which could read a stale/unmodified copy of the file.
 *
 * OOP PRINCIPLES DEMONSTRATED:
 *   INHERITANCE   — Extends BaseDAO.
 *   ABSTRACTION   — Implements IAuthDAO.
 *   ENCAPSULATION — File names and parsing logic are private.
 */
public class AuthenticationDAO extends BaseDAO implements IAuthDAO {

    private static final AuthenticationDAO INSTANCE = new AuthenticationDAO();

    // ── CSV file names (resolved via DataFileManager) ─────────────────────────
    private static final String EMPLOYEE_CREDENTIALS = "motorph_employee_credentials.csv";
    private static final String HR_CREDENTIALS       = "motorph_hr_credentials.csv";
    private static final String FINANCE_CREDENTIALS  = "motorph_finance_credentials.csv";
    private static final String IT_CREDENTIALS       = "motorph_it_credentials.csv";
    private static final String EMPLOYEE_DATA        = "motorph_employee_data.csv";

    // ── Classpath resource paths (kept for BaseDAO.openReader compatibility) ──
    private static final String RES = "/org/example/motorphui/data/";

    // ── IAuthDAO implementation ───────────────────────────────────────────────

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

    // ── Static delegate methods (backward compatibility with login controllers) ─

//    public static boolean authenticateHR(String username, String password) {
//        return INSTANCE.checkCredentials(HR_CREDENTIALS, username, password);
//    }
//
//    public static boolean authenticateFinance(String username, String password) {
//        return INSTANCE.checkCredentials(FINANCE_CREDENTIALS, username, password);
//    }
//
//    public static boolean authenticateIT(String username, String password) {
//        return INSTANCE.checkCredentials(IT_CREDENTIALS, username, password);
//    }

    /**
     * Authenticates an employee (empId + username + password).
     * Reads via DataFileManager so it sees the same file as write operations.
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

//    /** Static delegate for fetching employee data. */
//    public static AllEmployee getEmployeeData(String empId) {
//        return INSTANCE.findEmployee(empId);
//    }

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
