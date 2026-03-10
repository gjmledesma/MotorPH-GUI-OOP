package org.example.motorphui.ui;

import org.example.motorphui.dao.AuthenticationDAO;

/**
 * Login controller for HR users.
 *
 * OOP PRINCIPLES DEMONSTRATED:
 *   INHERITANCE   — Extends BaseLoginController instead of AuthenticationDAO.
 *   POLYMORPHISM  — Overrides all three abstract methods from BaseLoginController
 *                   to supply HR-specific authentication and navigation.
 */
public class HRLogin extends BaseLoginController {

    @Override
    protected boolean performAuthentication(String username, String password) {
        return new AuthenticationDAO().authenticateHR(username, password);
    }

    @Override
    protected String getDashboardFxml() {
        return "/org/example/motorphui/hr_dashboard.fxml";
    }

    @Override
    protected String getDashboardTitle() {
        return "MotorPH HR Dashboard";
    }
}
