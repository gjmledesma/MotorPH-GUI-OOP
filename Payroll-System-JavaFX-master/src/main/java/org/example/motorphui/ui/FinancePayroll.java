/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.example.motorphui.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.example.motorphui.model.AllEmployee;
import org.example.motorphui.model.AllEmployeePublic;

/**
 *
 * @author gabrielledesma
 */
public class FinancePayroll {

    @FXML
    private TableView<AllEmployee> emp_table;
    @FXML
    private TableColumn<AllEmployee, String> empNumColumn, lastNameColumn, firstNameColumn, sssColumn, philHealthColumn, tinColumn, pagIbigColumn;
    @FXML
    private final ObservableList<AllEmployee> employeeData = FXCollections.observableArrayList();

    public void initialize() {
        empNumColumn.setCellValueFactory(cellData -> cellData.getValue().employeeNumberProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        sssColumn.setCellValueFactory(cellData -> cellData.getValue().sssProperty());
        philHealthColumn.setCellValueFactory(cellData -> cellData.getValue().philHealthProperty());
        tinColumn.setCellValueFactory(cellData -> cellData.getValue().tinProperty());
        pagIbigColumn.setCellValueFactory(cellData -> cellData.getValue().pagIbigProperty());

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("/org/example/motorphui/data/motorph_employee_data.csv")))) {

            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split(",");
                if (data.length >= 19) {
                    String empNumber = data[0];
                    String lastName = data[1];
                    String firstName = data[2];
                    String birthday = data[3];
                    String address = data[4];
                    String phoneNumber = data[5];
                    String sss = data[6];
                    String philHealth = data[7];
                    String tin = data[8];
                    String pagIbig = data[9];
                    String status = data[10];
                    String position = data[11];
                    String immediateSupervisor = data[12];
                    String basicSalary = data[13];
                    String riceSubsidy = data[14];
                    String phoneAllowance = data[15];
                    String clothingAllowance = data[16];
                    String grossSemiMonthlyRate = data[17];
                    String hourlyRate = data[18];

                    AllEmployee employee = new AllEmployeePublic(
                            empNumber,          // employeeNumber
                            lastName,           // lastName
                            firstName,         // firstName
                            birthday,          // birthday
                            address,           // address
                            phoneNumber,       // phoneNumber
                            sss,              // sss
                            philHealth,       // philHealth
                            tin,              // tin
                            pagIbig,          // pagIbig
                            status,           // status (from data[10])
                            position,         // position
                            immediateSupervisor, // immediateSupervisor (from data[12])
                            basicSalary,      // basicSalary
                            riceSubsidy,      // riceSubsidy
                            phoneAllowance,   // phoneAllowance
                            clothingAllowance, // clothingAllowance
                            data[17],         // grossSemiMonthlyRate
                            hourlyRate        // hourlyRate
                    );

                    employeeData.add(employee);
                }
            }

            emp_table.setItems(employeeData);

        } catch (Exception e) {
            System.out.println("Error loading employee data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generatePayrollForEmployee(AllEmployee employee) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/motorphui/hr_payslip.fxml"));
            Parent root = loader.load();

            FinancePayslip controller = loader.getController();
            controller.setEmployee(employee);

            Stage stage = new Stage();
            stage.setTitle("Payroll Slip");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onGeneratePayroll() {
        AllEmployee selectedEmployee = emp_table.getSelectionModel().getSelectedItem();

        if (selectedEmployee != null) {
            generatePayrollForEmployee(selectedEmployee);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select an employee to generate payroll.");
            alert.showAndWait();
        }
    }

    private double calculateMonthlyHours(String empNumber, String month) {
        double totalHours = 0.0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("/org/example/motorphui/data/motorph_attendance_records.csv")))) {

            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split(",");
                if (data.length >= 7 && data[0].equals(empNumber) && data[4].equalsIgnoreCase(month)) {
                    String login = data[5];
                    String logout = data[6];

                    double loginTime = parseTimeToDecimal(login);
                    double logoutTime = parseTimeToDecimal(logout);
                    totalHours += (logoutTime - loginTime);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading attendance file: " + e.getMessage());
            e.printStackTrace();
        }

        return totalHours;
    }

    private double parseTimeToDecimal(String time) {
        try {
            String[] parts = time.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            return hours + (minutes / 60.0);
        } catch (Exception e) {
            System.out.println("Invalid time format: " + time);
            return 0.0;
        }
    }
}
