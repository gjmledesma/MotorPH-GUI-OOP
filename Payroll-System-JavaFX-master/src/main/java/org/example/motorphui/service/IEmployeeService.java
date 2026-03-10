package org.example.motorphui.service;

import org.example.motorphui.model.AllEmployee;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.Set;

/**
 * Business-logic contract for employee management operations.
 *
 * OOP — ABSTRACTION: Separates business rules (validation, duplicate checks)
 * from data-access details.
 */
public interface IEmployeeService {
    ObservableList<AllEmployee> getAllEmployees();
    AllEmployee findById(String employeeId);

    /**
     * Validates and persists a new employee.
     * @return list of validation error messages; empty means success.
     */
    List<String> addEmployee(AllEmployee employee);

    /**
     * Validates and updates an existing employee record.
     * @return list of validation error messages; empty means success.
     */
    List<String> updateEmployee(AllEmployee employee);

    void deleteEmployee(String employeeNumber);
    Set<String> getExistingEmployeeNumbers();
}
