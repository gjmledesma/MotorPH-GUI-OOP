package org.example.motorphui.ui;

import org.example.motorphui.dao.AuthenticationDAO;
import org.example.motorphui.model.AllEmployee;
import org.example.motorphui.model.EmployeeUser;
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
 * <h3>What changed (backend only)</h3>
 * <p>{@link #handleLoginButton} now also constructs an {@link EmployeeUser}
 * model and stores it in {@link SessionManager} alongside the existing
 * {@code AllEmployee} reference.  Every existing call-site ({@code EmployeeProfile},
 * {@code EmployeeViewSalary}, etc.) that reads
 * {@code SessionManager.getInstance().getCurrentEmployee()} continues to work
 * without any change.</p>
 *
 * <h3>OOP principles</h3>
 * <ul>
 *   <li><b>Polymorphism</b> — overrides hooks from {@link BaseLoginController}
 *       (Template Method pattern).</li>
 *   <li><b>Encapsulation</b> — raw credentials are used only here; the rest of
 *       the app interacts with the typed {@code EmployeeUser} / {@code AllEmployee}
 *       models through {@code SessionManager}.</li>
 *   <li><b>Composition</b> — {@code EmployeeUser} <em>wraps</em> the
 *       {@code AllEmployee} rather than replacing it, so the dual-session fields
 *       stay consistent.</li>
 * </ul>
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

    // ── Initialize ────────────────────────────────────────────────────────────

    @FXML
    @Override
    protected void initialize() {
        super.initialize();
    }

    // ── Login handler — stores both AllEmployee and EmployeeUser in session ───

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
            // ── 1. Fetch full employee data record (existing behaviour) ───────
            AllEmployee employee = new AuthenticationDAO().getEmployeeData(empId);

            // ── 2. Store AllEmployee for existing screens (UNCHANGED path) ────
            SessionManager.getInstance().setCurrentEmployee(employee);

            // ── 3. Build and store EmployeeUser (new SystemUser session) ──────
            //    EmployeeUser wraps the same AllEmployee object, so both session
            //    references always point at the same underlying data.
            EmployeeUser employeeUser = new EmployeeUser(employee);
            SessionManager.getInstance().setCurrentUser(employeeUser);

            // ── 4. Navigate to the employee dashboard (unchanged) ─────────────
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
