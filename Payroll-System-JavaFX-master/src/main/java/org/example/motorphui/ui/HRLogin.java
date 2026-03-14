package org.example.motorphui.ui;

import javafx.event.ActionEvent;
import org.example.motorphui.dao.AuthenticationDAO;
import org.example.motorphui.model.HRUser;
import org.example.motorphui.session.SessionManager;

/**
 * Login controller for HR users.
 *
 * <h3>What changed (backend only)</h3>
 * {@link #onLoginSuccess} is now overridden to construct an {@link HRUser}
 * model and store it in {@link SessionManager} before navigating to the
 * dashboard.  The FXML, the dashboard, and all other HR controllers are
 * completely unchanged.
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

    /**
     * Builds an {@link HRUser} model and stores it in the session before
     * loading the HR dashboard.
     *
     * <p>POLYMORPHISM — overrides the hook in {@link BaseLoginController};
     * the template method {@code handleLoginButton()} calls this automatically
     * after credentials are verified.</p>
     */
    @Override
    protected void onLoginSuccess(ActionEvent event) {
        String username = username_field.getText().trim();

        // Construct the HR user model and register it in the session
        HRUser hrUser = new AuthenticationDAO().getHRUser(username, password_field.getText());
        if (hrUser != null) {
            SessionManager.getInstance().setCurrentUser(hrUser);
        }

        // Delegate dashboard navigation to the base-class implementation
        super.onLoginSuccess(event);
    }
}
