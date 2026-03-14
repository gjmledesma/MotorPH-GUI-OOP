package org.example.motorphui.service;

import org.example.motorphui.dao.IEmployeeDAO;
import org.example.motorphui.dao.EmployeeDAOImpl;
import org.example.motorphui.model.AllEmployee;
import org.example.motorphui.util.EmployeeValidator;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Set;

/**
 * Business-logic implementation for employee management.
 * Delegates persistence to an {@link IEmployeeDAO} instance.
 */
public class EmployeeServiceImpl implements IEmployeeService {

    private final IEmployeeDAO employeeDAO;

    /** Default constructor uses the CSV-backed implementation. */
    public EmployeeServiceImpl() {
        this.employeeDAO = new EmployeeDAOImpl();
    }

    /** Injectable constructor for testing or alternative DAO implementations. */
    public EmployeeServiceImpl(IEmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    @Override
    public ObservableList<AllEmployee> getAllEmployees() {
        return employeeDAO.getAllEmployees();
    }

    @Override
    public AllEmployee findById(String employeeId) {
        return employeeDAO.findById(employeeId);
    }

    @Override
    public List<String> addEmployee(AllEmployee employee) {
        List<String> errors = EmployeeValidator.validateAll(
                employee.getEmployeeNumber(), employee.getLastName(),
                employee.getFirstName(), employee.getBirthday(),
                employee.getAddress(), employee.getPhoneNumber(),
                employee.getSss(), employee.getPhilHealth(),
                employee.getTin(), employee.getPagIbig(),
                employee.getStatus(), employee.getPosition(),
                employee.getImmediateSupervisor(), employee.getBasicSalary(),
                employee.getRiceSubsidy(), employee.getPhoneAllowance(),
                employee.getClothingAllowance(), employee.getHourlyRate(),
                true, employeeDAO.getExistingEmployeeNumbers());
        if (errors.isEmpty()) {
            employeeDAO.addEmployee(employee);
        }
        return errors;
    }

    @Override
    public List<String> updateEmployee(AllEmployee employee) {
        List<String> errors = EmployeeValidator.validateAll(
                employee.getEmployeeNumber(), employee.getLastName(),
                employee.getFirstName(), employee.getBirthday(),
                employee.getAddress(), employee.getPhoneNumber(),
                employee.getSss(), employee.getPhilHealth(),
                employee.getTin(), employee.getPagIbig(),
                employee.getStatus(), employee.getPosition(),
                employee.getImmediateSupervisor(), employee.getBasicSalary(),
                employee.getRiceSubsidy(), employee.getPhoneAllowance(),
                employee.getClothingAllowance(), employee.getHourlyRate(),
                false, null);
        if (errors.isEmpty()) {
            employeeDAO.updateEmployee(employee);
        }
        return errors;
    }

    @Override
    public void deleteEmployee(String employeeNumber) {
        employeeDAO.deleteEmployee(employeeNumber);
    }

    @Override
    public Set<String> getExistingEmployeeNumbers() {
        return employeeDAO.getExistingEmployeeNumbers();
    }
}
