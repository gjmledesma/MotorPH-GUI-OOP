package org.example.motorphui.session;

import org.example.motorphui.model.AllEmployee;

/**
 * Application-wide singleton that stores the employee who is currently logged in.
 *
 * Usage — after a successful login:
 *     SessionManager.getInstance().setCurrentEmployee(
 *         AuthenticationDAO.getEmployeeData(empId));
 *
 * Anywhere else in the app:
 *     AllEmployee emp = SessionManager.getInstance().getCurrentEmployee();
 */
public class SessionManager {

    private static final SessionManager INSTANCE = new SessionManager();

    private AllEmployee currentEmployee;

    private SessionManager() {}

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public AllEmployee getCurrentEmployee() {
        return currentEmployee;
    }

    public void setCurrentEmployee(AllEmployee employee) {
        this.currentEmployee = employee;
    }

    public void clearSession() {
        this.currentEmployee = null;
    }
}
