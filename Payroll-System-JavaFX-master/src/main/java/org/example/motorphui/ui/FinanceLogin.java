package org.example.motorphui.ui;

import javafx.event.ActionEvent;
import org.example.motorphui.dao.AuthenticationDAO;
import org.example.motorphui.model.FinanceUser;
import org.example.motorphui.session.SessionManager;

/**
 * Login controller for Finance users.
 *
 * <h3>What changed (backend only)</h3>
 * <p>{@link #onLoginSuccess} is now overridden to construct a {@link FinanceUser}
 * model and store it in {@link SessionManager} before navigating to the
 * dashboard.  The FXML, the dashboard, and all other Finance controllers are
 * completely unchanged.</p>
 *
 * <h3>OOP principles</h3>
 * <ul>
 *   <li><b>Polymorphism</b> — overrides the {@code onLoginSuccess} hook
 *       defined in {@link BaseLoginController} (Template Method pattern).</li>
 *   <li><b>Encapsulation</b> — the {@code FinanceUser} model encapsulates the
 *       Finance user's identity; raw credentials never leave this method.</li>
 * </ul>
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

    /**
     * Builds a {@link FinanceUser} model and stores it in the session before
     * loading the Finance dashboard.
     *
     * <p>POLYMORPHISM — overrides the hook in {@link BaseLoginController}.</p>
     */
    @Override
    protected void onLoginSuccess(ActionEvent event) {
        String username = username_field.getText().trim();

        // Construct the Finance user model and register it in the session
        FinanceUser financeUser = new AuthenticationDAO()
                .getFinanceUser(username, password_field.getText());
        if (financeUser != null) {
            SessionManager.getInstance().setCurrentUser(financeUser);
        }

        // Delegate dashboard navigation to the base-class implementation
        super.onLoginSuccess(event);
    }
}
