package org.example.motorphui.ui;

import org.example.motorphui.dao.EmployeeDAOImpl;
import org.example.motorphui.dao.IEmployeeDAO;
import org.example.motorphui.model.AllEmployee;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controller for the Delete Employee confirmation dialog.
 *
 * Previously this class was completely empty.  It now provides a confirmation
 * form that HR users complete before a deletion is committed.
 */
public class HRDeleteEmployee {

    @FXML private Label      empNameLabel;
    @FXML private Label      empNumberLabel;
    @FXML private Label      empPositionLabel;
    @FXML private Button     confirmDeleteButton;
    @FXML private Button     cancelButton;

    // ── Private state (ENCAPSULATION) ────────────────────────────────────────
    private AllEmployee     employee;
    private HREmployeeView  parentController;
    private final IEmployeeDAO employeeDAO = new EmployeeDAOImpl();

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Populates the confirmation form with the employee to be deleted.
     */
    public void setEmployee(AllEmployee employee) {
        this.employee = employee;
        if (empNameLabel     != null) empNameLabel.setText(employee.getFullName());
        if (empNumberLabel   != null) empNumberLabel.setText(employee.getEmployeeNumber());
        if (empPositionLabel != null) empPositionLabel.setText(employee.getPosition());
    }

    public void setParentController(HREmployeeView parent) {
        this.parentController = parent;
    }

    // ── Event handlers ────────────────────────────────────────────────────────

    @FXML
    private void handleConfirmDelete(ActionEvent event) {
        if (employee == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Permanent Deletion");
        confirm.setHeaderText("Delete " + employee.getFullName() + "?");
        confirm.setContentText("Employee #" + employee.getEmployeeNumber()
                + " will be permanently removed. This action cannot be undone.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                employeeDAO.deleteEmployee(employee.getEmployeeNumber());

                if (parentController != null) parentController.refreshTable();

                showInfo("Employee Deleted",
                         employee.getFullName() + " has been removed successfully.");
                closeWindow(event);
            }
        });
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow(event);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showInfo(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}
