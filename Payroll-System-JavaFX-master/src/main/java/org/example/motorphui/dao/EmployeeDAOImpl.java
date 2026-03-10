package org.example.motorphui.dao;

import org.example.motorphui.model.AllEmployee;
import org.example.motorphui.model.AllEmployeePublic;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * CSV-backed implementation of {@link IEmployeeDAO}.
 *
 * Centralises ALL employee file I/O in one place, removing the raw CSV
 * reading/writing that was previously scattered across HREmployeeView,
 * FinancePayroll, and other UI controllers.
 *
 * OOP PRINCIPLES DEMONSTRATED:
 *   INHERITANCE   — Extends BaseDAO, reusing resolveFile(), openReader(),
 *                   appendRow(), and rewriteFile() helpers.
 *   ABSTRACTION   — Implements IEmployeeDAO; callers depend on the interface,
 *                   not the concrete class.
 *   ENCAPSULATION — All CSV paths and parsing logic are private.
 */
public class EmployeeDAOImpl extends BaseDAO implements IEmployeeDAO {

    private static final String CSV_PATH = "/org/example/motorphui/data/motorph_employee_data.csv";

    public static final String CSV_HEADER =
            "Employee #,Last Name,First Name,Birthday,Address,Phone Number," +
            "SSS #,PhilHealth #,TIN #,Pag-Ibig #,Status,Position," +
            "Immediate Supervisor,Basic Salary,Rice Subsidy,Phone Allowance," +
            "Clothing Allowance,Gross Semi-monthly Rate,Hourly Rate";

    // ── IEmployeeDAO implementation ───────────────────────────────────────────

    @Override
    public ObservableList<AllEmployee> getAllEmployees() {
        ObservableList<AllEmployee> list = FXCollections.observableArrayList();
        for (String line : readDataLines(CSV_PATH)) {
            AllEmployee e = parseLine(line);
            if (e != null) list.add(e);
        }
        return list;
    }

    @Override
    public AllEmployee findById(String employeeId) {
        if (employeeId == null) return null;
        for (AllEmployee e : getAllEmployees()) {
            if (e.getEmployeeNumber().trim().equals(employeeId.trim())) return e;
        }
        return null;
    }

    @Override
    public void addEmployee(AllEmployee employee) {
        appendRow(CSV_PATH, CSV_HEADER, employee.toCSVRow());
    }

    @Override
    public void updateEmployee(AllEmployee updated) {
        ObservableList<AllEmployee> all = getAllEmployees();
        List<String> lines = new ArrayList<>();
        lines.add(CSV_HEADER);
        for (AllEmployee e : all) {
            if (e.getEmployeeNumber().equals(updated.getEmployeeNumber())) {
                lines.add(updated.toCSVRow());
            } else {
                lines.add(e.toCSVRow());
            }
        }
        rewriteFile(CSV_PATH, lines);
    }

    @Override
    public void deleteEmployee(String employeeNumber) {
        ObservableList<AllEmployee> all = getAllEmployees();
        List<String> lines = new ArrayList<>();
        lines.add(CSV_HEADER);
        for (AllEmployee e : all) {
            if (!e.getEmployeeNumber().equals(employeeNumber)) {
                lines.add(e.toCSVRow());
            }
        }
        rewriteFile(CSV_PATH, lines);
    }

    @Override
    public Set<String> getExistingEmployeeNumbers() {
        Set<String> ids = new HashSet<>();
        for (AllEmployee e : getAllEmployees()) ids.add(e.getEmployeeNumber());
        return ids;
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    private AllEmployee parseLine(String line) {
        if (line == null || line.isBlank()) return null;
        String[] d = line.split(",", -1);
        if (d.length < 19) return null;
        try {
            return new AllEmployeePublic(
                    d[0].trim(), d[1].trim(), d[2].trim(), d[3].trim(), d[4].trim(),
                    d[5].trim(), d[6].trim(), d[7].trim(), d[8].trim(), d[9].trim(),
                    d[10].trim(), d[11].trim(), d[12].trim(), d[13].trim(), d[14].trim(),
                    d[15].trim(), d[16].trim(), d[17].trim(), d[18].trim());
        } catch (Exception e) {
            System.err.println("[EmployeeDAOImpl] Skipping malformed row: " + line);
            return null;
        }
    }
}
