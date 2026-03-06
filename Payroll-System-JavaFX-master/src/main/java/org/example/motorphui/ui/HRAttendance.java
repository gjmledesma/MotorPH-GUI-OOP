package org.example.motorphui.ui;

import org.example.motorphui.dao.AllEmployeeDAO;
import org.example.motorphui.model.AllEmployee;
import org.example.motorphui.service.AuthenticationService;
import org.example.motorphui.service.SalaryTaxCalculatorService;
import org.example.motorphui.service.AttendanceService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class HRAttendance {
    
    @FXML
    private AnchorPane root;
    
    @FXML
    private TableView<AllEmployee> attendance_table;
    
    // Declare columns for each property in Employee class
    @FXML
    private TableColumn<AllEmployee, String> empNumColumn;
    @FXML
    private TableColumn<AllEmployee, String> lastNameColumn;
    @FXML
    private TableColumn<AllEmployee, String> firstNameColumn;
    
}
