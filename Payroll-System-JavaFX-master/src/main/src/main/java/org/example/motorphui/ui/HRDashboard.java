package org.example.motorphui.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HRDashboard {

    @FXML
    private AnchorPane contentPane;
    @FXML
    private Button emp_button, payroll_button, leave_button, attendance_button;
    @FXML
    private Button logout_button;

    @FXML
    public void initialize() {
        loadView("/org/example/motorphui/hr_employee_view.fxml");
        setActiveButton(emp_button);
    }

    @FXML
    private void onEmployeesClicked() {
        loadView("/org/example/motorphui/hr_employee_view.fxml");
        setActiveButton(emp_button);
    }

    @FXML
    private void onPayrollClicked() {
        loadView("/org/example/motorphui/hr_payroll.fxml");
        setActiveButton(payroll_button);
    }

    @FXML
    private void onLeaveClicked() {
        loadView("/org/example/motorphui/leave_management.fxml");
        setActiveButton(leave_button);
    }

    @FXML
    private void onAttendanceClicked() {
        loadView("/org/example/motorphui/hr_attendance.fxml");
        setActiveButton(attendance_button);
    }

    private void loadView(String fxml) {
        try {
            AnchorPane pane = FXMLLoader.load(getClass().getResource(fxml));
            contentPane.getChildren().setAll(pane);

            // Anchor the pane to all sides of contentPane to fill it entirely
            AnchorPane.setTopAnchor(pane, 0.0);
            AnchorPane.setBottomAnchor(pane, 0.0);
            AnchorPane.setLeftAnchor(pane, 0.0);
            AnchorPane.setRightAnchor(pane, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setActiveButton(Button active) {
        emp_button.getStyleClass().remove("menu-button-active");
        payroll_button.getStyleClass().remove("menu-button-active");
        leave_button.getStyleClass().remove("menu-button-active");
        logout_button.getStyleClass().remove("menu-button-active");
        attendance_button.getStyleClass().remove("menu-button-active");

        if (!active.getStyleClass().contains("menu-button-active")) {
            active.getStyleClass().add("menu-button-active");
        }
    }

    @FXML
    private void onLogoutClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/motorphui/landing_page.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) logout_button.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
