package org.example.motorphui.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Abstract base controller for all login screens (HR, Finance, IT Admin, Employee).
 *
 * Implements the Template Method design pattern:
 *   handleLoginButton()  — the template; defines the login flow.
 *   performAuthentication() — the hook; subclasses supply the actual auth logic.
 *   getDashboardFxml()   — the hook; subclasses supply the FXML path to load on success.
 *   getDashboardTitle()  — the hook; subclasses supply the window title.
 *
 * OOP PRINCIPLES DEMONSTRATED:
 *   ABSTRACTION   — Abstract class with abstract methods for subclass-specific logic.
 *   INHERITANCE   — HRLogin, FinanceLogin, ITAdminLogin, EmployeeLogin all extend this.
 *   ENCAPSULATION — Shared FXML fields are protected; helpers are private.
 *   POLYMORPHISM  — performAuthentication() is overridden differently by each subclass.
 */
public abstract class BaseLoginController {

    // ── Shared FXML fields ────────────────────────────────────────────────────
    @FXML protected Button    login_button;
    @FXML protected Label     back_label;
    @FXML protected TextField username_field;
    @FXML protected PasswordField password_field;
    @FXML protected TextField visible_password_field;
    @FXML protected CheckBox  show_password_check;

    // ── FXML initializer — shared password toggle logic ───────────────────────
    @FXML
    protected void initialize() {
        password_field.textProperty().addListener((obs, ov, nv) -> {
            if (!visible_password_field.isFocused()) visible_password_field.setText(nv);
        });
        visible_password_field.textProperty().addListener((obs, ov, nv) -> {
            if (!password_field.isFocused()) password_field.setText(nv);
        });
        show_password_check.setOnAction(event -> {
            boolean show = show_password_check.isSelected();
            visible_password_field.setVisible(show);
            visible_password_field.setManaged(show);
            password_field.setVisible(!show);
            password_field.setManaged(!show);
        });
    }

    // ── Template method (POLYMORPHISM — overridden by each subclass) ──────────

    /**
     * Returns true if the supplied credentials are valid for this role.
     * POLYMORPHISM — each subclass overrides with its own authentication call.
     */
    protected abstract boolean performAuthentication(String username, String password);

    /**
     * Returns the classpath FXML path of the dashboard to load on success.
     * POLYMORPHISM — overridden to point to HR / Finance / IT / Employee dashboard.
     */
    protected abstract String getDashboardFxml();

    /**
     * Returns the window title to set after a successful login.
     */
    protected abstract String getDashboardTitle();

    // ── Shared login handler (Template Method) ────────────────────────────────

    @FXML
    protected void handleLoginButton(ActionEvent event) {
        String username = username_field.getText();
        String password = password_field.getText();

        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "All fields are required.");
            return;
        }

        if (performAuthentication(username, password)) {
            onLoginSuccess(event);
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed",
                      "Invalid credentials. Please try again.");
        }
    }

    /**
     * Called by handleLoginButton when authentication succeeds.
     * Subclasses may override to perform additional work (e.g. loading session data).
     */
    protected void onLoginSuccess(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(getDashboardFxml()));
            Parent root = loader.load();

            Stage stage = (Stage) login_button.getScene().getWindow();
            stage.setMinWidth(1200);
            stage.setMinHeight(700);
            stage.setWidth(1200);
            stage.setHeight(700);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading dashboard.");
        }
    }

    // ── Shared back-navigation handler ────────────────────────────────────────

    @FXML
    protected void handleBackClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/motorphui/landing_page.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) back_label.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Alert helper ──────────────────────────────────────────────────────────

    protected void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
