package org.example.motorphui.ui;

import org.example.motorphui.dao.AuthenticationDAO;

/**
 * Login controller for Finance users.
 *
 * OOP PRINCIPLES DEMONSTRATED:
 *   INHERITANCE   — Extends BaseLoginController instead of AuthenticationDAO.
 *   POLYMORPHISM  — Overrides abstract methods with Finance-specific values.
 */
public class FinanceLogin extends BaseLoginController {

    @Override
    protected boolean performAuthentication(String username, String password) {
        return new AuthenticationDAO().authenticateFinance(username, password);
    }

    @Override
    protected String getDashboardFxml() {
        return "/org/example/motorphui/finance_dashboard.fxml";
    }

    @Override
    protected String getDashboardTitle() {
        return "MotorPH Finance Dashboard";
    }
}
