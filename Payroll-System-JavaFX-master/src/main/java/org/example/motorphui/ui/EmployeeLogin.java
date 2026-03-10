package org.example.motorphui.ui;

import org.example.motorphui.dao.AuthenticationDAO;
import org.example.motorphui.model.AllEmployee;
import org.example.motorphui.session.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Login controller for Employee users.
 *
 * Extends BaseLoginController and adds the Employee-ID field that is unique
 * to the employee login screen.
 *
 * OOP PRINCIPLES DEMONSTRATED:
 *   INHERITANCE   — Extends BaseLoginController instead of AuthenticationDAO.
 *   POLYMORPHISM  — Overrides performAuthentication() with three-argument
 *                   employee authentication (employee ID + username + password),
 *                   and overrides onLoginSuccess() to load the employee session.
 *   ENCAPSULATION — employeeid_field is private, accessed only within this class.
 */
public class EmployeeLogin extends BaseLoginController {

    @FXML
    private TextField employeeid_field;

    // ── Abstract method implementations ───────────────────────────────────────

    @Override
    protected boolean performAuthentication(String username, String password) {
        String empId = employeeid_field.getText().trim();
        if (empId.isEmpty()) return false;
        return AuthenticationDAO.authenticate(empId, username, password);
    }

    @Override
    protected String getDashboardFxml() {
        return "/org/example/motorphui/employee_dashboard.fxml";
    }

    @Override
    protected String getDashboardTitle() {
        return "MotorPH Employee Dashboard";
    }

    // ── Override initialize to add Employee-ID blank check ────────────────────

    @FXML
    @Override
    protected void initialize() {
        super.initialize();
        // nothing extra needed — the employee-ID check is handled in
        // handleLoginButton() via the overridden performAuthentication()
    }

    // ── Override handleLoginButton to validate employee ID field too ──────────

    @FXML
    @Override
    protected void handleLoginButton(ActionEvent event) {
        String empId    = employeeid_field.getText().trim();
        String username = username_field.getText().trim();
        String password = password_field.getText();

        if (empId.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "All fields are required.");
            return;
        }

        if (AuthenticationDAO.authenticate(empId, username, password)) {
            AllEmployee employee = new AuthenticationDAO().getEmployeeData(empId);
            SessionManager.getInstance().setCurrentEmployee(employee);
            loadEmployeeDashboard(employee, event);
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed",
                      "Invalid credentials. Please try again.");
        }
    }

    // ── Private helper ────────────────────────────────────────────────────────

    private void loadEmployeeDashboard(AllEmployee employee, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/motorphui/employee_dashboard.fxml"));
            Parent root = loader.load();

            EmployeeDashboard dashboardController = loader.getController();
            dashboardController.loadProfile(employee);

            Stage stage = (Stage) login_button.getScene().getWindow();
            stage.setMinWidth(1200);
            stage.setMinHeight(700);
            stage.setWidth(1200);
            stage.setHeight(700);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading employee dashboard.");
        }
    }
}
