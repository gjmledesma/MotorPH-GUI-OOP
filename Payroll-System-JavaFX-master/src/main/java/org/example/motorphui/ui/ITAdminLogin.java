package org.example.motorphui.ui;

import org.example.motorphui.dao.AuthenticationDAO;

/**
 * Login controller for IT Admin users.
 *
 * OOP PRINCIPLES DEMONSTRATED:
 *   INHERITANCE   — Extends BaseLoginController instead of AuthenticationDAO.
 *   POLYMORPHISM  — Overrides abstract methods with IT-specific values.
 */
public class ITAdminLogin extends BaseLoginController {

    @Override
    protected boolean performAuthentication(String username, String password) {
        return new AuthenticationDAO().authenticateIT(username, password);
    }

    @Override
    protected String getDashboardFxml() {
        return "/org/example/motorphui/it_dashboard.fxml";
    }

    @Override
    protected String getDashboardTitle() {
        return "MotorPH IT Admin Dashboard";
    }
}
