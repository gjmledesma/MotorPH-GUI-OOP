package org.example.motorphui.ui;

import org.example.motorphui.model.LeaveRequest;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;

import java.awt.event.MouseEvent;

public class LeaveManagement {

    @FXML
    private Button approve_button, deny_button;
    @FXML
    private TableView<LeaveRequest> leave_table; // TableView to display the leave requests (LeaveRequest class)

    // This method will be called when the Leave Management tab is opened.
    // It should load all leave requests from the backend and display them in the TableView.
    // Provide a method to fetch the leave requests, e.g., `getLeaveRequests()`
    // After fetching the data, populate the TableView with the leave requests.
    // TODO: Fetch data for leave requests and populate the TableView.

    // TODO: Please take note that in the LeaveID column we should be able to SELECT, when selected > approve/deny

    // Approve the selected leave request when the "Approve" button is clicked.
    // The backend should be updated with the new status of the leave request (approved).
    // TODO: Update the leave request status in the backend database (csv)

    // Deny the selected leave request when the "Deny" button is clicked.
    // A dialog box should open asking for remarks, which will be stored along with the status.
    // TODO: Update the leave request status and remarks in the backend database (csv)
}
