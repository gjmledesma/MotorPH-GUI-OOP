package org.example.motorphui.dao;

import org.example.motorphui.model.AllEmployee;

/**
 * Data-access contract for authentication operations.
 *
 * OOP — ABSTRACTION: Defines the authentication API without exposing
 * implementation details (CSV parsing, file paths).
 */
public interface IAuthDAO {
    boolean authenticateEmployee(String empId, String username, String password);
    boolean authenticateHR(String username, String password);
    boolean authenticateFinance(String username, String password);
    boolean authenticateIT(String username, String password);
    AllEmployee getEmployeeData(String empId);
}
