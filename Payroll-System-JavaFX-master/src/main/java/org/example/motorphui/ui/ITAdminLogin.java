package org.example.motorphui.ui;

import javafx.event.ActionEvent;
import org.example.motorphui.dao.AuthenticationDAO;
import org.example.motorphui.model.ITUser;
import org.example.motorphui.session.SessionManager;

/**
 * Login controller for IT Admin users.
 *
 * <h3>What changed (backend only)</h3>
 * <p>{@link #onLoginSuccess} is now overridden to construct an {@link ITUser}
 * model and store it in {@link SessionManager} before navigating to the
 * dashboard.  The FXML, the dashboard, and all other IT controllers are
 * completely unchanged.</p>
 *
 * <h3>OOP principles</h3>
 * <ul>
 *   <li><b>Polymorphism</b> — overrides the {@code onLoginSuccess} hook
 *       defined in {@link BaseLoginController} (Template Method pattern).</li>
 *   <li><b>Encapsulation</b> — the {@code ITUser} model encapsulates the IT
 *       admin's identity; raw credentials never leave this method.</li>
 * </ul>
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

    /**
     * Builds an {@link ITUser} model and stores it in the session before
     * loading the IT Admin dashboard.
     *
     * <p>POLYMORPHISM — overrides the hook in {@link BaseLoginController}.</p>
     */
    @Override
    protected void onLoginSuccess(ActionEvent event) {
        String username = username_field.getText().trim();

        // Construct the IT user model and register it in the session
        ITUser itUser = new AuthenticationDAO()
                .getITUser(username, password_field.getText());
        if (itUser != null) {
            SessionManager.getInstance().setCurrentUser(itUser);
        }

        // Delegate dashboard navigation to the base-class implementation
        super.onLoginSuccess(event);
    }
}
