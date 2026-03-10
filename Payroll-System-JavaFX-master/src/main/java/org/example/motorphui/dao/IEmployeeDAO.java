package org.example.motorphui.dao;

import org.example.motorphui.model.AllEmployee;
import javafx.collections.ObservableList;
import java.util.Set;

/**
 * Data-access contract for employee CRUD operations.
 *
 * OOP — ABSTRACTION: Declares what can be done with employee records;
 * EmployeeDAOImpl provides the CSV-backed implementation.
 */
public interface IEmployeeDAO {
    /** Returns all employees from the data store. */
    ObservableList<AllEmployee> getAllEmployees();

    /** Returns the employee with the given ID, or null if not found. */
    AllEmployee findById(String employeeId);

    /** Persists a new employee record. */
    void addEmployee(AllEmployee employee);

    /** Replaces an existing employee record (matched by employee number). */
    void updateEmployee(AllEmployee employee);

    /** Removes the employee with the given employee number. */
    void deleteEmployee(String employeeNumber);

    /** Returns a snapshot of all current employee numbers for duplicate checking. */
    Set<String> getExistingEmployeeNumbers();
}
